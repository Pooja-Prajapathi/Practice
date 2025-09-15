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
@Document(collection = "video_uploads")
public class Upload {

    @Id
    private String id; // MongoDB ObjectId

    @DBRef(lazy = true)
    private Athlete athlete;   // Reference back to athlete

    @NotBlank(message = "File name is required")
    private String fileName;

    @PositiveOrZero(message = "File size must be positive")
    private Long fileSize;

    private String videoId;

    private String uploadedAt;
}
