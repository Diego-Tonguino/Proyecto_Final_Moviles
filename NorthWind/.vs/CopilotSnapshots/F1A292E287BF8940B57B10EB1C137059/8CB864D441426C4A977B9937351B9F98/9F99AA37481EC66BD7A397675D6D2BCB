using System.Net;

namespace NorthWind.Sales.Entities.ValueObjects;

//  Esta clase se usa para centralizar y organizar las rutas de acceso (endpoints) de la API
//  de la aplicación la cual maneja URLs o rutas de acceso .
public class Endpoints
{
  //  Una expresión nameof genera el nombre de una variable, un tipo o un miembro como constante de cadena. 
  //  El nameof devuelve el nombre literal del miembro — en este caso, "CreateOrder" — como cadena de texto.
  //  Este código define de forma segura y centralizada un endpoint "/CreateOrder" para que sea utilizado
  //  en el sistema (por ejemplo, en APIs, rutas de servicios, validaciones, etc.), siguiendo buenas
  //  prácticas de mantenibilidad.
  //  ----------------------------------------------------------------------
  //  ¿Por qué usar "nameof" en vez de escribir directamente "/CreateOrder"?
  //  ----------------------------------------------------------------------
  //  - Evita errores de tipeo: Si se cambia el nombre de la constante "CreateOrder", el compilador
  //    actualizará automáticamente el nombre en la nueva constante.
  //  - Mejor mantenimiento: Refactorizar es más seguro, ya que el "nameof" está vinculado al símbolo
  //    en tiempo de compilación.
  //  - Código más robusto.
  //  -----------------------------------------------------------
  //  Resultado final:CreateOrder = "/CreateOrder
  //  Así que si se hace un POST a: /api/orders/CreateOrder
  //  -----------------------------------------------------------
  //  "nameof": Obtener el nombre de variables, propiedades, métodos o tipos como string, de forma segura
  //  en tiempo de compilación.
  public const string CreateOrder = $"/{nameof(CreateOrder)}";
}
