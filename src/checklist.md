from pathlib import Path

# Contenido del checklist en formato Markdown
checklist_content = """### ✅ Backend Technical Test - Checklist de Entrega

**👤 Autor:** Nauzet López Mendoza  
**📦 Repositorio:** [https://github.com/Nau-c/backendDevTest](https://github.com/Nau-c/backendDevTest)

---

#### ✅ Funcionalidad principal

- [x] Endpoint REST `GET /product/{productId}/similar` creado según el contrato acordado.
- [x] La API responde correctamente en el puerto `5000`.
- [x] Se consumen los endpoints mock:
  - `/product/{id}/similarids`
  - `/product/{id}`
- [x] Se devuelve una lista con los detalles completos de los productos similares.

---

#### ✅ Arquitectura y buenas prácticas

- [x] Proyecto desarrollado con **Java 21** y **Spring Boot 3.2.5**.
- [x] Estructura limpia por capas:
  - `controller/`
  - `service/`
  - `client/`
  - `model/`
  - `exception/`
- [x] Código claro, legible y mantenible.

---

#### ✅ Infraestructura y ejecución

- [x] Uso de `docker-compose` para levantar entorno de mocks y pruebas:
  - `simulado`
  - `influxdb`
  - `grafana`
- [x] Documentación detallada en el `README.md` para facilitar la ejecución local.

---

#### ✅ Resiliencia

- [x] Manejo de errores con `@ControllerAdvice`.
- [x] Excepciones personalizadas (`ProductNotFoundException`) y respuestas estructuradas.

---

#### ✅ Testing

- [x] Implementados tests **unitarios** utilizando **Mockito** (`SimilarProductServiceTest`).
- [x] Implementado test de **integración** con `@SpringBootTest` (`SimilarProductControllerIntegrationTest`).

---

#### ✅ Extras

- [x] Preparado para pruebas de rendimiento con `k6`.
- [x] Compatible con compilación vía `mvn` o `./mvnw`.
- [x] Cumple todos los criterios indicados en el enunciado.

---

> ✅ **Estado final:** Listo para entrega
"""

