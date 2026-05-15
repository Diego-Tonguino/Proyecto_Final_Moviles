using Microsoft.Extensions.DependencyInjection;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;
using NorthWind.Sales.Backend.Presenters.CreateOrder;

namespace NorthWind.Sales.Backend.Presenters;

public static class DependencyContainer
{
  //  Método de extensión.
  public static IServiceCollection AddPresenters(this IServiceCollection services)
  {
    //  Agregar la interfaz y la respectiva clase que implementa la interfaz.
    services.AddScoped<ICreateOrderOutputPort, CreateOrderPresenter>();
    return services;
  }
}
