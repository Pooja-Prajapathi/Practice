package com.sports.service;

import com.sports.entity.Athlete;
import com.sports.entity.User;
import com.sports.repository.AthleteRepository;
import com.sports.repository.UserRepository;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final GridFsTemplate gridFsTemplate;

    // ✅ Create athlete by linking existing User
    public Athlete createAthlete(String userId, Athlete athleteData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Optional<Athlete> existingAthlete = athleteRepository.findByUser(user);
        if (existingAthlete.isPresent()) {
            throw new RuntimeException("Athlete already exists for user with id: " + userId);
        }

        Athlete athlete = Athlete.builder()
                .user(user)
                .sportInterest(athleteData.getSportInterest())
                .heightCm(athleteData.getHeightCm())
                .weightKg(athleteData.getWeightKg())
                .medicalHistory(athleteData.getMedicalHistory())
                .parentalConsent(athleteData.getParentalConsent())
                .badgePoints(athleteData.getBadgePoints())
                .build();

        return athleteRepository.save(athlete);
    }

    public Optional<Athlete> getAthleteByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return athleteRepository.findByUser(user);
    }

    // ✅ Fetch all athletes
    public List<Athlete> getAllAthletes() {
        return athleteRepository.findAll();
    }

    // ✅ Fetch athlete by ID
    public Optional<Athlete> getAthleteById(String id) {
        return athleteRepository.findById(id);
    }

    // ✅ Update athlete details (excluding user reference)
    public Optional<Athlete> updateAthlete(String id, Athlete updatedAthlete) {
        return athleteRepository.findById(id).map(athlete -> {
            athlete.setSportInterest(updatedAthlete.getSportInterest());
            athlete.setHeightCm(updatedAthlete.getHeightCm());
            athlete.setWeightKg(updatedAthlete.getWeightKg());
            athlete.setMedicalHistory(updatedAthlete.getMedicalHistory());
            athlete.setParentalConsent(updatedAthlete.getParentalConsent());
            athlete.setBadgePoints(updatedAthlete.getBadgePoints());
            return athleteRepository.save(athlete);
        });
    }

    // ✅ Delete athlete
    public boolean deleteAthlete(String id) {
        if (athleteRepository.existsById(id)) {
            athleteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public String uploadVideo(String athleteId, MultipartFile file) throws IOException {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + athleteId));

        ObjectId videoId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());

        athlete.getVideoIds().add(videoId.toString());
        athleteRepository.save(athlete);

        return videoId.toString();
    }

    // ✅ Fetch all videos for an athlete
    public List<String> getVideos(String athleteId) {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + athleteId));
        return athlete.getVideoIds();
    }
}
