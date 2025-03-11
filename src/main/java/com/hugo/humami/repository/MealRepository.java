package com.hugo.humami.repository;

import com.hugo.humami.domain.MealEntity;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MealRepository extends MongoRepository<MealEntity, String> {

    @Aggregation(pipeline = {
            "{ '$search': { " +
                    "   'index': 'default', " +
                    "   'compound': { " +
                    "     'should': [ " +
                    "       { 'text': { 'query': ?0, 'path': 'name', 'score': { 'boost': { 'value': 5 } }, 'fuzzy': { 'maxEdits': 2 } } }, " +
                    "       { 'text': { 'query': ?0, 'path': 'description', 'score': { 'boost': { 'value': 3 } }, 'fuzzy': { 'maxEdits': 2 } } }, " +
                    "       { 'embeddedDocuments': { 'path': 'recipes', 'operator': { 'text': { 'query': ?0, 'path': 'recipes.title', 'score': { 'boost': { 'value': 2 } }, 'fuzzy': { 'maxEdits': 2 } } } } }, " +
                    "       { 'embeddedDocuments': { 'path': 'recipes', 'operator': { 'text': { 'query': ?0, 'path': 'recipes.ingredients', 'fuzzy': { 'maxEdits': 2 } } } } } " +
                    "     ], " +
                    "     'minimumShouldMatch': 1 " +
                    "   } " +
                    "} }",
            "{ '$limit': 10 }"
    })
    List<MealEntity> searchBySemanticText(String query);

    @Aggregation(pipeline = {
            "{ '$vectorSearch': { 'queryVector': ?0, 'path': 'embedding', 'numCandidates': 10, 'index': 'vector_index' } }",
            "{ '$limit': 10 }"
    })
    List<MealEntity> searchByEmbedding(List<Double> queryVector);

    @Aggregation(pipeline = {
            "{ '$search': { " +
                    "   'index': 'default', " +
                    "   'autocomplete': { 'query': ?0, 'path': 'name' } " +
                    "} }",
            "{ '$limit': 5 }"
    })
    List<MealEntity> autocompleteMealNames(String query);

    @Aggregation(pipeline = {
            "{ '$search': { " +
                    "   'index': 'default', " +
                    "   'autocomplete': { 'query': ?0, 'path': 'recipes.name' } " +
                    "} }",
            "{ '$limit': 5 }"
    })
    List<MealEntity> autocompleteRecipeNames(String query);
}
