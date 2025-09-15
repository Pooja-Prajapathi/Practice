package com.sports.service;

import com.sports.entity.Coach;
import com.sports.entity.User;
import com.sports.repository.CoachRepository;
import com.sports.repository.UserRepository;
import lombok.*;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoachService {

    private final CoachRepository coachRepository;
    private final UserRepository userRepository;

    public Coach createCoach(String userId, Coach coachData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Optional<Coach> existingAthlete = coachRepository.findByUser(user);
        if (existingAthlete.isPresent()) {
            throw new RuntimeException("Athlete already exists for user with id: " + userId);
        }

        Coach coach = Coach.builder()
                .user(user)
                .specialization(coachData.getSpecialization())
                .certification(coachData.getCertification())
                .experienceYears(coachData.getExperienceYears())
                .region(coachData.getRegion())
                .bio(coachData.getBio())
                .build();

        return coachRepository.save(coach);
    }

    public Optional<Coach> getCoachByUserId(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return coachRepository.findByUser(user);
    }

    public List<Coach> getAllCoaches() {
        return coachRepository.findAll();
    }

    public Optional<Coach> getCoachById(String id) {
        return coachRepository.findById(id);
    }

    public Optional<Coach> updateCoach(String id, Coach updatedCoach) {
        return coachRepository.findById(id).map(coach -> {
            coach.setSpecialization(updatedCoach.getSpecialization());
            coach.setCertification(updatedCoach.getCertification());
            coach.setExperienceYears(updatedCoach.getExperienceYears());
            coach.setRegion(updatedCoach.getRegion());
            coach.setBio(updatedCoach.getBio());
            return coachRepository.save(coach);
        });
    }

    public boolean deleteCoach(String id) {
        if (coachRepository.existsById(id)) {
            coachRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
