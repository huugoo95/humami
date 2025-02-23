package com.hugo.humami.controller;

import com.hugo.humami.dto.RecipeRequest;
import com.hugo.humami.dto.RecipeResponse;
import com.hugo.humami.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recipes/")
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping("")
    public ResponseEntity<List<RecipeResponse>> getAll() {
        List<RecipeResponse> recipes = recipeService.getAll();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
    @GetMapping("search")
    public ResponseEntity<List<RecipeResponse>> search(String search) {
        List<RecipeResponse> recipes = recipeService.search(search);
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }
    @GetMapping("{id}")
    public ResponseEntity<RecipeResponse> get(@PathVariable String id) {
        RecipeResponse recipe = recipeService.getOne(id);
        return new ResponseEntity(recipe, HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<?> create(RecipeRequest recipeRequest) {
        recipeService.save(recipeRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> update(RecipeRequest recipeRequest) {
        recipeService.save(recipeRequest);
        return new ResponseEntity(HttpStatus.CREATED);
    }

}
