package com.example.studentmanagement.dto.request;



import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class StudentRequest {

    @NotNull(message = "Cannot be null")
    private String name;
    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @NotNull(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = "Phonenumber cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phone;
    private String address;
}