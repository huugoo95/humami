# Spec 004: simple-cd-safe-deploy

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-10

## 1) Problem

No hay flujo CD definido que garantice despliegues consistentes y reversibles sin herramientas externas complejas.

## 2) Goal and metric

- Goal: definir CD simple con validaciones previas y rollback rápido.
- Success metric: cada deploy ejecuta checks y tiene procedimiento de rollback documentado y probado.

## 3) Scope

### In scope
- Workflow de deploy desde `master`.
- Pre-checks obligatorios (build/tests).
- Estrategia mínima de rollback.
- Guía operativa en docs.

### Out of scope
- Blue/green avanzado.
- Observabilidad enterprise.

## 4) User stories

- As an owner, I want repeatable deploy steps, so that a bad release can be reverted quickly.

## 5) Acceptance criteria (Given / When / Then)

1. Given un merge a `master`
   When se dispara CD
   Then se valida build/tests antes de desplegar.

2. Given fallo de healthcheck post-deploy
   When se detecta el fallo
   Then se ejecuta rollback según runbook.

3. Given incidencia en producción
   When se consulta documentación
   Then existe un procedimiento corto y claro para recuperar servicio.

## 6) Edge cases

- Deploy parcial (frontend ok, backend no).
- Secret de entorno ausente.

## 7) Technical notes

### Backend
- Health endpoint y smoke test mínimo.

### Frontend
- Build reproducible y smoke de ruta principal.

### API/Contract
- Compatibilidad backward durante release.

## 8) Risks and dependencies

- Risk: automatizar mal rollback y agravar incidencia.
- Dependency: acceso CI a servidor/infra.
- Mitigation: manual override documentado.

## 9) Rollout and rollback

- Rollout: habilitar CD en staging, luego prod.
- Rollback: volver a release anterior + invalidar cachés si aplica.
