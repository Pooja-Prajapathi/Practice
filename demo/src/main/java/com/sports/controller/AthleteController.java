package com.sports.controller;

import com.sports.entity.Athlete;
import com.sports.service.AthleteService;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/athletes")
@RequiredArgsConstructor
public class AthleteController {

    private final AthleteService athleteService;

    @PostMapping("/{userId}")
    public ResponseEntity<?> createAthlete(@PathVariable String userId,
                                           @Valid @RequestBody Athlete athlete) {
        try {
            Athlete created = athleteService.createAthlete(userId, athlete);
            return ResponseEntity.ok(created);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(409).body(ex.getMessage()); // 409 Conflict
        }
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<Athlete> getAthleteByUser(@PathVariable String userId) {
        return athleteService.getAthleteByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Athlete>> getAllAthletes() {
        return ResponseEntity.ok(athleteService.getAllAthletes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Athlete> getAthleteById(@PathVariable String id) {
        return athleteService.getAthleteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Athlete> updateAthlete(@PathVariable String id, @Valid @RequestBody Athlete athlete) {
        return athleteService.updateAthlete(id, athlete)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAthlete(@PathVariable String id) {
        return athleteService.deleteAthlete(id) ?
                ResponseEntity.noContent().build() :
                ResponseEntity.notFound().build();
    }
}
