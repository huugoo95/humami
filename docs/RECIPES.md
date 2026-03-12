# RECIPES.md — Operativa de recetas y API

## Objetivo
Estandarizar cómo crear recetas/menús desde Humami sin errores de contrato.

## Contrato base (actual)
- Crear menú/receta: `POST /api/meals` con JSON (`MealRequest`).
- Subir imagen: `PUT /api/meals/{id}/image` con `multipart/form-data`, campo `image`.

> Referencia de memoria operativa: `memory/humami-contracts.md` (workspace de tenacitas).

## Flujo operativo recomendado
1. Preparar payload validado.
2. Ejecutar creación (`POST /api/meals`).
3. Si aplica, subir imagen (`PUT /api/meals/{id}/image`).
4. Verificar respuesta y registrar resultado.

## Checklist de validación antes de crear un Meal
Antes de cada alta real, validar explícitamente:

1. **Semántica Meal vs Recipe**
   - `Meal` representa el plato/comida final.
   - `recipes[]` contiene elaboraciones concretas (sin mezclar subpreparaciones en una sola receta).

2. **Separación de preparaciones**
   - Ingredientes y pasos separados por cada elaboración.
   - No mezclar instrucciones de salsa/pan/caldo/chashu/etc. en la misma recipe si son bloques distintos.

3. **Enums y campos obligatorios**
   - `type`, `difficulty`, `servings`, timings y resto de campos del contrato.
   - Valores de enums válidos y coherentes con el caso.

4. **Consistencia culinaria**
   - Si hay varias recipes, confirmar que son parte natural del flujo del meal (ej. kebab + salsa, ramen + caldo/chashu/aceite).
   - Si algo es opcional o reusable globalmente, evaluar referencia en lugar de embebido.

5. **Previsualización + OK humano**
   - Mostrar resumen previo del payload.
   - Pedir validación final de Hugo antes de `POST /api/meals`.

## Regla de seguridad operativa
Antes de ejecutar escrituras reales en API desde chat, pedir validación explícita de Hugo.

## Pendiente
- Documentar ejemplos de payload reales y respuestas esperadas.
