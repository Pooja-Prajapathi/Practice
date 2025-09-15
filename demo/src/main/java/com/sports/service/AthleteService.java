package com.sports.service;

import com.sports.entity.Athlete;
import com.sports.entity.User;
import com.sports.repository.AthleteRepository;
import com.sports.repository.UserRepository;
import lombok.*;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AthleteService {

    private final AthleteRepository athleteRepository;
    private final UserRepository userRepository;
    private final GridFsTemplate gridFsTemplate;

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
                .build();

        return athleteRepository.save(athlete);
    }

    public Optional<Athlete> getAthleteByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return athleteRepository.findByUser(user);
    }

    public List<Athlete> getAllAthletes() {
        return athleteRepository.findAll();
    }

    public Optional<Athlete> getAthleteById(String id) {
        return athleteRepository.findById(id);
    }

    public Optional<Athlete> updateAthlete(String id, Athlete updatedAthlete) {
        return athleteRepository.findById(id).map(athlete -> {
            athlete.setSportInterest(updatedAthlete.getSportInterest());
            athlete.setHeightCm(updatedAthlete.getHeightCm());
            athlete.setWeightKg(updatedAthlete.getWeightKg());
            athlete.setMedicalHistory(updatedAthlete.getMedicalHistory());
            athlete.setParentalConsent(updatedAthlete.getParentalConsent());
            return athleteRepository.save(athlete);
        });
    }

    public boolean deleteAthlete(String id) {
        if (athleteRepository.existsById(id)) {
            athleteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
