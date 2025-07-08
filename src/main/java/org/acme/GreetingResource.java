package org.acme;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/hello")
public class GreetingResource {

    @Inject
    JsonWebToken jwt;

    @GET
    @RolesAllowed({"admin"})
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST " + jwt.getClaim("upn") + " | " + jwt.getClaim("username");
    }
}
