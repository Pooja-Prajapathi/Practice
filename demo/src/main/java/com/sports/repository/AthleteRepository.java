package com.sports.repository;

import com.sports.entity.Athlete;
import com.sports.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AthleteRepository extends MongoRepository<Athlete, String> {
    Optional<Athlete> findByUser(User user);
}
