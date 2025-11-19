# IMPORTANTE LEER:

Por motivos de seguridad ya que desconozco que contiene simulado, he remplzado totalmente el contenedor simulado por este nuevo contenedor que es un express

Se inicia en: `http://localhost:3001`
Simula las APIs según `shared/simulado/mocks.json`.

Verificación

```bash
curl http://localhost:3001/product/1/similarids
#[2,3,4]

curl http://localhost:3001/product/1
#{"id":"1","name":"Shirt","price":9.99,"availability":true}
```
