package com.sports.service;

import com.sports.entity.*;
import com.sports.repository.LeaderBoardRepository;
import com.sports.repository.ResultRepository;
import com.sports.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaderBoardService {

    private final LeaderBoardRepository leaderBoardRepository;
    private final ResultRepository resultRepository;
    private final UploadRepository uploadRepository;

    public LeaderBoard saveLeaderBoard(LeaderBoard lb) {
        lb.setPoints(calculatePoints(lb.getAthlete(), lb.getSport()));
        LeaderBoard saved = leaderBoardRepository.save(lb);
        recalculateRank(lb.getSport(), lb.getRegion());
        return saved;
    }

    public List<LeaderBoard> getAllLeaderBoards() {
        return leaderBoardRepository.findAll();
    }

    public Optional<LeaderBoard> getLeaderBoardById(String id) {
        return leaderBoardRepository.findById(id);
    }

    public LeaderBoard updateLeaderBoard(String id, LeaderBoard updatedLb) {
        return leaderBoardRepository.findById(id)
                .map(existing -> {
                    existing.setAthlete(updatedLb.getAthlete());
                    existing.setCoach(updatedLb.getCoach());
                    existing.setSport(updatedLb.getSport());
                    existing.setRegion(updatedLb.getRegion());
                    existing.setPoints(calculatePoints(updatedLb.getAthlete(), updatedLb.getSport()));
                    LeaderBoard saved = leaderBoardRepository.save(existing);
                    recalculateRank(saved.getSport(), saved.getRegion());
                    return saved;
                })
                .orElseThrow(() -> new RuntimeException("LeaderBoard not found with id: " + id));
    }

    // DELETE
    public void deleteLeaderBoard(String id) {
        leaderBoardRepository.deleteById(id);
    }

    // Calculate total points for an athlete in a given sport
    private int calculatePoints(Athlete athlete, String sport) {
        List<Result> results = resultRepository.findAll(); // fetch all results
        return results.stream()
                .filter(r -> r.getVideo().getAthlete().getId().equals(athlete.getId()))
                .mapToInt(r -> r.getBadges() != null ? r.getBadges() : 0)
                .sum();
    }

    // Recalculate ranks for a sport and optional region
    private void recalculateRank(String sport, String region) {
        List<LeaderBoard> leaderboard;

        if (region != null && !region.isBlank()) {
            leaderboard = leaderBoardRepository.findBySportAndRegionOrderByPointsDesc(sport, region);
        } else {
            leaderboard = leaderBoardRepository.findBySportOrderByPointsDesc(sport);
        }

        if (leaderboard.isEmpty()) {
            return;
        }

        int rank = 1;
        int samePointsRank = 1;
        Integer lastPoints = null;

        for (int i = 0; i < leaderboard.size(); i++) {
            LeaderBoard lb = leaderboard.get(i);

            if (lastPoints != null && lb.getPoints().equals(lastPoints)) {
                // Same points → same rank
                lb.setRank(samePointsRank);
            } else {
                // New points → update rank
                lb.setRank(rank);
                samePointsRank = rank;
            }

            lastPoints = lb.getPoints();
            rank++;
        }

        leaderBoardRepository.saveAll(leaderboard);
    }


    public void create(String uploadId,Athlete athlete){
        Upload upload=uploadRepository.findById(uploadId).orElseThrow(()->
                new RuntimeException("Video Doesn't exists"));
        LeaderBoard leaderBoard=LeaderBoard.builder()
                .athlete(athlete)
                .region(athlete.getUser().getLocation())
                .sport(athlete.getSportInterest())
                .build();
        saveLeaderBoard(leaderBoard);
    }
}
