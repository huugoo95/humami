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

import javax.validation.Valid;
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

    @GetMapping("/search")
    public ResponseEntity<List<MealResponse>> search(@RequestParam("query") String query) {
        List<MealResponse> meals = mealService.search(query);
        return ResponseEntity.ok(meals);
    }

    @GetMapping("{id}")
    public ResponseEntity<MealResponse> get(@PathVariable String id) throws ChangeSetPersister.NotFoundException, IOException {
        MealResponse meal = mealService.getById(id);
        return new ResponseEntity(meal, HttpStatus.OK);
    }

    @PostMapping(value = "")
    public ResponseEntity<MealResponse> create(
            @RequestBody @Valid MealRequest mealRequest
    ) {
        MealResponse created = mealService.create(mealRequest);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<MealResponse> update(
            @PathVariable String id,
            @RequestBody MealRequest mealRequest
    ) {
        MealResponse updated = mealService.update(id, mealRequest);
        return ResponseEntity.ok(updated);
    }

    @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> setMealImage(
            @PathVariable String id,
            @RequestPart("image") MultipartFile image
    ) throws IOException {
        mealService.setImage(id, image);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/autocomplete")
    public AutocompleteResponse autocomplete(@RequestParam String query) {
        return mealService.autocomplete(query);
    }

}
