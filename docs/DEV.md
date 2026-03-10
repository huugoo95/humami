# DEV.md — Guía de ejecución técnica

## Alcance
Normas operativas para trabajo en backend (`humami-backend`) y frontend (`humami-web`).

## Principios
- Seguir `ENGINEERING_RULES.md` (TDD y DoD obligatorios).
- Toda tarea no trivial parte de `specs/`.
- Sin lógica de negocio en controladores.
- Front por features, no por carpetas genéricas.

## Flujo por tarea
1. Definir spec (`specs/XXX-*.md`)
2. Implementar en rama de feature
3. Tests + lint + docs
4. PR con plantilla completa

## Convenciones rápidas
- Backend: validar inputs en boundary.
- Frontend: API en módulos/hooks dedicados.
- Cambios de contrato: documentar antes de integrar FE/BE.
