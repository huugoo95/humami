# Spec 002: recipe-create-auth-secret

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-10

## 1) Problem

Actualmente la creación de recetas podría invocarse sin una protección robusta desde cliente, exponiendo riesgo de abuso (spam, costes, datos basura).

## 2) Goal and metric

- Goal: proteger endpoints de escritura (`POST /api/meals`, `PUT /api/meals/{id}/image`) con un secreto de servidor fuera del repo.
- Success metric: 100% de llamadas de escritura rechazadas sin credencial válida en entornos deployados.

## 3) Scope

### In scope
- Secret por entorno (local/staging/prod) vía variables de entorno.
- Middleware/filtro de autorización para endpoints de escritura.
- Respuestas estándar 401/403.
- Documentación de setup seguro (sin exponer valor en repo).

### Out of scope
- Sistema completo de usuarios/roles.
- OAuth/JWT por usuario final.

## 4) User stories

- As a project owner, I want recipe creation endpoints protected, so that only authorized clients can write data.

## 5) Acceptance criteria (Given / When / Then)

1. Given una petición a `POST /api/meals` sin cabecera secreta
   When llega al backend
   Then responde 401/403 y no crea recursos.

2. Given una petición con secreto válido
   When llega al backend
   Then procesa normalmente y devuelve respuesta esperada.

3. Given repositorio público/compartido
   When se revisa código y commits
   Then no existe ningún secreto hardcodeado.

## 6) Edge cases

- Secret vacío o no configurado en deploy.
- Rotación de secret sin downtime.
- Diferente secret por entorno.

## 7) Technical notes

### Backend
- Validación de cabecera tipo `X-HUMAMI-SECRET` (nombre final por definir).
- Aplicación a rutas de escritura únicamente.

### Frontend
- Nunca exponer el secret en cliente público.
- Escrituras desde server-side route/proxy seguro si aplica.

### API/Contract
- Definir errores de auth consistentes (`code`, `message`).

## 8) Risks and dependencies

- Risk: falsa sensación de seguridad si secret se filtra en logs.
- Dependency: configuración correcta de variables en entorno de despliegue.
- Mitigation: enmascarar logs + guía de rotación.

## 9) Rollout and rollback

- Rollout: activar validación en staging, validar, activar en prod.
- Rollback: feature flag temporal para desactivar validación en emergencia.
