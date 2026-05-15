//  Microsoft.Extensions.DependencyInjection es un espacio de nombres proporcionado por .NET
//  para trabajar con la inyección de dependencias (DI).
using Microsoft.Extensions.DependencyInjection;
using NorthWind.Events.Entities.Interfaces;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;
using NorthWind.Sales.Backend.UseCases.CreateOrder;
using NorthWind.Sales.Entities.Dtos.CreateOrder;

namespace NorthWind.Sales.Backend.UseCases;

//  Este es un contenedor de dependencias. Aquí se realiza la "Inversión de dependencias".

//  Este código define una clase estática. Contiene un método de extensión (AddUseCasesServices)
//  La clase DependencyContainer es estática porque contiene métodos de extensión.
//  Los métodos de extensión deben estar en clases estáticas para poder ser utilizados sin
//  instanciar la clase.
public static class DependencyContainer
{

  //  Este código es un método de extensión que registra un caso de uso (CreateOrderInteractor)
  //  en el contenedor DI.
  //  --------------------------
  //  Parámetros:
  //  --------------------------
  //  "IServiceCollection": Este es un tipo proporcionado por el framework de inyección
  //          de dependencias de .NET. Representa la colección de servicios que se van
  //          a registrar en el contenedor de dependencias IoC.  
  //  "this IServiceCollection services": Esta es la sintaxis para definir un método de extensión
  //          en C#. Permite "añadir" un método a la interfaz "IServiceCollection" como si fuera
  //          un método de instancia propio. En la práctica, cuando se llame  a este método, se lo
  //          deberá hacer con una instancia de "IServiceCollection" (generalmente en el archivo
  //          Startup.cs o Program.cs de la aplicación).
  //  "services": Parámetro que representa la colección de servicios (uses cases) a la que vamos a
  //          añadir nuevas dependencias.

  //  Función: Este método permite agregar/registrar los servicios de los casos de uso en el Framework.
  //           Es un método de extensión de: "IServiceCollection"
  //           "IServiceCollection": Es donde se registra los servicios en el Framework, para cuando
  //           se lo utilice en la aplicación el Framework sepa que devolver.
  //
  //  --------------------------------------------------------------------------------------
  //  Un método de extensión en C# sirve para agregar funcionalidad a tipos existentes
  //  (como clases, interfaces o structs) sin modificarlos directamente ni usar herencia.
  //  --------------------------------------------------------------------------------------
  public static IServiceCollection AddUseCasesServices(
  this IServiceCollection services)
  {

    //  Explicación: "AddScoped" permite registrar el servicio en el contenedor de inyección de
    //               dependencias (IoC) DI del framework, para que cuando en el código se pida
    //               un "ICreateOrderInputPort" el framework regrese una instancia de la clase
    //               "CreateOrderInteractor" ya que es esta clase la que contiene la implementación
    //               del use case "crear orden".
    //               La dependencia/servicio se registra como "Scoped", lo que significa que se crea
    //               una instancia por cada una de las solicitudes HTTP que se haga.
    services.AddScoped<ICreateOrderInputPort, 
        CreateOrderInteractor>();
    services.AddModelValidator<CreateOrderDto,
        CreateOrderCustomerValidator>();
    services.AddModelValidator<CreateOrderDto,
        CreateOrderProductValidator>();
    services.AddScoped<IDomainEventHandler<SpecialOrderCreatedEvent>,
        SendEMailWhenSpecialOrderCreatedEventHandler>();
        //  El método devuelve la misma instancia modificada de "IServiceCollection" que recibió como
        //  parámetro.
        //  Esto permite encadenar múltiples llamadas de configuración de servicios en una sola
        //  línea, lo cual es una práctica común en ASP.NET Core.
        return services;
  }
}
