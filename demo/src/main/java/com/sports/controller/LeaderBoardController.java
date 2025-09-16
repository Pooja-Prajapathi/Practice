package com.sports.controller;

import com.sports.entity.LeaderBoard;
import com.sports.service.LeaderBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboards")
@RequiredArgsConstructor
public class LeaderBoardController {

    private final LeaderBoardService leaderBoardService;

    // CREATE
    @PostMapping
    public ResponseEntity<LeaderBoard> createLeaderBoard(@RequestBody LeaderBoard lb) {
        return ResponseEntity.ok(leaderBoardService.saveLeaderBoard(lb));
    }

    // READ all
    @GetMapping
    public ResponseEntity<List<LeaderBoard>> getAllLeaderBoards() {
        List<LeaderBoard> updatedList = leaderBoardService.updateAllLeaderBoards();
        return ResponseEntity.ok(updatedList);
    }

    // READ by ID
    @GetMapping("/{id}")
    public ResponseEntity<LeaderBoard> getLeaderBoardById(@PathVariable String id) {
        return leaderBoardService.getLeaderBoardById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<LeaderBoard> updateLeaderBoard(@PathVariable String id, @RequestBody LeaderBoard lb) {
        return ResponseEntity.ok(leaderBoardService.updateLeaderBoard(id, lb));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaderBoard(@PathVariable String id) {
        leaderBoardService.deleteLeaderBoard(id);
        return ResponseEntity.noContent().build();
    }
}
