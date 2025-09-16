package com.sports.controller;

import com.sports.entity.VideoDTO;
import com.sports.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/{athleteId}")
    public ResponseEntity<String> uploadVideo(@PathVariable String athleteId,
                                              @RequestParam("file") MultipartFile file) throws IOException {
        String videoId = uploadService.uploadVideo(athleteId, file);
        return ResponseEntity.ok(videoId);
    }

    @GetMapping("/{coachId}")
    public ResponseEntity<List<VideoDTO>> getVideos(@PathVariable String coachId) {
        List<VideoDTO> videos = uploadService.getVideos(coachId);
        return ResponseEntity.ok(videos);
    }

    @GetMapping("/view/{videoId}")
    public ResponseEntity<Resource> viewVideo(@PathVariable String videoId) {
        Resource resource = uploadService.loadVideoAsResource(videoId);
        if (resource == null || !resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "application/octet-stream";
        try {
            contentType = java.nio.file.Files.probeContentType(resource.getFile().toPath());
        } catch (IOException ex) {
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "video/mp4")
                .body(resource);
    }
}
