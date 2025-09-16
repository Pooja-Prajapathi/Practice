package com.sports.service;

import com.sports.entity.*;
import com.sports.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Criteria;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final ResultRepository resultRepository;
    private final CoachRepository coachRepository;
    private final UploadRepository uploadRepository;
    private final AthleteRepository athleteRepository;
    private final GridFsTemplate gridFsTemplate;
    private final ResultService resultService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String uploadVideo(String athleteId, MultipartFile file) throws IOException {
        Athlete athlete = athleteRepository.findById(athleteId)
                .orElseThrow(() -> new RuntimeException("Athlete not found with id: " + athleteId));

        // ✅ Step 1: Store video in GridFS
        ObjectId videoId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());

        Upload upload = Upload.builder()
                .athlete(athlete)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .videoId(videoId.toString())
                .uploadedAt(LocalDate.now().toString())
                .build();
        String today = LocalDate.now().format(formatter);
        upload.setUploadedAt(today);
        uploadRepository.save(upload);

        // ✅ Step 2: Write MultipartFile to a temporary file (for Python processing)
        Path tempFilePath = null;
        try {
            tempFilePath = Files.createTempFile("video-", ".mp4");
            file.transferTo(tempFilePath.toFile());

            // ✅ Step 3: Call Python script
            String pythonOutput = callPythonVideoAnalysis(tempFilePath.toString(), athleteId);

            // ✅ Step 4: Parse Python JSON output & save result
            resultService.saveReportToDatabase(upload, pythonOutput);

            return videoId.toString();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (tempFilePath != null) {
                Files.deleteIfExists(tempFilePath); // cleanup temp file
            }
        }
    }

    private String callPythonVideoAnalysis(String videoFilePath, String athleteId) throws IOException, InterruptedException {
        StringBuilder output = new StringBuilder();

        // Run Python script
        ProcessBuilder pb = new ProcessBuilder("python", "VideoAnalyzer.py", videoFilePath, athleteId);
        pb.redirectErrorStream(true);

        Process p = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }

        int exitCode = p.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script failed with exit code: " + exitCode + ". Output: " + output);
        }

        return output.toString();
    }

    public Resource loadVideoAsResource(String videoId) {
        try {
            GridFSFile gridFSFile = gridFsTemplate.findOne(
                    new Query(Criteria.where("_id").is(new ObjectId(videoId)))
            );
            if (gridFSFile == null) {
                throw new RuntimeException("Video not found with id: " + videoId);
            }
            return gridFsTemplate.getResource(gridFSFile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load video: " + videoId, e);
        }
    }

    // Update getVideos() to return full URLs
    public List<VideoDTO> getVideos(String coachId) {
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new RuntimeException("Coach not found"));

        List<Result> results = resultRepository.findByCoach(coach);

        return results.stream().map(result -> {
            Upload upload = result.getVideo();
            Athlete athlete = result.getAthlete();

            return new VideoDTO(
                    upload.getId(),
                    upload.getFileName(),
                    "http://localhost:8080/api/videos/view/" + upload.getVideoId(),
                    upload.getUploadedAt(),
                    athlete != null && athlete.getUser() != null ? athlete.getUser().getFullname() : "Unknown Athlete"
            );
        }).collect(Collectors.toList());
    }
}
