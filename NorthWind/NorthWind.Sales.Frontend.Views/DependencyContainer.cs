using Microsoft.Extensions.DependencyInjection;
using NorthWind.Sales.Frontend.Views.ViewModels.CreateOrder;

namespace NorthWind.Sales.Frontend.Views;

public static class DependencyContainer
{
  public static IServiceCollection AddViewsServices(this IServiceCollection services)
  {
    services.AddScoped<CreateOrderViewModel>();
    services.AddModelValidator<CreateOrderViewModel,
        CreateOrderViewModelValidator>();
        
     return services;

    }
}


