/**
 * MockServer -> Remplazo de simulado
 * Ver mocks.json
 */

const express = require('express');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3001;

// Cargar mocks desde el archivo JSON
// Funciona tanto en Docker como en ejecución local
const mocksPath =
	process.env.MOCKS_PATH || path.join(__dirname, '..', 'shared', 'simulado', 'mocks.json');
// Si estamos en Docker, usar la ruta montada
const dockerMocksPath = '/shared/simulado/mocks.json';
const finalMocksPath = require('fs').existsSync(dockerMocksPath) ? dockerMocksPath : mocksPath;

let mocks = [];

try {
	const mocksData = fs.readFileSync(finalMocksPath, 'utf8');
	mocks = JSON.parse(mocksData);
	console.log(`✓ Cargados ${mocks.length} mocks desde ${finalMocksPath}`);
} catch (error) {
	console.error('✗ Error cargando mocks:', error.message);
	console.error(`  Intentó cargar desde: ${finalMocksPath}`);
	process.exit(1);
}

// Middleware para parsear JSON
app.use(express.json());

// Función para encontrar un mock por path
function findMock(path) {
	return mocks.find((mock) => mock.path === path);
}

// Endpoint genérico que maneja todos los paths
app.get('*', (req, res) => {
	const mock = findMock(req.path);

	if (!mock) {
		console.log(`⚠️  Mock no encontrado para: ${req.path}`);
		return res.status(404).json({ error: 'Not found' });
	}

	// Aplicar delay si existe
	if (mock.delay) {
		setTimeout(() => {
			sendResponse(mock, res);
		}, mock.delay);
	} else {
		sendResponse(mock, res);
	}
});

function sendResponse(mock, res) {
	// Establecer headers
	if (mock.headers) {
		Object.keys(mock.headers).forEach((key) => {
			res.setHeader(key, mock.headers[key]);
		});
	}

	// Establecer status code (default 200)
	const status = mock.status || 200;

	// Enviar respuesta
	if (mock.body) {
		// Si el body es un string JSON, parsearlo primero
		try {
			const body = typeof mock.body === 'string' ? JSON.parse(mock.body) : mock.body;
			res.status(status).json(body);
		} catch (e) {
			// Si no es JSON válido, enviar como string
			res.status(status).send(mock.body);
		}
	} else {
		res.status(status).end();
	}
}

// Iniciar servidor
app.listen(PORT, () => {
	console.log(`🚀 Mock Server seguro corriendo en http://localhost:${PORT}`);
	console.log(`📋 Endpoints disponibles:`);
	mocks.forEach((mock) => {
		console.log(`   ${mock.path}${mock.delay ? ` (delay: ${mock.delay}ms)` : ''}`);
	});
});
