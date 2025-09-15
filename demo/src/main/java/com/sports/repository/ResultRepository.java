package com.sports.repository;

import com.sports.entity.Athlete;
import com.sports.entity.Coach;
import com.sports.entity.Result;
import com.sports.entity.Upload;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface ResultRepository extends MongoRepository<Result, String> {
    List<Result> findByVideo(Upload upload);
    List<Result> findByAthleteId(Optional<Athlete> athlete);
    List<Result> findByCoach(Coach coach);
}

