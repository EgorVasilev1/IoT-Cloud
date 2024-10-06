package ru.iot_cloud;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import ru.iot_cloud.data.User;
import ru.iot_cloud.security.Roles;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
public class ExampleResource {

    @Inject
    SecurityContext securityContext;

    @GET
    @Path("/")
    @RolesAllowed({Roles.USER, Roles.SERVICE})
    @Transactional
    public Response getMe() {
        String userEmail = securityContext.getUserPrincipal().getName();
        User user = User.find("email", userEmail).firstResult();
        if (user != null) {
            if (!user.isActive) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("Аккаунт не активирован. Пожалуйста, подтвердите ваш email.")
                        .build();
            }
            user.updateLastLoginDate();
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Пользователь не найден")
                    .build();
        }
    }

    @GET
    @Path("/admin")
    @RolesAllowed(Roles.ADMIN)
    public Response adminTest() {
        String adminEmail = securityContext.getUserPrincipal().getName();
        return Response.ok("Доступ к админ-панели предоставлен для " + adminEmail).build();
    }

    @GET
    @Path("/void")
    @DenyAll
    public Response nothing() {
        // Этот метод никогда не должен выполняться из-за @DenyAll
        return Response.status(Response.Status.FORBIDDEN)
                .entity("Доступ запрещен")
                .build();
    }

    @GET
    @Path("/public")
    @PermitAll
    public Response publicEndpoint() {
        return Response.ok("Это публичный эндпоинт").build();
    }

    @GET
    @Path("/status")
    @PermitAll
    public Response getStatus() {
        if (securityContext.getUserPrincipal() != null) {
            String userEmail = securityContext.getUserPrincipal().getName();
            String role = securityContext.isUserInRole(Roles.ADMIN) ? "админ" :
                    securityContext.isUserInRole(Roles.SERVICE) ? "сервис" : "пользователь";
            return Response.ok("Вы вошли как " + userEmail + " с ролью: " + role).build();
        } else {
            return Response.ok("Вы не авторизованы").build();
        }
    }
}
