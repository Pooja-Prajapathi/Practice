package com.sports.entity;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "athletes")
public class Athlete {

    @Id
    private String id; // MongoDB ObjectId

    @DBRef(lazy = true)
    private User user;

    @NotBlank(message = "Sport interest is required")
    private String sportInterest;

    @PositiveOrZero(message = "Height must be positive")
    private Integer heightCm;

    @PositiveOrZero(message = "Weight must be positive")
    private Integer weightKg;

    private String medicalHistory;

    private Boolean parentalConsent = false;
}
