package org.acme.controller.auth;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.repository.auth.AuthEnt;
import org.acme.repository.auth.register.RegisterRepository;

@Path("/api/v1/register")
public class RegisterController {

    @Inject
    RegisterRepository registerRepository;

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AuthEnt registerUser(@Valid AuthEnt AuthEnt) {
        return registerRepository.register(AuthEnt);
    }
}