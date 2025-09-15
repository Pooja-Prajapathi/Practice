package com.sports.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sports.entity.*;
import com.sports.repository.AthleteRepository;
import com.sports.repository.LeaderBoardRepository;
import com.sports.repository.ResultRepository;
import com.sports.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResultService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResultRepository resultRepository;
    private final LeaderBoardRepository leaderBoardRepository;
    private final UploadRepository uploadRepository;
    private final AthleteRepository athleteRepository;

    // CREATE
    public Result saveResult(Result result) {
        String today = LocalDate.now().format(formatter);
        result.setCreatedAt(today);
        return resultRepository.save(result);
    }

    // READ (all results)
    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    // READ (by id)
    public Optional<Result> getResultById(String id) {
        return resultRepository.findById(id);
    }

    public List<ResultDTO> getResultsByAthleteId(String athleteId) {
        Optional<Athlete> athlete = athleteRepository.findById(athleteId);

        return resultRepository.findByAthleteId(athlete)
                .stream()
                .map(result -> ResultDTO.builder()
                        .id(result.getId())
                        .athleteName(result.getAthlete() != null ? result.getAthlete().getUser().getFullname() : null)
                        .coachName(result.getCoach() != null ? result.getCoach().getUser().getFullname() : null)
                        .videoFileName(result.getVideo() != null ? result.getVideo().getFileName() : null)
                        .percentile(result.getPercentile())
                        .injuryRisk(result.getInjuryRisk())
                        .recommendations(result.getRecommendations())
                        .comments(result.getComments())
                        .createdAt(result.getCreatedAt())
                        .badges(result.getBadges())
                        .build()
                )
                .collect(Collectors.toList());
    }

//    public List<Result> getResultsByCoachId(String coachId) {
//        Coach coach = new Coach();
//        coach.setId(coachId);
//        return resultRepository.findByVideoCoach(coach);
//    }

    // READ (by video)
    public List<Result> getResultsByVideo(Upload upload) {
        return resultRepository.findByVideo(upload);
    }

    // UPDATE
    public Result updateResult(String id, Result updatedResult) {
        return resultRepository.findById(id)
                .map(existing -> {
                    existing.setPercentile(updatedResult.getPercentile());
                    existing.setInjuryRisk(updatedResult.getInjuryRisk());
                    existing.setRecommendations(updatedResult.getRecommendations());
                    existing.setComments(updatedResult.getComments());
                    existing.setBadges(updatedResult.getBadges());
                    return resultRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Result not found with id: " + id));
    }

    // DELETE
    public void deleteResult(String id) {
        resultRepository.deleteById(id);
    }

    public void saveReportToDatabase(Upload upload, String reportJson) {
        try {
            // 🔹 Clean JSON (strip logs before first '{')
            int jsonStart = reportJson.indexOf("{");
            if (jsonStart > 0) {
                reportJson = reportJson.substring(jsonStart);
            }
            JsonNode json = objectMapper.readTree(reportJson);
            System.out.println("*****************************"+json);
            Float score = (float) json.path("average_score").asDouble(0.0);
            // Collect recommendations
            StringBuilder recommendationsBuilder = new StringBuilder();
            if (json.has("suggestions") && json.get("suggestions").isArray()) {
                for (JsonNode suggestion : json.get("suggestions")) {
                    recommendationsBuilder.append(suggestion.asText()).append("\n");
                }
            }
            String recommendations = recommendationsBuilder.toString().trim();

            // Collect key moments
            StringBuilder keyMomentsBuilder = new StringBuilder();
            if (json.has("key_moments") && json.get("key_moments").isArray()) {
                for (JsonNode moment : json.get("key_moments")) {
                    keyMomentsBuilder
                            .append("Frame ")
                            .append(moment.path("frame").asInt())
                            .append(": ")
                            .append(moment.path("description").asText())
                            .append("\n");
                }
            }
            String keyMoments = keyMomentsBuilder.toString().trim();
            Result result = Result.builder()
                    .athlete(upload.getAthlete())
                    .video(upload)
                    .percentile(score)
                    .injuryRisk(score>50?"high":"low")
                    .recommendations(recommendations)
                    .comments(keyMoments)
                    .badges(0)
                    .build();
            String today = LocalDate.now().format(formatter);
            result.setCreatedAt(today);
            resultRepository.save(result);
            createLeader(upload.getAthlete());
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse and save report JSON", e);
        }
    }

    public void createLeader(Athlete athlete){
        LeaderBoard leaderBoard=LeaderBoard.builder()
                .athlete(athlete)
                .region(athlete.getUser().getLocation())
                .sport(athlete.getSportInterest())
                .build();
        leaderBoardRepository.save(leaderBoard);
    }
}
