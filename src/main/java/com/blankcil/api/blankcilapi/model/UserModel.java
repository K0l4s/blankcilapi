package com.blankcil.api.blankcilapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserModel implements Serializable {
    private Integer id;
    private String fullname;
    private String email;
    private String password;
    private LocalDate birthday;
    private String address;
    private String phone;
}
