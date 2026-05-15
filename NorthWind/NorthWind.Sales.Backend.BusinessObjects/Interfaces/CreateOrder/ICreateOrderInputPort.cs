//using NorthWind.Sales.Entities.Dtos.CreateOrder;

namespace NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;

// ************************************
// * InputPort                        *
// ************************************
// Función: Por medio del "Controller" el "InputPort" recibe los datos necesarios en el "Dto"
//          y los pasa al "Interactor" para que este pueda resolver el caso de uso "Crear orden".
//          Además le comunica al "Interactor" que luego de procesar el caso de uso NO DEBE
//          regresar NADA al "Controller".
public interface ICreateOrderInputPort
{
  // En lugar de "Handle" se puede poner "Execute".
  // Task: Para que lo implemente el método de forma sincrona o asincrona.
  Task Handle(CreateOrderDto orderDto);
}
