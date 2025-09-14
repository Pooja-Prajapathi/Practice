package com.sports.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "coaches")
public class Coach {

    @Id
    private String id; // MongoDB ObjectId

    @DBRef(lazy = true) // Reference to User collection
    private User user;

    @NotBlank(message = "Specialization is required")
    private String specialization; // Sport type

    private String certification;

    @Min(value = 0, message = "Experience years must be 0 or greater")
    private Integer experienceYears;

    @NotBlank(message = "Region is required")
    private String region;

    private String bio;
}
