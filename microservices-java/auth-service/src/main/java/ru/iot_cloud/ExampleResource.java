package ru.iot_cloud;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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
    @Path("/user")
    @RolesAllowed({Roles.USER, Roles.SERVICE})
    public Response getMe() {
        String userEmail = securityContext.getUserPrincipal().getName();
        User user = User.find("email", userEmail).firstResult();
        if (user != null) {
            user.updateLastLoginDate();
            return Response.ok(user).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
        }
    }

    @GET
    @Path("/admin")
    @RolesAllowed(Roles.ADMIN)
    public Response adminTest() {
        return Response.ok("Access granted to admin area").build();
    }

    @GET
    @Path("/void")
    @DenyAll
    public Response nothing() {
        // This method should never be executed due to @DenyAll
        return Response.status(Response.Status.FORBIDDEN).entity("Access denied").build();
    }

    @GET
    @Path("/public")
    public Response publicEndpoint() {
        return Response.ok("This is a public endpoint").build();
    }
}
