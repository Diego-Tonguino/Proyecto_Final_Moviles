using Microsoft.Extensions.DependencyInjection;
using NorthWind.DomainLogs.Entities.Interfaces;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories;
using NorthWind.Sales.Backend.Repositories.Repositories;

namespace NorthWind.Sales.Backend.Repositories;

//  Este es un contenedor de dependencias "IoC". Aquí se realiza la "Inversión de dependencias".
//  Este código debe difinr una clase estática.
//  Contiene un método de extensión (AddRepositories)
//  La clase DependencyContainer (IoC) debe ser estática porque contiene métodos de extensión.
//  Los métodos de extensión deben estar en clases estáticas para poder ser utilizados sin
//  instanciar la clase.

public static class DependencyContainer
{
  public static IServiceCollection AddRepositories(this IServiceCollection services)
  {
    //  Cuando en el código se necesite una interface "ICommandsRepository" el framework
    //  regresa una instancia de la clase "CommandsRepository" ya que es esta clase la que
    //  contiene la implementación del "repository-CommandsRepository".
    //  NOTA: 
    //      Los métodos de la interface "ICommandsRepository" lo implementa la clase
    //      "CommandsRepository". 
    services.AddScoped<ICommandsRepository, CommandsRepository>();
    services.AddScoped<IQueriesRepository, QueriesRepository>();
    services.AddScoped<IDomainLogsRepository, DomainLogsRepository>();

        return services;
  }
}
