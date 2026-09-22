# Meal content and API contract

A Meal represents a final composition; recipes are its separate preparations. Shared semantics live in [the meal model](meal-recipe-model.md).

- [MealRequest](../humami-backend/src/main/java/com/hugo/humami/dto/request/MealRequest.java) and related request DTOs define payload fields; [controller](../humami-backend/src/main/java/com/hugo/humami/controller/MealController.java) defines operations.
- Creation is `POST /api/meals` with JSON. Image upload is `PUT /api/meals/{id}/image` with multipart field `image`.
- Writes require X-HUMAMI-SECRET through the existing interceptor. No draft state or idempotency key is currently provided for meal creation.
- Culinary fields are Spanish. Technical documentation/contracts are English.
- [Template](../ops/mealrequest-template.json) and [example](../ops/mealrequest-example-valid.json) provide payload shapes, not permission to publish.
- [Validator](../scripts/validate-mealrequest.py) checks shape/enums and some content constraints. It currently does not enforce the documented minimum of two steps per preparation.

Procedures: [prepare meals](../.agents/skills/humami-meal-authoring/SKILL.md) and [publish content](../.agents/skills/humami-content-publish/SKILL.md). No private workspace memory is required.
