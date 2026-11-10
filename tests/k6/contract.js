import http from 'k6/http';
import { check } from 'k6';

// Complement the supplied benchmark with assertions; its scripts remain unchanged.
export const options = {
  vus: 1,
  iterations: 1,
  thresholds: {
    checks: ['rate==1'],
    http_req_failed: ['rate==0'],
  },
};

const baseUrl = __ENV.BASE_URL || 'http://host.docker.internal:5000';
const scenarios = [
  ['1', ['2', '3', '4']],
  ['2', ['3', '100', '1000']],
  ['3', ['100', '1000']],
  ['4', ['1', '2']],
  ['5', ['1', '2']],
];

export default function () {
  for (const [source, expectedIds] of scenarios) {
    const response = http.get(`${baseUrl}/product/${source}/similar`);
    let products = null;
    try { products = response.json(); } catch (_) { /* The checks report invalid JSON. */ }
    check(response, {
      [`product ${source}: status 200`]: r => r.status === 200,
      [`product ${source}: ordered IDs`]: () => Array.isArray(products)
        && JSON.stringify(products.map(p => p.id)) === JSON.stringify(expectedIds),
      [`product ${source}: valid details`]: () => Array.isArray(products)
        && products.length === expectedIds.length
        && products.every(p => typeof p.id === 'string' && p.id.length > 0
          && typeof p.name === 'string' && p.name.length > 0
          && typeof p.price === 'number' && typeof p.availability === 'boolean'),
    });
  }
  const missing = http.get(`${baseUrl}/product/unknown/similar`, {
    responseCallback: http.expectedStatuses(404),
  });
  check(missing, { 'unknown product: status 404': r => r.status === 404 });
}
