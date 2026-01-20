#!/usr/bin/env python3
"""
Simula las dos APIs externas necesarias para la aplicación
Usa http.server (no requiere dependencias externas)
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
from urllib.parse import urlparse
import json
import time

PRODUCTS = {
    "1": {"id": "1", "name": "T-Shirt", "price": 9.99, "availability": True},
    "2": {"id": "2", "name": "Dress", "price": 19.99, "availability": True},
    "3": {"id": "3", "name": "Blazer", "price": 29.99, "availability": False},
    "4": {"id": "4", "name": "Jeans", "price": 39.99, "availability": True},
    "5": {"id": "5", "name": "Shirt", "price": 14.99, "availability": True},
    "6": {"id": "6", "name": "Sweater", "price": 24.99, "availability": True},
}

SIMILAR_IDS = {
    "1": ["2", "3", "4"],
    "2": ["1", "3", "5"],
    "3": ["2", "4", "6"],
    "4": ["1", "2", "5"],
    "5": ["1", "2", "3"],
}

DELAYS = {
    "2": 0.1,
    "3": 5.0,
}

ERRORS = {
    "5": 500,
}


class MockServerHandler(BaseHTTPRequestHandler):
    
    def do_GET(self):
        parsed_path = urlparse(self.path)
        path = parsed_path.path
        
        if path == '/health':
            self.send_json({"status": "ok"}, 200)
        elif '/similarids' in path:
            self.handle_similar_ids(path)
        elif path.startswith('/product/'):
            self.handle_product_detail(path)
        else:
            self.send_json({"error": "Not found"}, 404)
    
    def handle_similar_ids(self, path):
        try:
            parts = path.split('/')
            product_id = parts[2]
            
            if product_id in DELAYS:
                delay = DELAYS[product_id]
                print(f"  [Mock] Simulando delay de {delay}s para producto {product_id}")
                time.sleep(delay)
            
            if product_id in ERRORS:
                error_code = ERRORS[product_id]
                print(f"  [Mock] Retornando error {error_code} para {product_id}/similarids")
                self.send_json({"error": f"Error {error_code}"}, error_code)
                return
            
            if product_id in SIMILAR_IDS:
                similar_ids = SIMILAR_IDS[product_id]
                print(f"📍 GET /product/{product_id}/similarids → [{', '.join(similar_ids)}]")
                self.send_json(similar_ids, 200)
            else:
                print(f"📍 GET /product/{product_id}/similarids → 404 NOT FOUND")
                self.send_json({"error": "Product not found"}, 404)
        except:
            self.send_json({"error": "Error"}, 500)
    
    def handle_product_detail(self, path):
        try:
            parts = path.split('/')
            product_id = parts[2]
            
            if product_id in DELAYS:
                delay = DELAYS[product_id]
                print(f"  [Mock] Simulando delay de {delay}s para detalle de {product_id}")
                time.sleep(delay)
            
            if product_id in ERRORS:
                error_code = ERRORS[product_id]
                print(f"  [Mock] Retornando error {error_code} para detalle de {product_id}")
                self.send_json({"error": f"Error {error_code}"}, error_code)
                return
            
            if product_id in PRODUCTS:
                product = PRODUCTS[product_id]
                print(f"  → {product['name']} (${product['price']})")
                self.send_json(product, 200)
            else:
                print(f"  → 404 - Producto {product_id} no encontrado")
                self.send_json({"error": "Product not found"}, 404)
        except:
            self.send_json({"error": "Error"}, 500)
    
    def send_json(self, data, status_code):
        self.send_response(status_code)
        self.send_header('Content-Type', 'application/json')
        self.send_header('Access-Control-Allow-Origin', '*')
        self.end_headers()
        self.wfile.write(json.dumps(data).encode())
    
    def log_message(self, format, *args):
        pass


def run_server():
    server_address = ('localhost', 3001)
    httpd = HTTPServer(server_address, MockServerHandler)
    print("🚀 MOCK SERVER - Similar Products API")
    print("=" * 60)
    print("Escuchando en http://localhost:3001\n")
    print("Endpoints disponibles:")
    print("  - GET /product/{id}/similarids")
    print("  - GET /product/{id}")
    print("  - GET /health\n")
    print("Casos de prueba:")
    print("  - Producto 1: normal")
    print("  - Producto 2: delay 100ms")
    print("  - Producto 3: delay 5000ms")
    print("  - Producto 4: similar no encontrado")
    print("  - Producto 5: error 500")
    print("=" * 60)
    print()
    
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print("\nServidor detenido")


if __name__ == '__main__':
    run_server()
