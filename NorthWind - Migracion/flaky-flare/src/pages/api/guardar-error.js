export const prerender = false;
import fs from 'fs';
import path from 'path';

export async function ALL({ request }) {
  try {
    const url = new URL(request.url);
    
    let errorMsg = url.searchParams.get('error');
    let pantalla = url.searchParams.get('pantalla');
    let evento = url.searchParams.get('evento');
    let linea = url.searchParams.get('linea');

    if (request.method === 'POST') {
      try {
        const text = await request.text();
        if (text) {
          const data = JSON.parse(text);
          errorMsg = errorMsg || data.error;
          pantalla = pantalla || data.pantalla;
          evento = evento || data.evento;
          linea = linea || data.linea;
        }
      } catch (e) {}
    }
    
    // Ruta al archivo errores.json en public
    const filePath = path.join(process.cwd(), 'public', 'errores.json');
    
    let errores = [];
    if (fs.existsSync(filePath)) {
      try {
        const fileData = fs.readFileSync(filePath, 'utf-8');
        if (fileData) {
          errores = JSON.parse(fileData);
        }
      } catch(e) {
        errores = [];
      }
    }
    
    const newId = errores.length > 0 ? Math.max(...errores.map(e => e.id)) + 1 : 1;
    const nuevoError = {
      id: newId,
      error: errorMsg || 'Error desconocido',
      linea: linea || 'N/A',
      fecha: new Date().toISOString(),
      pantalla: pantalla || 'Desconocida',
      evento: evento || 'General'
    };
    
    errores.push(nuevoError);
    fs.writeFileSync(filePath, JSON.stringify(errores, null, 2));
    
    return new Response(JSON.stringify({ success: true, error: nuevoError }), {
      status: 200,
      headers: { 'Content-Type': 'application/json' }
    });
  } catch (error) {
    return new Response(JSON.stringify({ error: error.message }), { status: 500 });
  }
}
