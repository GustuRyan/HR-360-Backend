package org.acme.controller.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.smallrye.jwt.build.Jwt;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.model.ProfileResponse;
import org.acme.model.TokenResponse;
import org.acme.repository.auth.AuthEnt;
import org.acme.repository.auth.login.LoginRepository;

import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
@Path("api/v1/user")
public class LoginController {

    @Inject
    LoginRepository loginRepository;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(AuthEnt loginRequest) {
        AuthEnt user = loginRepository.findByEmail(loginRequest.email());

        if (user == null || !BCrypt.verifyer().verify(loginRequest.password().toCharArray(), user.password()).verified) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        Set<String> roles = new HashSet<>();
        if (user.userStatus() != null && user.userStatus() == 1) {
            roles.add("user");
        }
        if (user.adminStatus() == 1) {
            roles.add("admin");
        }

        String token = Jwt.upn(String.valueOf(user.userId()))
                .groups(roles)
                .claim("userId", user.userId())
                .claim("employeeId", user.employeeId())
                .expiresIn(86400)
                .sign();

        return Response.ok(new TokenResponse(token)).build();
    }

    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user", "admin"})
    public ProfileResponse userProfile() {
        Long userId = Long.parseLong(jwt.getClaim("userId").toString());
        Long employeeId = Long.parseLong(jwt.getClaim("employeeId").toString());

        return new ProfileResponse(userId, employeeId, "Profile data fetched successfully");
    }
}
