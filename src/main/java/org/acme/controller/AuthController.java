package org.acme.controller;

import io.smallrye.jwt.build.Jwt;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.acme.dto.AuthResponse;
import org.acme.dto.UserRequest;
import org.acme.entity.Users;
import org.acme.util.PasswordUtils;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.Set;

@Path("/auth")
@Tag(name = "Auth Controller", description = "Auth Management API")
public class AuthController {

  @POST
  @Path("/register")
  @Transactional
  @Operation(summary = "Register new user", description = "create new user, user id is generated id, username is unique")
  public Response register(@Valid UserRequest request){
    if (Users.find("username", request.username).firstResult() != null){
      throw new RuntimeException("Username sudah dipakai");
    }

    Users user = new Users();
    user.username = request.username;
    user.password = PasswordUtils.hash(request.password);

    user.persist();

    return Response.status(Response.Status.CREATED).build();
  }

  @POST
  @Path("/login")
  @Operation(summary = "Login using existing user, return JWT if login success", description = "Login using existing user, return JWT if login success")
  public Response login(UserRequest request){
    Users existing = Users.find("username", request.username).firstResult();

    if (existing == null || !PasswordUtils.verify(request.password, existing.password)) {
      return Response.status(Response.Status.NO_CONTENT).build();
    }


    String token = Jwt.upn(existing.id.toString())
            .claim("username", existing.username)
            .groups(Set.of("admin"))
            .expiresIn(100000000L)
            .sign();

    return Response.ok(new AuthResponse(token)).header("Authorization", "Bearer " + token).entity(new AuthResponse(token)).build();


//    return Response.ok().build();
  }
}
