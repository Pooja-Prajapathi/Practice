package com.sports.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "leaderboard")
public class LeaderBoard {

    @Id
    private String id; // MongoDB ObjectId (leaderboard_id)

    @DBRef(lazy = true)
    private Athlete athlete;   // FK -> Athlete

    @DBRef(lazy = true)
    private Coach coach;

    @NotBlank(message = "Sport category is required")
    private String sport;

    private String region;     // optional region filter

    @PositiveOrZero(message = "Rank must be positive")
    private Integer rank;

    @PositiveOrZero(message = "Points must be positive")
    private Integer points;    // based on performance & badges
}
