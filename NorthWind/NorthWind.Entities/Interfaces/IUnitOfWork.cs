namespace NorthWind.Entities.Interfaces;
// ************************************
//  * Unit Of Work                    *
// ************************************
//  Función: Es un patron de diseño que permite mantener una lista de objetos
//  que forman parte de una transacción de negocios, y coordinar el guardado
//  de los cambios, la persistencia, asi como la resolución de conflictos que puden darse por
//  la concurrencia.
//  Unit Of Work es el encargado de confirmar los cambios en la fuente de datos, con esto
//  se garantiza una transacción completa sin pérdida de datos de forma atómica. Por ejemplo
//  en una transacción maestro/detalle.

//  Para el ejemplo:
//  La "interfaz Unit Of Work" Esta en la carpeta "Common" porque se va a utilizar en todos
//  los casos de uso que se pueda tener en aplicación.
//  La "interfaz Unit Of Work" también se puede poner en la capa (ENTERPRISE BUSINESS RULES)
//  pero como este patron se lo va a utilizar solo en la aplicación backend (Web API) y no se lo
//  va a utilizar en la segunda aplicación frontend que utiliza Blazor, entonces se ha decido
//  poner en esta capa (APPLICATION BUSINESS RULES).
public interface IUnitOfWork
{
  //  Task: El método regresa un "Task" para que se pueda implementar de forma síncrona
  //  o de forma asíncrona.
  //  Y podría regresar un código de error luego de ejecutado el proceso.
  Task SaveChanges();
}
