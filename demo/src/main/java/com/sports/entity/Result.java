package com.sports.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "results")
public class Result {

    @Id
    private String id; // MongoDB ObjectId

    @DBRef(lazy = true)
    private Upload video;   // FK -> VideoUpload

    @DBRef(lazy = true)
    private Coach coach;

    @DBRef(lazy = true)
    private Athlete athlete;

    private Float percentile;    // rank among peers

    private String injuryRisk;   // ENUM: "low", "medium", "high"

    private String recommendations; // training suggestions

    private String comments;

    private String createdAt;

    private Integer badges;
}
