package com.sports.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullname;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Date of birth is required (yyyy-MM-dd)")
    private String dob;   // stored as plain string in MongoDB

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "male|female|other", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Gender must be male, female, or other")
    private String gender;

    @NotBlank(message = "Location is required")
    private String location;

    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10–15 digits")
    private String contact;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "athlete|coach", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Role must be athlete, coach, or admin")
    private String role;

    private String createdAt;
}
