package com.blankcil.api.blankcilapi.auth;

import com.blankcil.api.blankcilapi.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  private String fullname;
  private String email;
  private String password;
  private Role role;
  private LocalDate birthday;
  private String address;
  private String phone;
  private String code;
}
