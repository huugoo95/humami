# Spec 005: blog-section-foundation

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-10

## 1) Problem

La web no tiene una sección de blog estructurada para captar tráfico orgánico y publicar contenido recurrente.

## 2) Goal and metric

- Goal: crear apartado Blog con estructura estándar y mantenible.
- Success metric: publicar el primer artículo en producción con plantilla reutilizable.

## 3) Scope

### In scope
- Ruta índice de blog (`/blog`).
- Plantilla de post (`/blog/[slug]`).
- Estructura de contenido (frontmatter + cuerpo).
- Metadatos SEO base por post.

### Out of scope
- CMS externo.
- Sistema avanzado de comentarios.

## 4) User stories

- As a visitor, I want to browse articles, so that I can discover Humami content easily.

## 5) Acceptance criteria (Given / When / Then)

1. Given un usuario en `/blog`
   When carga la página
   Then ve listado de posts ordenados por fecha.

2. Given un post publicado
   When accede a `/blog/[slug]`
   Then ve título, fecha, autor, contenido y metadatos.

3. Given un slug inexistente
   When intenta abrirlo
   Then recibe 404 consistente.

## 6) Edge cases

- Post sin imagen destacada.
- Slugs duplicados.

## 7) Technical notes

### Backend
- N/A (si se arranca file-based).

### Frontend
- Render estático si es posible para rendimiento/SEO.

### API/Contract
- Si luego hay API de posts, mantener contrato compatible.

## 8) Risks and dependencies

- Risk: deuda técnica al migrar a CMS futuro.
- Dependency: decisión formato contenido (MD/MDX/JSON).
- Mitigation: abstraer capa de lectura.

## 9) Rollout and rollback

- Rollout: publicar con 1–3 posts iniciales.
- Rollback: ocultar navegación Blog manteniendo rutas.
