//  Se importa el DTO CreateOrderDto, que contiene los datos necesarios para crear una orden
//  (cliente, fecha, detalles, etc.).
using NorthWind.Sales.Entities.Dtos.CreateOrder;
using System.Runtime.Intrinsics.X86;
using static System.Runtime.InteropServices.JavaScript.JSType;


namespace NorthWind.Sales.Frontend.BusinessObjects.Interfaces;

//  Este código define una interfaz (ICreateOrderGateway) que sirve como contrato de abstracción
//  para encapsular la lógica de comunicación del cliente frontend con la Web API que crea órdenes. 
//  Esta interfaz está pensada para que podamos ocultar la lógica del consumo HTTP (como HttpClient)
//  en una clase implementadora.
public interface ICreateOrderGateway
{
  //  Método asíncrono que:
  //  - Recibe un DTO con los datos de la orden.
  //  - Devuelve el ID de la orden creada(int), una vez que la Web API haya respondido.
  Task<int> CreateOrderAsync(CreateOrderDto order);
}

//  ¿Para qué sirve en Clean Architecture?
//  Esta interfaz forma parte de un patrón de puerto (Ports) y adaptador (Adapters)
//  (también conocido como arquitectura hexagonal o Clean Architecture), donde:
//  - El frontend no conoce detalles de la Web API.
//  - Cualquier clase que implemente esta interfaz puede encargarse de:
//      - Llamar a un endpoint REST.
//      - Validar datos.
//      - Manejar errores de red.
//  - La ventaja es que se puede cambiar la implementación (por ejemplo, un mock o fake para testing)
//    sin afectar la lógica que la usa.

//  Ventajas:
//  - Desacopla el frontend del consumo directo de la Web API.
//  - Facilita pruebas unitarias (puede simular el gateway).
//  - Centraliza el código de red en un solo lugar.