package ru.iot_cloud.user;

import jakarta.inject.Inject;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.iot_cloud.data.User;
import ru.iot_cloud.security.TokenService;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    TokenService service;

    @Inject
    EmailService emailService;

    @POST
    @Path("/register")
    @Transactional
    public Response register(User user) throws MessagingException {
        if (User.find("email", user.email).firstResult() != null) {
            return Response.status(Response.Status.CONFLICT).entity("Пользователь с таким email уже существует").build();
        }

        String confirmationCode = generateCode();
        user.setConfirmationCode(confirmationCode);
        user.isActive = false;
        user.persist();

                emailService.sendEmail(user.email, "Подтверждение регистрации",
                "Добро пожаловать в IoT Cloud! Ваш код подтверждения: " + confirmationCode);

        return Response.status(Response.Status.CREATED).entity("Пользователь зарегистрирован. Проверьте почту для подтверждения.").build();
    }

    @POST
    @Path("/conform-email")
    @Transactional
    public Response confirmEmail(@QueryParam("email") String email, @QueryParam("code") String code) {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Пользователь не найден").build();
        }

        if (user.confirmationCode.equals(code)) {
            user.isActive = true;
            user.confirmationCode = null;
            user.persist();
            return Response.ok("Email подтвержден. Теперь вы можете войти в систему.").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Неверный код подтверждения").build();
        }
    }

    @POST
    @Path("/request-code")
    @Transactional
    public Response requestCode(@QueryParam("email") String email) throws MessagingException {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Пользователь не найден").build();
        }

        String code = generateCode();
        OneTimeCode oneTimeCode = new OneTimeCode();
        oneTimeCode.userId = user.id;
        oneTimeCode.code = code;
        oneTimeCode.expirationTime = LocalDateTime.now().plusMinutes(15);
        oneTimeCode.persist();

        emailService.sendEmail(user.email, "Код для входа", "Ваш код для входа на платформу IoT Cloud: " + code);

        return Response.ok("Код отправлен на вашу почту").build();
    }

    @POST
    @Path("/login")
    @Transactional
    public Response login(@QueryParam("email") String email, @QueryParam("code") String code, @QueryParam("deviceId") String deviceId) {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Пользователь не найден").build();
        }

        if (!user.isActive) {
            return Response.status(Response.Status.FORBIDDEN).entity("Аккаунт не активирован. Подтвердите email.").build();
        }

        OneTimeCode oneTimeCode = OneTimeCode.find("userId = ?1 and code = ?2 and expirationTime > ?3",
                user.id, code, LocalDateTime.now()).firstResult();

        if (oneTimeCode == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Код недействителен или просрочен").build();
        }

        oneTimeCode.delete();
        String newDeviceHash = generateDeviceHash();
        UserDeviceHash userDeviceHash = new UserDeviceHash();
        userDeviceHash.userId = user.id;
        userDeviceHash.deviceId = deviceId;
        userDeviceHash.hash = newDeviceHash;
        userDeviceHash.persist();

        user.updateLastLoginDate();
        user.persist();

        String token = service.generateUserToken(user.email, newDeviceHash);

        return Response.ok(new LoginResponse(token, newDeviceHash)).build();
    }

    @POST
    @Path("/verify-device")
    @Transactional
    public Response verifyDeviceHash(@QueryParam("userId") Long userId, @QueryParam("deviceId") String deviceId, @QueryParam("hash") String hash) {
        UserDeviceHash userDeviceHash = UserDeviceHash.find("userId = ?1 and deviceId = ?2 and hash = ?3", userId, deviceId, hash).firstResult();

        if (userDeviceHash != null) {
            String newDeviceHash = generateDeviceHash();
            userDeviceHash.hash = newDeviceHash;
            userDeviceHash.persist();
            return Response.ok(new DeviceVerificationResponse(newDeviceHash)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
    }

    @DELETE
    @Path("/delete-account")
    @Transactional
    public Response deleteAccount(@QueryParam("userId") Long userId) {
        User user = User.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Пользователь не найден").build();
        }

        LocalDateTime accountCreationTime = user.getRegistrationDate();
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(accountCreationTime.plusDays(2))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Удаление аккаунта возможно только через 48 часов после создания").build();
        }

        user.delete();

        return Response.ok("Аккаунт удалён").build();
    }

    @PUT
    @Path("/change-email")
    @Transactional
    public Response changeEmail(@QueryParam("userId") Long userId, @QueryParam("newEmail") String newEmail) throws MessagingException {
        User user = User.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Пользователь не найден").build();
        }

        LocalDateTime accountCreationTime = user.registrationDate;
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(accountCreationTime.plusHours(24))) {
            return Response.status(Response.Status.FORBIDDEN).entity("Изменение почты возможно только через 24 часа после создания аккаунта").build();
        }

        if (User.find("email", newEmail).firstResult() != null) {
            return Response.status(Response.Status.CONFLICT).entity("Пользователь с таким email уже существует").build();
        }

        user.email = newEmail;
        user.persist();

        String confirmationCode = generateCode();
        user.setConfirmationCode(confirmationCode);
        emailService.sendEmail(newEmail, "Подтверждение нового email",
                "Ваш код подтверждения для смены email: " + confirmationCode);

        return Response.ok("Email изменён. Подтвердите новый email").build();
    }


    private String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String generateDeviceHash() {
        return UUID.randomUUID().toString();
    }
}