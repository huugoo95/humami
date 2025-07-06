package com.hugo.humami.controller;

import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.AutocompleteResponse;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.service.MealService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @GetMapping("")
    public ResponseEntity<List<MealResponse>> getAll() {
        List<MealResponse> meals = mealService.getAll();
        return new ResponseEntity<>(meals, HttpStatus.OK);
    }

    @GetMapping("search")
    public ResponseEntity<List<MealResponse>> search(String search) {
        List<MealResponse> meals = mealService.search(search);
        return new ResponseEntity<>(meals, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<MealResponse> get(@PathVariable String id) throws ChangeSetPersister.NotFoundException, IOException {
        MealResponse meal = mealService.getById(id);
        return new ResponseEntity(meal, HttpStatus.OK);
    }

    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MealResponse> createMeal(
            @RequestPart("mealRequest") MealRequest mealRequest,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        MealResponse created = mealService.create(mealRequest, image);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<MealResponse> update(
            @PathVariable String id,
            @RequestBody MealRequest mealRequest,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        MealResponse updated = mealService.update(id, mealRequest, image);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/autocomplete")
    public AutocompleteResponse autocomplete(@RequestParam String query) {
        return mealService.autocomplete(query);
    }

}
