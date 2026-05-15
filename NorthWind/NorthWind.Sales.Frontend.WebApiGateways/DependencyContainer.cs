using Microsoft.Extensions.DependencyInjection;
using NorthWind.HttpDelegatingHandlers;
using NorthWind.Sales.Frontend.BusinessObjects.Interfaces;

namespace NorthWind.Sales.Frontend.WebApiGateways;

public static class DependencyContainer
{
    public static IServiceCollection AddWebApiGateways(
  this IServiceCollection services,
  Action<HttpClient> configureHttpClient,
  Action<IHttpClientBuilder> configureHttpClientBuilder)
    {
        services.AddExceptionDelegatingHandler();
        var Builder = services
            .AddHttpClient<ICreateOrderGateway,
            CreateOrderGateway>(configureHttpClient)
            .AddHttpMessageHandler<ExceptionDelegatingHandler>();
       
        configureHttpClientBuilder(Builder);
        return services;
    }
}
