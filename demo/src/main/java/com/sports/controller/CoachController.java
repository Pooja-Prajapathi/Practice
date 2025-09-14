package com.sports.controller;

import com.sports.entity.Athlete;
import com.sports.entity.Coach;
import com.sports.service.CoachService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachService coachService;

    // ✅ Create coach by linking to existing User via userId
    @PostMapping("/{userId}")
    public ResponseEntity<Coach> createCoach(@PathVariable String userId,
                                             @Valid @RequestBody Coach coachData) {
        return ResponseEntity.ok(coachService.createCoach(userId, coachData));
    }

    @GetMapping("/byUser/{userId}")
    public ResponseEntity<Coach> getCoachByUser(@PathVariable String userId) {
        return coachService.getCoachByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get all coaches
    @GetMapping
    public ResponseEntity<List<Coach>> getAllCoaches() {
        return ResponseEntity.ok(coachService.getAllCoaches());
    }

    // ✅ Get coach by ID
    @GetMapping("/{id}")
    public ResponseEntity<Coach> getCoachById(@PathVariable String id) {
        return coachService.getCoachById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Update coach (only profile fields, not linked user)
    @PutMapping("/{id}")
    public ResponseEntity<Coach> updateCoach(@PathVariable String id,
                                             @Valid @RequestBody Coach coachData) {
        return coachService.updateCoach(id, coachData)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete coach
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable String id) {
        return coachService.deleteCoach(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
