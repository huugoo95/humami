package com.hugo.humami.service;

import com.hugo.humami.dto.response.AutocompleteResponse;
import com.hugo.humami.dto.request.MealRequest;
import com.hugo.humami.dto.response.MealResponse;

import java.util.List;

public interface MealService {
    List<MealResponse> getAll();

    MealResponse getById(String id);

    MealResponse save(MealRequest mealRequest);

    void delete(String id);

    List<MealResponse> search(String query);

    AutocompleteResponse autocomplete(String query);
}