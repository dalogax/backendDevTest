# Decisiones Técnicas — Servicio Similar Products

> Servicio: API `GET /product/{productId}/similar :5000` que orquesta `GET /similarids` + fan-out `GET /product/{id} :3001`.
> Stack: **Java 21 + Spring Boot 3.3 + Maven**.
> Principios guía: **claridad del código, buen comportamiento bajo carga y degradación parcial ante fallos**.

## 1. Qué hace el sistema y qué comportamientos debe cubrir

Fuentes: `readme.md`, `similarProducts.yaml`, `existingApis.yaml`, `shared/k6/test.js` y `shared/simulado/mocks.json`.

Flujo básico:
1. Llega `GET /product/{id}/similar`.
2. Se piden los ids similares con `GET /product/{id}/similarids`.
3. Por cada id se pide su detalle con `GET /product/{id}` en paralelo.
4. Se devuelve la lista de detalles en el mismo orden recibido.

Casos que el sistema debe manejar (extraídos de `mocks.json` y `test.js`):

| Caso | Datos de ejemplo | Comportamiento del servicio |
|---|---|---|
| Normal → `/1/similar` | `[2,3,4]` rápidos | Devuelve los 3 detalles, orden preservado, `200` |
| Detalle inexistente → `/4/similar` | `[1,2,5]`, `5=404` | Filtra el id con 404 y devuelve `[1,2]` con `200`. El fallo de un detalle no rompe la respuesta |
| Detalle con error → `/5/similar` | `[1,2,6]`, `6=500` | Filtra el id con 500 y devuelve `[1,2]` con `200` |
| Detalles lentos → `/2/similar` | `[3=100ms, 100=1s, 1000=5s]` | No espera 5s por el más lento. Con timeout ~2s devuelve una respuesta parcial rápida |
| Detalles muy lentos → `/3/similar` | `[100=1s, 1000=5s, 10000=50s]` | Aplica timeout y descarta los que excedan, para no dejar hilos ocupados 50s |
| Producto base inexistente | `similarids` responde 404 | Devuelve `404`: el producto pedido no existe |

Idea clave para entender el diseño: este servicio es una **agregación con fan-out paralelo, timeouts y degradación parcial**.

## 2. Stack base: Java 21 + Spring Boot 3.3 + Maven

**Decisión:** Java 21 LTS, Boot 3.3.x, Maven.

**Por qué:**
- **Java 21** incluye Virtual Threads (JEP 444, final) y un `HttpClient` maduro. Permite escalar operaciones de espera I/O sin código reactivo ni pools manuales. Con Java 17 habría que dimensionar pools a mano.
- **Boot 3.3** ofrece soporte de primera clase para Virtual Threads (`spring.threads.virtual.enabled=true`) y `RestClient` (estable desde 3.2/3.3). Boot 2.x está fuera de soporte y no dispone de ninguno de los dos.
- **Maven** frente a Gradle: `pom.xml` declarativo + wrapper `mvnw` permite clonar y compilar sin instalar nada extra. Gradle aporta más en monorepos grandes; aquí (1 módulo, <20 clases) no compensa la complejidad adicional.

**Descartado:** Java 17 (sin virtual threads nativos), Boot 2.7 (fuera de soporte, `RestTemplate` en mantenimiento), Gradle Kotlin DSL (curva de aprendizaje innecesaria para este tamaño).

## 3. Modelo de concurrencia: MVC + Virtual Threads frente a WebFlux o pool manual

Es la decisión con más impacto en el comportamiento bajo carga.

### 3.1 Opción elegida: Spring MVC bloqueante + Virtual Threads

```properties
spring.threads.virtual.enabled=true
server.port=5000
```

Cada petición usa un virtual thread ligero. El código sigue siendo imperativo y fácil de seguir (`for + future.get()`), pero escala a muchas peticiones concurrentes sin configurar pools.

**Por qué encaja aquí:**
1. **Lectura sencilla.** Obtener cada detalle con `CompletableFuture` + `orTimeout` se entiende en pocas líneas. El equivalente reactivo requiere `Flux.flatMap + timeout + onErrorResume` y disciplina de schedulers.
2. **Carga orientada a espera I/O.** El cuello es esperar a `:3001`, no CPU. Los virtual threads eliminan la penalización clásica de “1 thread de plataforma por petición bloqueada”.
3. **Ecosistema bloqueante estándar:** `RestClient`, `Caffeine`, `Validation`, `Mockito/WireMock` funcionan sin adaptadores reactivos.
4. **Comportamiento bajo carga:** con picos de peticiones concurrentes y respuestas lentas (`slow/verySlow`), Tomcat + virtual threads absorbe la carga sin ajustes. Con threads de plataforma (200 por defecto) habría rechazos justo en esos casos.

### 3.2 Alternativa valorada: WebFlux reactivo (`WebClient` + `Flux`)

Ventaja: mayor rendimiento con menos memoria ante decenas de miles de conexiones con latencias extremas, y backpressure nativo.

**Por qué no se eligió:**
- Complejidad injustificada para un fan-out de 3 elementos por petición.
- Riesgo de bloquear el event-loop por error (un `.block()` accidental afecta a todo).
- Depuración y trazas más difíciles de seguir y de diagnosticar.
- `WebClient` está en modo mantenimiento frente a `RestClient` en el mundo bloqueante.
- Tendría sentido con streaming/SSE o decenas de miles de RPS sostenidos, que no es este caso.

### 3.3 Alternativa valorada: MVC + `CompletableFuture` con pool fijo (estilo Java 17)

Era el patrón clásico: `Executors.newFixedThreadPool(100)` para el fan-out.

**Por qué no se eligió:** obliga a dimensionar el pool a mano (¿50? ¿200?) y ante un detalle de 50s el pool se satura igual. Con virtual threads ese parámetro desaparece y el límite real pasa al timeout + pool de conexiones HTTP, que es donde debe estar.

> En resumen: **virtual threads ofrecen un rendimiento cercano al reactivo con un código casi síncrono**, el mejor equilibrio entre legibilidad y comportamiento bajo carga.

## 4. Cliente HTTP: `RestClient` + Apache HttpClient 5 con pool

**Decisión:** `RestClient` (Boot 3.3) con `HttpClient` y `PoolingHttpClientConnectionManager` (máx. 600 conexiones, keep-alive).

**Comparativa:**
- **`RestTemplate`:** en mantenimiento desde Boot 3, sin API fluida para timeouts por petición.
- **`WebClient`:** reactivo, obligaría a adaptar todo a `Mono`. Ver §3.2.
- **`RestClient`:** API moderna e inmutable, interceptores, `requestFactory` configurable, compatible con virtual threads. Permite `connectTimeout(500ms)` + `readTimeout(2s)` por defecto.

Timeouts (ver §5): connect 500ms, read 2s. El pool de 600 conexiones evita la cola bajo carga: con 200 VUs × 3 detalles en fan-out hay hasta 600 conexiones concurrentes a `:3001`; con 200 el `connectionRequestTimeout` añadía hasta ~2s extra (medido: `verySlow` p95 4s frente al timeout de 2s).

## 5. Estrategia ante fallos: filtrar fallos + timeout ~2s + preservar orden

**Reglas:**
- `GET /similarids` → 404 significa que el producto base no existe → se devuelve `404`. Un 5xx/timeout aquí es un fallo global (no hay nada que degradar) → `502` o `500`.
- Nota sobre el contrato: `similarProducts.yaml` solo define `200/404`. El `502` es una extensión deliberada para el fallo global de `similarids` (el contrato no contempla qué hacer si el upstream de colección cae); los fallos de detalle nunca dan error global porque se filtran con `200` parcial.
- `GET /product/{id}` en fan-out → `404`, `500`, `timeout` o error de conexión → **se filtra ese elemento** y se devuelve una respuesta parcial con `200`.
- Timeout por detalle: **2s**. Tiempo global de agregación ≈ 2s + pequeño margen.
- **Sin reintentos** en detalles: reintentar un detalle de 5s duplicaría la carga.
- Se **preserva el orden** de `similarids` al filtrar (requisito “ordered by similarity”).

**Por qué 2s:**
- `100=1s` debe entrar (es un caso “lento pero válido”). Con timeout de 500ms lo perderíamos y devolveríamos respuestas demasiado vacías.
- `1000=5s` y `10000=50s` deben quedar fuera. Con timeout de 6s cada petición lenta tardaría 6s y el p95 se dispararía con muchas peticiones concurrentes.
- 2s es el punto intermedio entre 1s y 5s que maximiza respuestas útiles sin alargar la latencia. Además es configurable (`app.product.timeout-ms=2000`) para ajustarlo sin tocar código.

**Descartado “todo o nada”:** devolver error global si un detalle falla haría que un solo producto roto rompiera los demás. Es un anti-patrón en agregación.

**Sobre circuit-breaker:** para 3 llamadas por petición el coste de estado no compensa. Se contempla `Resilience4j @TimeLimiter + @Bulkhead` ligero o un simple `orTimeout` como extensión futura, sin cambiar la API.

## 6. Arquitectura: hexagonal simplificada (puerto de entrada + puerto de salida)

Estado actual del código:

```
api/SimilarController
  → application/port/in/SimilarProductsUseCase (puerto de entrada)
    → service/SimilarService (caso de uso)
      → port/ProductCatalogPort (puerto de salida)
        → infrastructure/rest/RestProductClient (adapter)
+ dto / exception / config
```

- `SimilarController`: mapea `GET /product/{id}/similar`, valida que `productId` no esté vacío y delega en el puerto de entrada.
- `SimilarProductsUseCase`: puerto de entrada con `getSimilarProducts(id)`. Añadir un caso de uso (ej. batch) es una interfaz nueva sin tocar el controller.
- `SimilarService`: implementa el puerto de entrada y orquesta `getSimilarIds()` + fan-out en paralelo + filtrado + orden. Depende del puerto de salida, no del cliente REST.
- `ProductCatalogPort`: puerto de salida con `getSimilarIds(id)` y `getProduct(id)`. Punto de extensión para nuevas fuentes.
- `RestProductClient`: adapter REST con `RestClient`. Traduce el 404 a `Optional.empty()` o excepción tipada. Incluye caché Caffeine de éxitos.
- `GlobalExceptionHandler`: `ProductNotFound → 404`, `UpstreamError → 502`, `Validation → 400`.
- `dto/ProductDetail` como `record` inmutable con `jakarta.validation`.

**Por qué esta forma y no las alternativas:**
- Frente a capas sin puertos (`controller → service → client` directo): los 2 puertos cuestan 2 ficheros y aportan inversión de dependencias en ambos bordes. El controller se prueba mockeando el puerto de entrada y el servicio mockeando el de salida, sin HTTP real.
- Frente a hexagonal completa (puertos desdoblados, `application/service`, adapters de caché/REST separados, mappers): con una sola fuente y un solo caso de uso, desdoblar la salida y mover paquetes duplicaría ficheros sin aislar nada nuevo. Se adopta cuando haya retorno (ver §6.1).

### 6.1 Evolución futura hacia hexagonal completa (adopción condicionada)

Estado: parcialmente activado (entrada hecha, salida pendiente de desdoblar).

La entrada ya sigue el esquema objetivo (`application/port/in/SimilarProductsUseCase`). Queda documentado aquí cuándo y cómo completar la salida, para no pagar abstracción sin retorno.

**Opciones valoradas:**
1. Hexagonal completa desde el inicio: puertos desdoblados, `application/service`, adapters REST/caché separados, mappers.
2. Capas sin puertos: `controller → service → client` directo.
3. Compromiso adoptado: hexagonal simplificada (puerto de entrada + puerto de salida único).

**Condiciones de activación de la parte pendiente:** completar el desdoble si aparece alguno de estos disparadores:
- Segunda fuente de catálogo (ej. `catalog-v2`, Redis, fichero de respaldo).
- Segunda regla de dominio no trivial (scoring, deduplicación, personalización, impuestos).
- Segundo modo de entrada con lógica propia (ej. `POST /similar/batch` o eventos).
- Necesidad de aislar pruebas de dominio de la infraestructura HTTP de forma sistemática.

Mientras no se cumpla ninguno, el puerto de salida único cubre las necesidades con menor coste de lectura. La entrada ya queda en su ubicación final y no se mueve.

**Diseño objetivo pendiente (solo salida):**

```
application/port/in/SimilarProductsUseCase
application/port/out/SimilarIdsProvider
application/port/out/ProductDetailsProvider
application/service/SimilarService (opcional)
infrastructure/rest/RestSimilarIdsAdapter
infrastructure/rest/RestProductDetailsAdapter
infrastructure/cache/CaffeineProductDetailsAdapter
api/SimilarController
```

| Elemento | Estado |
|---|---|
| `SimilarProductsUseCase`, `SimilarController` | Hecho, no se mueve |
| `SimilarIdsProvider`, `ProductDetailsProvider` | Pendiente: desdoblar `ProductCatalogPort` aquí |
| `RestSimilarIdsAdapter`, `RestProductDetailsAdapter` | Pendiente: dividir `RestProductClient` en dos adapters |
| `CaffeineProductDetailsAdapter` | Pendiente solo si hace falta segunda fuente de detalle |
| Mover `SimilarService` a `application/service` | Opcional, sin cambio de comportamiento |

**Pasos de migración restantes (sin reescritura):**
1. Desdoblar `port/ProductCatalogPort` en `application/port/out/SimilarIdsProvider` + `ProductDetailsProvider`.
2. Dividir `infrastructure/rest/RestProductClient` en dos adapters (uno por puerto).
3. Extraer la caché a `infrastructure/cache/` como segundo adapter del puerto de detalles.
4. Mover `service/SimilarService` a `application/service/` si se quiere la distribución por paquetes hexagonal canónica (opcional, sin cambio de comportamiento).
5. Cada fuente nueva = un adapter nuevo + su test WireMock, sin tocar el caso de uso.

**Consecuencias:**
- Hoy: lectura lineal, dos puntos de extensión, refactor restante estimado en minutos.
- Cuando se complete: cada iteración toca un adapter, no el núcleo; el coste restante se paga cuando hay retorno.
- Si no se completa nunca: ningún efecto funcional; queda un puerto de salida con una sola implementación, justificado en este apartado.

## 7. Caché, observabilidad y configuración

- **Caché Caffeine vía Spring Cache (60s, máx. 1000) para detalles:** `@Cacheable("details")` en `getProduct` con `CaffeineCacheManager` (TTL/tamaño desde configuración). Solo guarda éxitos (los vacíos no se cachean); evita llamadas repetidas a `:3001`. Desactivable con el estándar `spring.cache.type=none` (sin flag propio). Nota: basta `unless = "#result == null"` porque Spring desenvuelve el `Optional` antes de evaluar la condición (un `Optional.empty` llega como `null`); no añadir `isPresent()` al SpEL — rompe la evaluación (verificado con `emptyResultIsNotCached`).
- **Actuator + Micrometer:** `/actuator/health,info,metrics` expuestos. Sin temporizadores propios del fan-out (solo log por agregación con `productId, total, filtrados, duración`); añadir `Timer/Counter` queda como evolución futura.
- **Configuración externalizada:** `app.product.base-url=http://localhost:3001`, timeouts y puerto 5000 en `application.yaml`. Necesario para Docker (`host.docker.internal` en tests de carga frente a `localhost` en local).
- **Logging:** un log por agregación con `productId, total, filtrados, duración`. Sin log por detalle para no inundar los logs bajo carga.

## 8. Cómo se comprueba

1. **Unitarios:** servicio con client simulado: filtra 404/500, preserva orden, propaga 404 de `similarids`.
2. **Integración con WireMock:** reproduce `mocks.json` con retardos (100ms/1s/5s) y estados (404/500). Criterios: caso `slow` responde en <2.5s con parcial, caso `verySlow` no supera 3s.
3. **Contrato:** valida estado + esquema contra `similarProducts.yaml`.
4. **Simulado local:** `docker-compose up -d simulado` + `curl :3001/product/1/similarids` (debe devolver `[2,3,4]`) + app en `:5000` + `curl` a los 6 casos (`/1`, `/2`, `/3`, `/4`, `/5` → `200`; `/999` → `404`). Es el procedimiento estándar para reproducir el comportamiento en local.

## 9. Resumen de equilibrios asumidos

| Decisión | Aporta | Cede | Cómo se compensa |
|---|---|---|---|
| Virtual threads frente a WebFlux | Legibilidad + buen comportamiento bajo carga | Rendimiento extremo teórico | Pool HTTP + timeout acotan la necesidad |
| Filtrar frente a fallar todo | Respuestas parciales útiles | Oculta errores individuales | Log + métrica `filtered.count` |
| Timeout 2s | Acota casos lentos/muy lentos | Descarta `1000` (5s) aunque exista | Valor configurable y documentado |
| Sin reintento | Menos carga aguas abajo | Un fallo transitorio no se reintenta | Asumido: sin reintentos en ningún punto (ni `similarids` ni fan-out) |
| Caché 60s | Mejor p95 con peticiones repetidas | Posible dato ligeramente desactualizado | TTL corto + solo éxitos |
| Capas simples | Lectura directa | Menos piezas de “arquitectura” | Se adopta hexagonal simplificada (§6), no capas sin puertos |

Con estas decisiones, el código se mantiene legible, responde parcial ante 404/500/lentitud y su comportamiento es reproducible con los comandos del apartado 8.
