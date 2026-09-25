# Spec 007: about-page-foundation

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-10

## 1) Problem

Falta página “About me” para construir confianza, explicar el proyecto y conectar marca-persona.

## 2) Goal and metric

- Goal: crear sección About clara y profesional alineada con Humami.
- Success metric: página publicada con estructura estándar y CTA útil.

## 3) Scope

### In scope
- Ruta About (`/about`).
- Bloques: historia, misión, enfoque, quién está detrás, CTA.
- Versión inicial con copy editable.

### Out of scope
- Timeline interactivo complejo.
- Multi-idioma inicial.

## 4) User stories

- As a visitor, I want to understand who is behind Humami, so that I can trust the project.

## 5) Acceptance criteria (Given / When / Then)

1. Given un usuario entra en `/about`
   When carga la página
   Then entiende en menos de 30 segundos quién es Humami y qué ofrece.

2. Given la página publicada
   When navega por secciones
   Then encuentra CTA final (suscripción, contacto o blog).

3. Given contenido futuro
   When se quiera actualizar copy
   Then puede editarse sin tocar lógica compleja.

## 6) Edge cases

- Texto demasiado largo en móvil.
- Falta de foto o assets personales iniciales.

## 7) Technical notes

### Backend
- N/A.

### Frontend
- Layout responsive y legible.

### API/Contract
- N/A.

## 8) Risks and dependencies

- Risk: about genérico que no aporta confianza real.
- Dependency: definición de narrativa personal.
- Mitigation: copy concreto con hechos y enfoque.

## 9) Rollout and rollback

- Rollout: publicar v1 y medir interacción básica.
- Rollback: simplificar bloques si baja legibilidad.
