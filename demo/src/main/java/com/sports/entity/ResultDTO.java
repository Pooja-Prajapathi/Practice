package com.sports.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultDTO {
    private String id;
    private String athleteName;
    private String coachName;
    private String videoFileName;
    private Float percentile;
    private String injuryRisk;
    private String recommendations;
    private String comments;
    private String createdAt;
    private Integer badges;
}
