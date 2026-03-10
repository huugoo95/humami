# Spec 006: seo-google-indexing-baseline

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-10

## 1) Problem

Sin fundamentos técnicos SEO, Google no indexará correctamente páginas clave.

## 2) Goal and metric

- Goal: implementar base SEO técnica para indexación en Google.
- Success metric: páginas clave enviadas y detectadas en Search Console; sitemap y robots válidos.

## 3) Scope

### In scope
- `robots.txt` correcto.
- `sitemap.xml` actualizado.
- Metadatos título/description por página clave.
- Preparar alta y verificación en Google Search Console.

### Out of scope
- Estrategia avanzada de link building.
- SEO internacional multi-idioma.

## 4) User stories

- As a site owner, I want Google to discover and index my pages, so that I can get organic traffic.

## 5) Acceptance criteria (Given / When / Then)

1. Given acceso a dominio en producción
   When Googlebot rastrea
   Then `robots.txt` permite indexar páginas públicas clave.

2. Given sitemap publicado
   When se consulta
   Then incluye URLs canónicas válidas.

3. Given alta en Search Console
   When se envía sitemap
   Then queda aceptado sin errores críticos.

## 6) Edge cases

- Entorno staging indexado por error.
- URLs duplicadas por parámetros.

## 7) Technical notes

### Backend
- N/A (según stack actual).

### Frontend
- Canonical tags y metadatos por página.

### API/Contract
- N/A.

## 8) Risks and dependencies

- Risk: bloquear accidentalmente indexación.
- Dependency: acceso a DNS/Search Console.
- Mitigation: checklist pre-producción.

## 9) Rollout and rollback

- Rollout: activar SEO base y enviar sitemap.
- Rollback: revertir reglas robots conflictivas.
