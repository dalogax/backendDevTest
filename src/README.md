# 📦 Similar Products API - Backend Technical Test

Este proyecto es una solución para la prueba técnica de backend de **Nunegal Consulting**, desarrollada con **Java 21** y **Spring Boot 3.2.5**. El objetivo es exponer un endpoint que devuelva los productos similares a uno dado, consumiendo datos desde un mock API.

---

## 🚀 Cómo ejecutar la aplicación

### 1. Clonar el repositorio

```bash
git clone https://github.com/<TU_USUARIO>/backendDevTest.git
cd backendDevTest


2. Ejecutar el mock y entorno de pruebas con Docker

docker-compose up -d simulado influxdb grafana

📌 Esto levantará:

El mock del API en: http://localhost:3001

Grafana en: http://localhost:3000

3. Ejecutar la API de productos similares

./mvnw spring-boot:run

📌 La API se ejecutará en: http://localhost:5000

📡 Endpoint principal

GET /product/{productId}/similar

Ejemplo:

GET http://localhost:5000/product/1/similar

Respuesta:

[
  {
    "id": "2",
    "name": "Shirt",
    "price": 9.99,
    "availability": true
  },
  {
    "id": "3",
    "name": "Shoes",
    "price": 19.99,
    "availability": true
  }
]

🧪 Test de rendimiento (opcional)
Con los mocks levantados, puedes lanzar las pruebas de rendimiento con:

docker-compose run --rm k6 run scripts/test.js

http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

🗂️ Estructura del proyecto

src/
├── main/
│   ├── java/com/nunegal/backendDevTest/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── model/
│   │   ├── client/
│   │   └── exception/
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/nunegal/backendDevTest/

📦 Requisitos
Java 21

Maven 3.8+ o ./mvnw

Docker

Docker Compose

✅ Pendiente (si se desea mejorar)
Añadir más validaciones y control de errores

Mejorar cobertura de test unitario y de integración

Manejo más robusto de excepciones HTTP del cliente

✍️ Autor
Desarrollado por Nauzet López Mendoza para el proceso de selección de Nunegal Consulting.