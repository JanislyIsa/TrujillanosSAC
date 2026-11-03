import { cp, mkdir, readdir, unlink } from 'node:fs/promises';
import { resolve, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';
const frontend = resolve(dirname(fileURLToPath(import.meta.url)), '..');
const destino = resolve(frontend, '../src/main/resources/static/inventario-app');
await mkdir(destino, { recursive: true });
// Solo se retiran bundles propios de la compilación anterior.
for (const nombre of await readdir(destino)) {
  if (/^(main|chunk|polyfills|styles)-[A-Z0-9_-]+\.(js|css)(\.map)?$/i.test(nombre)) await unlink(resolve(destino,nombre));
}
await cp(resolve(frontend,'dist/inventario/browser'),destino,{recursive:true});
console.log('Frontend integrado en '+destino);
