# Trazabilidad IA — prompt → resultado → verificación

Registro de cómo se produjo cada parte con asistencia de IA. Los agentes, skills y comandos genéricos viven en `.opencode/`; los valores concretos en `.opencode/project.yaml`.

## Paso 1 — Contrato (`contract-analyst` + `openapi-first`)

- Prompt: analizar `similarProducts.yaml`, `existingApis.yaml`, `mocks.json`, `test.js` y devolver endpoints, matriz de casos, esquema de DTOs y ambigüedades.
- Resultado: `docs/03-analisis-contrato.md` (tabla de endpoints, matriz C1–C6, `ProductDetail`, 5 reglas).
- Verificación: validación humana del checkpoint 1. Hallazgo clave aplicado: `similarids` llega como números `[2,3,4]` aunque el yaml diga `string[]` → normalización número/string a string.

## Paso 2 — Base (`spring-architect`, comando `/scaffold`)

- Prompt: scaffold Boot 3.3 + Java 21 + Maven con puerto 5000, hilos virtuales, timeouts, pool y caché.
- Resultado: `pom.xml`, `application.yaml`, `SimilarProductsApplication`, `port/ProductCatalogPort`, `dto/ProductDetail`, test de contexto.
- Verificación: `mvn verify` en verde. Entorno comprobado: Java 21.0.10, Maven 3.9.1, Docker 28.1.1 (daemon parado, ver paso 4).

## Paso 3 — Implementación (`implementer`)

- 3a/3b: DTO y puerto ya existían de la base; se verificaron contra el análisis.
- 3c (`openapi-first` + `java21-virtual-threads`): `config/ProductProperties`, `config/RestClientConfig` (pool 600 —subido desde 200 tras medir cola bajo 200 VUs, ver paso 8—, 500/2000ms), `exception/*`, `infrastructure/rest/RestProductClient` (normalización, 404→vacío, caché Caffeine de éxitos).
  - Verificación: `RestProductClientTest` (7 tests WireMock standalone) en verde. Incidencia: `org.wiremock:wiremock` modular falla sin Jetty → se cambió a `wiremock-standalone:3.9.1`.
- 3d (`java21-virtual-threads` + `resilience-filter-timeout`): `service/SimilarService` (fan-out virtual + `orTimeout` + filtrado + orden + dedupe + log por agregación).
  - Verificación: `SimilarServiceTest` (5 tests Mockito) en verde.
- 3e (`openapi-first` + `java-review-checklist`): `api/SimilarController` + `api/GlobalExceptionHandler` (404/502/400).
  - Verificación: `SimilarControllerTest` (3 tests MockMvc) en verde.
- Incidencia: `SimilarService` con dos constructores rompía la inyección (`No default constructor found`) → se anotó el principal con `@Autowired`.
- Escalabilidad: se añadió el puerto de entrada `application/port/in/SimilarProductsUseCase`; el controller depende de la interfaz. Ver `docs/01 §6`.

## Paso 4 — Resiliencia (`resilience-tester` + `wiremock-mirror`)

- Prompt: flujo completo contra stubs exactos de `mocks.json`.
- Resultado: `SimilarFlowTest` (6 tests, corre dentro de `mvn verify`): C1 `[2,3,4]` en ~300ms, C4/C5 filtran 404/500, C6 propaga 404, C2 `[3,100]` en ~2s, C3 `[100]` en ~2s.

## Paso 5 — Verificación contra simulado real

- Smoke: `curl :3001/product/1/similarids` → `[2,3,4]`. App en `:5000` con `actuator/health UP`.
- Casos reales: `/1` → `[2,3,4]` (~435ms), `/4` → `[1,2]`, `/5` → `[1,2]`, `/2` → ~2036ms (filtra el de 5s), `/3` → ~2040ms (filtra 5s/50s), `/999` → 404.

## Paso 6 — Revisión (`ai-reviewer` + `java-review-checklist`)

- Checklist aplicado: métodos ≤30 líneas (se extrajeron `validate/dedupe/fetchInParallel/collect` en `SimilarService`), sin literales de configuración en código, nombres de dominio, `record` + validación, un log por agregación, tests espejo con umbrales, docs actualizados (`01 §6/§6.1`, `02`, `03`).
- Post-cierre: `RequestConfig.setConnectTimeout` deprecado → movido a `ConnectionConfig` (+ `connectionRequestTimeout` acotado); `SimilarFlowIT` renombrado a `SimilarFlowTest` para que surefire lo incluya en `verify`.

## Paso 7 — Mejoras

- Caché manual eliminada: `config/CacheConfig` (`CaffeineCacheManager`) + `@Cacheable("details")` en `getProduct` (los vacíos no se cachean). Incidencia: el `unless` con `#result.isEmpty()` falla porque Spring desempaqueta el `Optional` → se dejó `unless = "#result == null"`. `RestProductClientCacheTest` con `@SpringBootTest` + `DynamicPropertySource`: éxitos con 1 sola llamada aguas abajo y vacíos sin cachear con 2 llamadas (`emptyResultIsNotCached`).
- Executor virtual como bean (`config/TaskExecutorConfig`, `destroyMethod="close"`) inyectado en `SimilarService`; se mantiene el constructor de test. Cierra la limitación anotada en el paso 6.
- Spotless (`palantirJavaFormat`) con `check` en `verify`.
- Verificación final: `mvn clean verify` en verde (24 tests, 0 fallos, sin avisos de deprecación).

## Paso 8 — Ajuste de pool bajo carga (`perf-tuner` + `k6-verify`)

- Hallazgo: con `max-connections: 200`, el run completo de `k6/test.js` daba en `verySlow` med 3.48s / p95 4.02s (el doble del timeout de 2s) + 3×502. Causa: 200 VUs × 3 detalles en fan-out = 600 conexiones concurrentes contra un pool de 200 → cola en `connectionRequestTimeout`.
- Cambio: `application.yaml` → `max-connections: 600` (+ justificación en `docs/01 §4`).
- Verificación: run aislado `GET /product/3/similar` (200 VUs, 15s, script temporal borrado tras medir): med 2.02s, p95 2.81s (< 3s), max 2.91s, 1200/1200 → 200, 0 interrumpidas.
