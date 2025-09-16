package com.sports.controller;

import com.sports.entity.Result;
import com.sports.entity.ResultDTO;
import com.sports.service.ResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;

    // CREATE
    @PostMapping
    public Result createResult(@RequestBody Result result) {
        return resultService.saveResult(result);
    }

    // READ (all)
    @GetMapping
    public List<Result> getAllResults() {
        return resultService.getAllResults();
    }

    // READ (by id)
    @GetMapping("/{id}")
    public Result getResultById(@PathVariable String id) {
        return resultService.getResultById(id)
                .orElseThrow(() -> new RuntimeException("Result not found with id: " + id));
    }

    @GetMapping("/athlete/{id}")
    public List<ResultDTO> getResultByAthleteId(@PathVariable String id) {
        return resultService.getResultsByAthlete(id);
    }

    @GetMapping("/coach/{id}")
    public List<ResultDTO> getResultByCoachId(@PathVariable String id) {
        return resultService.getResultsByCoach(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Result updateResult(@PathVariable String id, @RequestBody Result updatedResult) {
        return resultService.updateResult(id, updatedResult);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void deleteResult(@PathVariable String id) {
        resultService.deleteResult(id);
    }
}
