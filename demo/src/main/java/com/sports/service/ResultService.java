package com.sports.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sports.entity.*;
import com.sports.repository.*;
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
    private final CoachRepository coachRepository;
    private final UserRepository userRepository;

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

    public List<ResultDTO> getResultsByAthlete(String userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Doesn't exists"));
        Optional<Athlete> athlete = athleteRepository.findByUser(user);

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

    public List<ResultDTO> getResultsByCoach(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User Doesn't exist"));

        Coach coach = coachRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Coach not found for this user"));

        return resultRepository.findByCoach(coach)
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

    // READ (by video)
    public List<Result> getResultsByVideo(Upload upload) {
        return resultRepository.findByVideo(upload);
    }

    // UPDATE
    public Result updateResult(String id, Result updatedResult) {
        return resultRepository.findById(id)
                .map(existing -> {
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

            // ✅ Use "percentile" instead of "average_score"
            Float score = (float) json.path("percentile").asDouble(0.0);

            // ✅ Use "recommendations" directly (string now)
            String recommendations = json.path("recommendations").asText("");

            // ✅ Use "comments" directly (string now)
            String comments = json.path("comments").asText("");

            // ✅ Use "detected_sports" (string now)
            String detectedSports = json.path("detected_sports").asText("");

            // Coach selection logic
            List<Coach> coachList = coachRepository.findAll();
            Coach coach = null;
            String sport = upload.getAthlete().getSportInterest();
            for (Coach c : coachList) {
                if (!detectedSports.isEmpty() && c.getSpecialization().contains(detectedSports)) {
                    coach = c;
                    break;
                } else if (c.getSpecialization().contains(sport)) {
                    coach = c;
                }
            }

            // ✅ Injury risk already computed in Python, but if you want override:
            String injuryRisk = json.path("injuryRisk").asText("low");

            // ✅ Badges
            int badges = json.path("badges").asInt(0);

            Result result = Result.builder()
                    .athlete(upload.getAthlete())
                    .coach(coach)
                    .video(upload)
                    .percentile(score)
                    .injuryRisk(injuryRisk)
                    .recommendations(recommendations)
                    .comments(comments)
                    .badges(badges)
                    .build();

            String today = LocalDate.now().format(formatter);
            result.setCreatedAt(today);

            resultRepository.save(result);
            createLeader(upload.getAthlete(), coach);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse and save report JSON", e);
        }
    }

    public void createLeader(Athlete athlete, Coach coach) {
        // Check if a leaderboard entry already exists for this athlete
        Optional<LeaderBoard> existing = leaderBoardRepository.findByAthleteId(athlete.getId());

        if (existing.isPresent()) {
            // Optionally, update coach or other fields if needed
            LeaderBoard lb = existing.get();
            lb.setCoach(coach);
            lb.setRegion(athlete.getUser().getLocation());
            lb.setSport(athlete.getSportInterest());
            leaderBoardRepository.save(lb); // update existing entry
        } else {
            // Create a new leaderboard entry
            LeaderBoard leaderBoard = LeaderBoard.builder()
                    .athlete(athlete)
                    .coach(coach)
                    .region(athlete.getUser().getLocation())
                    .sport(athlete.getSportInterest())
                    .build();
            leaderBoardRepository.save(leaderBoard);
        }
    }

}
