#!/bin/bash

# Script para ejecutar los servicios necesarios

echo "🚀 Iniciando servicios..."
echo ""

killall java 2>/dev/null
killall python3 2>/dev/null
sleep 2

echo "1️⃣  Iniciando Mock Server (puerto 3001)..."
python3 /Users/javikuka/Documents/GitHub/backendDevTest/mock_server.py > /tmp/mock.log 2>&1 &
MOCK_PID=$!
sleep 3

if curl -s http://localhost:3001/health > /dev/null 2>&1; then
    echo "   ✅ Mock Server corriendo (PID: $MOCK_PID)"
else
    echo "   ❌ Error iniciando Mock Server"
    cat /tmp/mock.log
    exit 1
fi

# Iniciar Spring Boot
echo ""
echo "2️⃣  Iniciando Spring Boot (puerto 8080)..."
cd /Users/javikuka/Documents/GitHub/backendDevTest/app
java -jar target/app-1.0-SNAPSHOT.jar > /tmp/spring.log 2>&1 &
SPRING_PID=$!
sleep 8

# Verificar que Spring Boot esté corriendo
if curl -s http://localhost:8080/product/1/similar > /dev/null 2>&1; then
    echo "   ✅ Spring Boot corriendo (PID: $SPRING_PID)"
else
    echo "   ❌ Error iniciando Spring Boot"
    echo "   Primeros 50 líneas de logs:"
    head -50 /tmp/spring.log
    exit 1
fi

echo ""
echo "✨ Servicios iniciados correctamente!"
echo ""
echo "URLs disponibles:"
echo "  - Mock Server:   http://localhost:3001/health"
echo "  - Spring Boot:   http://localhost:8080/product/1/similar"
echo ""
echo "Para detener los servicios, ejecute: killall java python3"
