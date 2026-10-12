# Análisis de contrato — Paso 1

Fuentes: `similarProducts.yaml`, `existingApis.yaml`, `shared/simulado/mocks.json`, `shared/k6/test.js`.

## 1. Endpoints

| Origen | Método y path | Petición | Respuesta 200 | Otros códigos |
|---|---|---|---|---|
| A exponer `:5000` | `GET /product/{productId}/similar` (`productId` path string requerido) | Sin body | `ProductDetail[]` ordenado, admite vacío, elementos únicos | `404` si el producto base no existe |
| Dependencia `:3001` | `GET /product/{productId}/similarids` | Sin body | `string[]` ordenado, admite vacío, elementos únicos | `404` si el id no existe (comportamiento observado del simulado, no documentado en el yaml) |
| Dependencia `:3001` | `GET /product/{productId}` | Sin body | `ProductDetail` | `404` documentado; en la práctica también `500`, retardos y cuerpo vacío |

Relación: el endpoint `:5000` es una agregación: pedir ids y luego N detalles en paralelo preservando el orden.

Nota de tipos: el yaml declara `similarids` como `string[]` con ejemplo `["1","2","3"]`, pero los mocks devuelven números `[2,3,4]`. El servicio acepta ambos y normaliza a string (`2` → `"2"`, con `trim`, descartando vacíos/nulos).

## 2. Matriz de casos

| Caso | Entrada `:5000` | Datos `:3001` (con retardo) | Salida `:5000` | Regla aplicada |
|---|---|---|---|---|
| C1 directo | `/product/1/similar` | `similarids [2,3,4]`; `/2` ok, `/3` ok 100ms, `/4` ok | `200` con `[2,3,4]` en orden | Sin errores |
| C2 mixto lento | `/product/2/similar` | `similarids [3,100,1000]`; `/3` 100ms, `/100` 1s, `/1000` 5s | `200` parcial, ej. `[3,100]` si el timeout filtra `1000` | Fallo/timeout de detalle: filtrar elemento |
| C3 muy lento | `/product/3/similar` | `similarids [100,1000,10000]`; 1s, 5s, 50s | `200` parcial o `[]` según timeout; nunca cuelga 50s | Timeout: filtrar; si todo filtra: `200 []` |
| C4 detalle inexistente | `/product/4/similar` | `similarids [1,2,5]`; `/1,/2` ok, `/5` 404 | `200` con `[1,2]` | 404 de detalle: filtrar |
| C5 detalle con error | `/product/5/similar` | `similarids [1,2,6]`; `/1,/2` ok, `/6` 500 | `200` con `[1,2]` | 500/cuerpo vacío de detalle: filtrar |
| C6 base inexistente | `/product/999/similar` (sin mock de `similarids`) | `similarids` 404, sin llamadas a detalle | `404` | 404 de `similarids`: propagar `404` |

Aclaraciones:
- `similarids: []` o todos los detalles filtrados → `200 []`, no 404.
- Duplicados en `similarids` → deduplicar por id normalizado preservando el primer orden.

## 3. Esquema `ProductDetail`

```java
record ProductDetail(
  String id,           // requerido, no vacío (tras trim). Acepta número de entrada, expone string
  String name,         // requerido, no vacío
  BigDecimal price,    // requerido, número. Evita error binario de double con 9.99
  Boolean availability // requerido. Ausencia: filtrar elemento, no asumir false
)
```

Reglas de mapeo:
- `FAIL_ON_UNKNOWN_PROPERTIES=false` (la dependencia puede añadir campos).
- Elemento que incumple requeridos o con JSON inválido: se filtra, no rompe la lista.
- Respuesta siempre en el orden de `similarids`, con ids como string.

## 4. Reglas fijadas para el checkpoint

1. `404` de `similarids` → `404`. `404`/`500`/timeout/conexión rota de detalle → filtrar.
2. Timeout por detalle 2s (configurable `app.product.timeout-ms`), llamadas en paralelo, respuesta reordenada al orden original.
3. Sin reintentos en el fan-out de detalles.
4. `productId` se propaga tal cual como string, sin validar formato numérico.
5. Error `404` con cuerpo mínimo propio, sin reenviar el cuerpo de la dependencia.
