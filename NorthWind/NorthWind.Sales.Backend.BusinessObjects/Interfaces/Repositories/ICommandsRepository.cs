//using NorthWind.Sales.Backend.BusinessObjects.Aggregates;
//using NorthWind.Sales.Backend.BusinessObjects.Interfaces.Common;


namespace NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories;

// ************************************
// * Repository                       *
// ************************************
//  Función: El "Iteractor" implementa el caso de uso para "crear la orden" con la lógica
//           necesaria. Pero el "Iteractor" no debe almacenar los datos de la "Orden" en la
//           base datos, y tampoco sabe en que medio se va a almacenar, es otro objeto el que
//           debe realizar esta tarea (el patrón repository).
//
//           El patrón de diseño "Repository" permite separar la "lógica de negocio" de la
//           "lógica que recuperar los datos", y luego de recuperar los datos los asigna (pasa)
//           a un modelo basado en "entidades" y los mantiene temporalmente en la memoria de la PC.
//           Un objeto "Repository" es un "intermediario" entre la capa de "dominio" y las capas
//           que "mapean los datos", manteniendo una colección de los objetos de "dominio"
//           en memoria.
//           Por lo tanto un objeto "Repository" recibe los datos y los almacena en la memoria
//           temporalmente, y cuando le damos la orden este los guarda en la fuente de datos que
//           se indique.
//           Un objeto "Repository" oculta la lógica necesaria para recuperar o almacenar los datos.
//           Por lo tanto a una aplicación no le importa si para recuperar o almacenar los datos
//           se utiliza un ORM (Entity Framework, Dapper, etc.) o SQL directo puesto que toda
//           esta lógica se implementará en el "Repositorio".
//           Se utiliza frecuentemente en conjunto con el patrón "Unit Of Work".
//
//  NOTA: Es recomendable separar las responsabilidades para recuperar y almacenar los datos
//        en 2 repositorios: es decir un repositorio para recuperar (SELECTs) los datos y otro
//        repositorio para almacenar los datos (INSERT, DELETE Y UPDATE). Para esto uno de los
//        patrones recomendados a utilizar es "CQRS" Command Query Responsibility Segregation).
//        En español, Segregación (Separación) de Responsabilidades de Consultas y Comandos, es
//        un patrón de arquitectura de software que separa las operaciones de lectura y escritura
//        de datos.
//        Esto permite optimizar cada grupo (lectura y escritura) de forma independiente. 

//  NOTA: Para implementar el caso de uso "Crear orden" se necesita un repositorio de "Comandos".
//  Por lo tanto se crea un "Repositorio" para los Comandos: INSERT, DELETE, UPDATE. 
public interface ICommandsRepository : IUnitOfWork
{
  //  El método "CreateOrder" no regresa ni un "bool" o un "int", no regresa nada porque es la
  //  clase que implemente la interfaz "IUnitOfWork" la que debe realizar este proceso.  
  //  Además se regresa un "Task" para que se implente de forma síncrona o asíncrona.
  //  OrderAggregate: Contiene solo los datos que se va a utilizar para la persistencia de datos.
  Task CreateOrder(OrderAggregate order);
}
