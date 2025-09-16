package com.sports.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VideoDTO {
    private String id;
    private String fileName;
    private String url;
    private String uploadedAt;
    private String athleteName;
}
