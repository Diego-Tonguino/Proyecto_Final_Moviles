using NorthWind.Sales.Backend.BusinessObjects.POCOEntities;

namespace NorthWind.Sales.Backend.Repositories.Interfaces;

//  ---------------------------------
//  Función:
//  ---------------------------------
//  Esta interfaz:
//  1) Separa la capa dominio/application de la cada de infraestructura/Interface Adapters. 
//     - El dominio o la capa de aplicación no sabe nada del ORM.
//     - Se puede trabajar con esta interfaz sin importar si para la implementación se usa
//       Entity Framework, Dapper, MongoDB, archivos planos, etc.
//  2) Aplica el principio DIP (Dependency Inversion Principle).
//       Es decir se depende de abstracciones (interfaces) no de implementaciones (clases).
//     - El repositorio (CommandsRepository que lo implementa) depende de esta interfaz, no de
//       un tipo de clase concreta.
//  3) Permite la inversión de control (IoC).
//     - En el arranque de la aplicación (Program.cs o el contenedor de inyección de
//       dependencias IoC container), se decide qué clase concreta implementa esta interfaz.
public interface INorthWindSalesCommandsDataContext
{
  //  Definamos ahora las abstracciones que utilizaremos para comunicarnos con la
  //  herramienta ORM utilizada (en este caso Entity Framework) pero se puede cambiar el ORM
  //  porque se utiliza Interfaces para desacoplar el código.

  Task AddOrderAsync(Order order);// Agregar el encabezado de la orden. 
  Task AddOrderDetailsAsync(IEnumerable<Entities.OrderDetail> orderDetails); // Agregar el detalle. 
  Task SaveChangesAsync(); // Guardar los datos en la BD.
}
