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
    public User register(User user) {
        user.persist();
        return user;
    }

    @POST
    @Path("/request-code")
    @Transactional
    public Response requestCode(@QueryParam("email") String email) throws MessagingException {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            throw new WebApplicationException(Response.status(404).entity("User not found").build());
        }
        String confirmationCode = generateDeviceHash();
        user.setConfirmationCode(confirmationCode);
        user.persist();

        String code = generateCode();
        OneTimeCode oneTimeCode = new OneTimeCode();
        oneTimeCode.userId = user.id;
        oneTimeCode.code = code;
        oneTimeCode.expirationTime = LocalDateTime.now().plusMinutes(15);

        emailService.sendEmail(user.email, "Никому не говори код", "Твой код для входа на платформу IoT Cloud: " + code);

        return Response.ok("Код отправлен на твою почту").build();
    }

    @POST
    @Path("/login")
    @Transactional
    public Response login(@QueryParam("email") String email, @QueryParam("code") String code, @QueryParam("deviceId") String deviceId) {
        User user = User.find("email", email).firstResult();
        if (user == null) {
            throw new WebApplicationException(Response.status(404).entity("Пользователь не найден").build());
        }

        OneTimeCode oneTimeCode = OneTimeCode.find("userId = ?1 and code = ?2 and expirationTime > ?3",
                user.id, code, LocalDateTime.now()).firstResult();

        if (oneTimeCode == null) {
            throw new WebApplicationException(Response.status(401).entity("Код недействителен или просрочен").build());
        }

        oneTimeCode.delete();

        String newDeviceHash = generateDeviceHash();
        UserDeviceHash userDeviceHash = new UserDeviceHash();
        userDeviceHash.userId = user.id;
        userDeviceHash.deviceId = deviceId;
        userDeviceHash.hash = newDeviceHash;
        userDeviceHash.persist();

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

    private String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    private String generateDeviceHash() {
        return UUID.randomUUID().toString();
    }
}