package com.shopping.electroshopping.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserSignUpDto {

    @NotNull(message = "user name is empty")
    private String userName;

    @Email(message = "not a valid email")
    private String email;

    @NotNull(message = "password is empty")
    private String password;

    @NotNull(message = "phone number is empty")
    private String phoneNumber;

    private LocalDate joinDate;

    private boolean verified;




}
