package com.sports.repository;

import com.sports.entity.Athlete;
import com.sports.entity.Coach;
import com.sports.entity.Upload;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UploadRepository extends MongoRepository<Upload, String> {
        Optional<Upload> findByAthlete(Athlete athlete);
        Optional<Upload> findByVideoId(String videoId);
}
