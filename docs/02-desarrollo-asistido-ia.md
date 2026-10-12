# Desarrollo Asistido por IA

> Este documento describe cómo se usa la IA en este proyecto: qué agentes y skills existen, cómo se trabaja con ellos y qué beneficios aportan para la claridad y el mantenimiento del código.

## 1. Principios de trabajo

1. **Persona en el centro, IA como apoyo trazable.** Ningún código generado entra sin revisión y sin test que lo respalde. Cada decisión relevante queda en `docs/01-decisiones-tecnicas.md` y cada prompt relevante en `docs/PROMPTS.md`, de modo que el porqué queda registrado.
2. **Tareas pequeñas y verificables.** La IA funciona mejor con pasos acotados (contrato → base → cliente → servicio → resiliencia → rendimiento) que con “genera la aplicación”.
3. **Skills reutilizables en lugar de prompts sueltos.** Lo que se repite (contrato OpenAPI, timeouts, pruebas de carga) se guarda como skill versionada en el repo, no en chats que se pierden.
4. **Primero verificar, después dar por hecho.** Todo lo que produce la IA debe pasar `mvn verify`, tests con WireMock y prueba de carga real. Si no pasa, se itera.
5. **Todo queda en el repo.** La estructura `.opencode/`, el documento de decisiones y la trazabilidad prompt→código→test dejan registrado el flujo completo.

## 2. Agentes: qué son y por qué cada uno

Trabajamos con un **orquestador + 6 especialistas**. Cada uno tiene un rol claro, una entrada, una salida y un criterio de “hecho” para evitar solapes.

### 2.1 Orquestador (persona + agente principal)

- **Por qué existe:** sin coordinación, cada cambio de la IA puede contradecir otro (uno cambia el puerto, otro el timeout). Centraliza el plan y resuelve conflictos.
- **Cómo trabaja:** mantiene la lista de tareas, lanza subagentes y exige verificación ejecutada (salida de tests, no afirmaciones).
- **Qué aporta:** un único lugar donde ver el estado del trabajo y qué se hizo en cada paso.

### 2.2 `contract-analyst` — analista de contratos

- **Por qué:** el error más caro sería malinterpretar `similarProducts.yaml` frente a `existingApis.yaml` (por ejemplo, confundir `similarids: string[]` con `similar: ProductDetail[]`, u olvidar cuándo devolver 404).
- **Entrada:** los 2 yaml + `mocks.json` + `test.js`. **Salida:** tabla de casos + reglas de mapeo de errores + esquema de DTOs.
- **Cuándo se usa:** al principio y cada vez que cambia el contrato. Su salida alimenta al resto.
- **Qué aporta:** elimina ambigüedad desde el inicio; la semántica del contrato queda fijada antes de implementar.

### 2.3 `spring-architect` — diseño base

- **Por qué:** hay decisiones difíciles de revertir (Java 21, Boot 3.3, virtual threads, `RestClient`, timeout 2s, capas finas). Conviene tomarlas una vez y dejarlas escritas.
- **Entrada:** tabla del analista + restricciones (puerto 5000, carga concurrente). **Salida:** `docs/01-decisiones-tecnicas.md`, `pom.xml`, `application.yaml`, estructura de paquetes.
- **Qué aporta:** base que compila y decisiones justificadas por escrito antes de escribir código de negocio.

### 2.4 `implementer` — implementación con tests

- **Por qué:** es quien más código genera y donde más fácil es introducir “código que parece correcto pero falla”. Se limita con desarrollo guiado por tests y alcance por fichero.
- **Cómo trabaja:** por incrementos (`dto → client → service con paralelo+timeout → controller → handler`). Cada incremento = generar + `mvn -q test` + corregir. No se avanza con tests en rojo.
- **Qué aporta:** el código nace con tests que reproducen `mocks.json`; el comportamiento queda documentado con ejemplos ejecutables.

### 2.5 `resilience-tester` — pruebas de fallos y lentitud

- **Por qué:** el comportamiento ante 404/500/retardos hay que provocarlo, no suponerlo.
- **Cómo trabaja:** levanta WireMock con los escenarios de `mocks.json` (retardos 100ms/1s/5s/50s y estados 404/500). Comprueba: se filtran 404/500, se preserva el orden, `slow` responde en <2.5s, `verySlow` no supera 3s, `similarids` con 404 → 404.
- **Qué aporta:** convierte “debe ser resistente” en pruebas concretas con tiempos y criterios ejecutables.

### 2.6 `perf-tuner` — ajuste bajo carga

- **Por qué:** algo que funciona con 1 petición puede degradarse con muchas concurrentes. Este rol solo ajusta pool/timeouts/caché con datos reales.
- **Cómo trabaja:** `docker-compose up -d simulado` → `curl :3001` → `k6 run` → revisa p95/tasa de error en Grafana `:3000` → ajusta un parámetro (`pool.max`, `timeout`, `cache.ttl`) → repite. Un cambio cada vez.
- **Qué aporta:** evita optimización a ciegas: solo se cambia lo que las métricas muestran.

### 2.7 `ai-reviewer` — revisión y documentación

- **Por qué:** conviene que quien revisa use criterios distintos a quien escribe (claridad, nombres, responsabilidad única, logs útiles, sin valores mágicos).
- **Cómo trabaja:** aplica checklist (¿método de más de 30 líneas? ¿timeout escrito a mano? ¿log con contexto?), propone refactors mínimos y genera `docs/PROMPTS.md` y actualiza `docs/` (el `readme.md` es el original de la prueba y no se reescribe).
- **Qué aporta:** mantiene la homogeneidad del código: claridad, nombres consistentes, responsabilidad única y logs útiles.

## 3. Skills: genéricas + instanciadas por proyecto

Las skills son guías versionadas en `.opencode/skills/<nombre>/SKILL.md` con: *cuándo usar, inputs, pasos genéricos, errores típicos y verificación*. La parte genérica no cambia entre proyectos; los valores concretos viven en `.opencode/project.yaml` y en el apartado “Instancia en este proyecto” de cada skill.

| Skill | Problema que evita | Patrón genérico | Instancia aquí (`project.yaml`) |
|---|---|---|---|
| `openapi-first` | DTOs que no coinciden con el contrato | Mapear schemas a tipos inmutables + normalización + test de contrato | `ProductDetail`, normalización número/string → string |
| `java21-virtual-threads` | Hilos o conexiones mal dimensionados | Hilos virtuales + pool + timeouts desde config + fan-out con timeout | Puerto 5000, pool 200, 500ms/2000ms |
| `resilience-filter-timeout` | Fallar todo ante un error parcial o esperar sin límite | Fallo de colección → error; fallo de elemento → filtrar; orden; timeout configurable | `similarids` 404 → 404; detalle 404/500/timeout → filtrar; 2s |
| `wiremock-mirror` | Probar contra aproximaciones en vez de la realidad | Un stub por fixture con body/status/delay exactos + asserts con umbral | `mocks.json` exacto, umbrales 2.5s/3.0s |
| `k6-verify` | Dar por bueno algo sin probarlo bajo carga | Dependencias → smoke → app → carga → p95 | Comandos y dashboard en `project.yaml` |
| `decision-record` | Decisiones sin contexto escrito | Plantilla contexto → opciones → decisión → consecuencias + disparadores | Documento `01-decisiones-tecnicas.md` |
| `java-review-checklist` | Revisiones con criterios dispares | Checklist homogéneo + build en verde | `record` + validación, tests espejo |

Sin skills, el conocimiento quedaría en chats sueltos. Con skills genéricas + `project.yaml`, queda guardado, se audita y se reutiliza en el siguiente proyecto cambiando un solo fichero.

## 4. Comandos y estructura en el repo (cómo se usa en la práctica)

Estructura prevista:

```
.opencode/
  project.yaml   # único fichero con valores concretos del proyecto
  agents/{orchestrator,contract-analyst,spring-architect,implementer,resilience-tester,perf-tuner,ai-reviewer}.md  # roles genéricos
  skills/{openapi-first,java21-virtual-threads,resilience-filter-timeout,wiremock-mirror,k6-verify,decision-record,java-review-checklist}/SKILL.md  # patrón + instancia
  commands/{contract-check.md,scaffold.md,resilience-test.md,perf-run.md,ai-review.md}
docs/
  01-decisiones-tecnicas.md
  02-desarrollo-asistido-ia.md (este documento)
  03-analisis-contrato.md
  PROMPTS.md
```

Reutilizar en otro proyecto = copiar `.opencode/` y cambiar solo `project.yaml` + el documento de análisis. Agentes y skills no se tocan.

> Nota de entrega: `.opencode/` está excluido del repo vía `.gitignore` para una entrega limpia; disponible bajo petición.

Convención de idioma: `.opencode/` (agentes, skills, comandos) y el código van en inglés para reutilización entre proyectos; `docs/` se mantiene en español.

**Flujo habitual:**
1. `contract-analyst` (skills `openapi-first` + `wiremock-mirror`, comando `/contract-check`) → tabla de casos revisada por una persona. Punto de control: ¿son correctas las reglas? Si no, no se continúa.
2. `spring-architect` → base + decisiones. Punto de control: build con tests en verde.
3. `implementer` en ciclos cortos (skills `java21-virtual-threads` + `resilience-filter-timeout`) → cada fichero con su test. Punto de control: tests en verde.
4. `resilience-tester` (comando `/resilience-test`) → informe de fallos. Si hay rojo, se vuelve al paso 3.
5. `perf-tuner` (comando `/perf-run`) → solo con prueba de carga real, un ajuste por iteración.
6. `ai-reviewer` (comando `/ai-review`) → refactors mínimos + `PROMPTS.md` + README.

**Reglas de trabajo:** un agente = un conjunto de cambios acotado; ningún agente modifica lo que otro dejó estable sin pasar por el orquestador; todo cambio incluye su verificación ejecutada (salida de `mvn`/`k6`).

## 5. Qué beneficios aporta esta forma de trabajar

- **Menos idas y vueltas:** analizar el contrato y diseñar la base una vez evita rehacer código después.
- **Resistencia comprobada desde el inicio:** los escenarios de fallos y lentitud existen como tests WireMock antes que el código final.
- **Rendimiento con datos:** en lugar de afirmar que “va bien”, se muestran p95 y tasa de error de Grafana antes y después de cada ajuste.
- **Claridad estructural:** decisiones + skills genéricas + checklist mantienen el proyecto ordenado sin depender de memoria externa.
- **Reutilización:** las skills son genéricas; el siguiente proyecto reutiliza `.opencode/` cambiando solo `project.yaml`. Los prompts sueltos se pierden; las skills quedan.
- **Trazabilidad:** `docs/PROMPTS.md` (prompt → resultado → verificación) permite revisar cómo se llegó a cada parte del código.

## 6. Riesgos del uso de IA y cómo se controlan

| Riesgo | Cómo se controla |
|---|---|
| La IA propone una API que no existe en el stack | Las skills leen el stack de `project.yaml` y cada generación debe compilar |
| Propuesta demasiado compleja para este tamaño (~14 clases main) | El diseño fija capas finas y lo deja escrito en el documento de decisiones; la revisión penaliza ficheros innecesarios |
| Tests poco exigentes que siempre pasan | Se usan fixtures exactas con retardos y estados reales, con aserciones de tiempo según umbrales de `project.yaml` |
| Pérdida de contexto entre agentes | El orquestador es el único que cruza salidas; cada agente recibe solo su entrada mínima |
| Confianza ciega en la IA | Puntos de control con revisión humana en contrato, diseño y resultado de carga. La IA propone, la persona decide |
