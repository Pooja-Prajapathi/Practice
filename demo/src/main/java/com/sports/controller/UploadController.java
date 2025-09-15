package com.sports.controller;

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
    public ResponseEntity<?> getVideos(@PathVariable String coachId) {
        return ResponseEntity.ok(uploadService.getVideos(coachId));
    }

    @GetMapping("/view/{videoId}")
    public ResponseEntity<Resource> viewVideo(@PathVariable String videoId) {
        Resource resource = uploadService.loadVideoAsResource(videoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "video/mp4") // could also read from GridFS metadata
                .body(resource);
    }

}
