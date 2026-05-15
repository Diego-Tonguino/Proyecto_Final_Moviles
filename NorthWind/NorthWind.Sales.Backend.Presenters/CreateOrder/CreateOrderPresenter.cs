using NorthWind.Sales.Backend.BusinessObjects.Aggregates;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;

namespace NorthWind.Sales.Backend.Presenters.CreateOrder;

internal class CreateOrderPresenter : ICreateOrderOutputPort
{
  public int OrderId { get; private set; }

  public Task Handle(OrderAggregate addedOrder)
  {
    OrderId = addedOrder.Id; // Retornar el "Id" en la propiedad publica "OrderId". 

    //  En C#, cuando un método devuelve Task (o Task<T>), se espera que sea asíncrono.
    //  Pero a veces no se necesita hacer nada que sea realmente asíncrono.
    //  En esos casos, se puede retornar Task.CompletedTask para indicar que la tarea
    //  se ha completado.
    return Task.CompletedTask;
  }
}
