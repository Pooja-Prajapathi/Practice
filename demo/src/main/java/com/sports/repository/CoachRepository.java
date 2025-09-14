package com.sports.repository;

import com.sports.entity.Athlete;
import com.sports.entity.Coach;
import com.sports.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CoachRepository extends MongoRepository<Coach, String> {
    Optional<Coach> findByUser(User user);
}
