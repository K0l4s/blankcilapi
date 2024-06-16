package com.blankcil.api.blankcilapi.controller;

import com.blankcil.api.blankcilapi.model.*;
import com.blankcil.api.blankcilapi.model.request.AuthenticationRequest;
import com.blankcil.api.blankcilapi.model.request.RegisterRequest;
import com.blankcil.api.blankcilapi.model.response.AuthenticationResponse;
import com.blankcil.api.blankcilapi.model.response.RegisterResponse;
import com.blankcil.api.blankcilapi.service.AuthenticationService;
import com.blankcil.api.blankcilapi.user.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/register")
  public ResponseEntity<RegisterResponse> register(
          @RequestBody RegisterRequest request
  ) {
    request.setRole(Role.USER);
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/confirm-email")
  public ResponseEntity<AuthenticationResponse> confirm(@RequestBody ConfirmRequest confirmRequest) throws Exception {
    return ResponseEntity.ok(service.confirmRegister(confirmRequest));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }
  @PatchMapping("/code/{email}")
  public ResponseEntity<String> sendCode(
          @PathVariable("email") String email
  ){
    return ResponseEntity.ok(service.sendCodeToUser(email));
  }

}
