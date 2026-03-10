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

## Regla de seguridad operativa
Antes de ejecutar escrituras reales en API desde chat, pedir validación explícita de Hugo.

## Pendiente
- Documentar ejemplos de payload reales y respuestas esperadas.
