# Spec 003: tests-and-ci-baseline

- **Status:** Draft
- **Owner:** Hugo + tenacitas
- **Created:** 2026-03-10

## 1) Problem

No hay una malla mínima garantizada para evitar regresiones en cada cambio de backend/frontend.

## 2) Goal and metric

- Goal: establecer una base de tests automáticos y CI obligatoria antes de merge.
- Success metric: PR bloqueada si fallan tests/lint/build.

## 3) Scope

### In scope
- Definir pirámide mínima de tests (unit + integración crítica).
- Añadir tests en rutas críticas (creación receta/subida imagen/flujo FE de creación).
- Pipeline CI en GitHub Actions sin SaaS externo.

### Out of scope
- E2E visual compleja.
- Performance testing avanzado.

## 4) User stories

- As a maintainer, I want automatic validation in PRs, so that bad changes are caught before merge.

## 5) Acceptance criteria (Given / When / Then)

1. Given un PR a `develop`
   When se ejecuta CI
   Then corre lint + tests + build para backend y frontend.

2. Given un fallo en cualquier check
   When el autor intenta mergear
   Then el merge queda bloqueado por protección de rama.

3. Given cambios en feature crítica
   When se ejecutan tests
   Then hay cobertura de casos felices y de error.

## 6) Edge cases

- Flakiness en tests asíncronos.
- Diferencias de entorno local vs CI.

## 7) Technical notes

### Backend
- Mantener tests de servicio/controlador para endpoints críticos.

### Frontend
- Unit tests de lógica de submit y manejo de error.

### API/Contract
- Tests de contrato básico de respuesta/error.

## 8) Risks and dependencies

- Risk: pipelines lentas reducen velocidad del equipo.
- Dependency: scripts estables en package/maven.
- Mitigation: separar jobs y cachear dependencias.

## 9) Rollout and rollback

- Rollout: habilitar CI en PR y exigir checks requeridos.
- Rollback: desactivar temporalmente check no estable, manteniendo mínimos.
