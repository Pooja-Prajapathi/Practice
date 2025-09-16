package com.sports.repository;

import com.sports.entity.Athlete;
import com.sports.entity.LeaderBoard;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface LeaderBoardRepository extends MongoRepository<LeaderBoard, String> {

    Optional<LeaderBoard> findByAthleteId(String athleteId);

    Optional<LeaderBoard> findByAthlete(Athlete athlete);

    List<LeaderBoard> findBySportOrderByPointsDesc(String sport);

    List<LeaderBoard> findBySportAndRegionOrderByPointsDesc(String sport, String region);
}
