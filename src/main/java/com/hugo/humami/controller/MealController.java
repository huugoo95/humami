package com.hugo.humami.controller;

import com.hugo.humami.dto.response.AutocompleteResponse;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.MealResponse;
import com.hugo.humami.service.MealService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/meals/")
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
    public ResponseEntity<MealResponse> get(@PathVariable String id) {
        MealResponse meal = mealService.getById(id);
        return new ResponseEntity(meal, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody MealRequest mealRequest) {
        mealService.save(mealRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> update(MealRequest mealRequest) {
        mealService.save(mealRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @GetMapping("/autocomplete")
    public AutocompleteResponse autocomplete(@RequestParam String query) {
        return mealService.autocomplete(query);
    }

}
