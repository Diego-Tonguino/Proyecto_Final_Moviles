using Microsoft.Extensions.DependencyInjection;
using NorthWind.Membership.Backend.AspNetIdentity.Options;
using NorthWind.Membership.Backend.Core.Options;
using NorthWind.Sales.Backend.DataContexts.EFCore; //AddDataContexts
using NorthWind.Sales.Backend.DataContexts.EFCore.Options; //configureDBOptions
using NorthWind.Sales.Backend.Presenters; //AddPresenters
using NorthWind.Sales.Backend.Repositories; //AddRepositories
using NorthWind.Sales.Backend.UseCases; //AddUseCasesServices
using NorthWind.UserServices;
namespace NorthWind.Sales.Backend.IoC;

//  Este clase es el punto central de la inyección de dependencias para el backend
//  NorthWind.Sales, diseñado bajo el enfoque de Clean Architecture en C#.
//  Es una clase estática dentro de la carpeta/capa de Inversión de Control (IoC).
//  Su objetivo es:
//                  Registrar todas las dependencias necesarias para ejecutar
//                  el caso de uso CreateOrder, siguiendo Clean Architecture.
public static class DependencyContainer
{
    //  Este método de extensión sobre IServiceCollection (el contenedor de DI de ASP.NET Core):
    //  - Recibe la instancia de services.
    //  - Recibe una acción de configuración (configureDBOptions) para proporcionar la cadena de conexión.
    public static IServiceCollection AddNorthWindSalesServices(
 this IServiceCollection services,
 Action<DBOptions> configureDBOptions,
 Action<SmtpOptions> configureSmtpOptions,
 Action<MembershipDBOptions> configureMembershipDBOptions,
 Action<JwtOptions> configureJwtOptions)
    {
        //  -------------------------------------------------------
        //  ¿Qué se registra el contenedor?
        //  -------------------------------------------------------
        //  Método: AddUseCasesServices(), capa: Application.
        //  Que registra: Los casos de uso (como CreateOrderUseCase) y sus interfaces (IInputPort).

        //  Método: AddRepositories(), capa: Interface Adapters (Infrastructure).
        //  Que registra: Implementaciones concretas que acceden a la BD (repositorios EF Core).

        //  Método: AddDataContexts(...), capa: Frameworks & Drivers (Presentation).
        //  Que registra: DbContext y configuración de conexión con SQL Server.

        //  Método: AddPresenters(), capa: Interface Adapters (Infrastructure).
        //  Que registra: Presentadores que devuelven respuestas (implementan IOutputPort).

        services
            .AddUseCasesServices()
            .AddRepositories()
            .AddDataContexts(configureDBOptions)
            .AddPresenters()
            .AddValidationService()
            .AddValidators()
            .AddValidationExceptionHandler()
            .AddUpdateExceptionHandler()
            .AddUnauthorizedAccessExceptionHandler()
            .AddUnhandledExceptionHandler()
            .AddEventServices()
            .AddMailServices(configureSmtpOptions)
            .AddDomainLogsServices()
            .AddTransactionServices()
            .AddUserServices()
            .AddMembershipCoreServices(configureJwtOptions)
            .AddMembershipIdentityServices(configureMembershipDBOptions);


        return services;
  }
}

//  ------------------------------------------------------------------------
//   ¿Por qué es importante el contenedor IoC en Clean Architecture?
//  ------------------------------------------------------------------------
//  - Es una pieza clave de la Clean Architecture aplicada correctamente en .NET.
//  - Aplica el principio de inversión de dependencias (DIP).
//    Desacopla las dependencias.
//  - Hace que todas las capas se comuniquen solo a través de interfaces.
//    Orquesta toda la aplicación.
//  - Separa la infraestructura (EF Core) de la lógica de negocio (casos de uso).
//  - Facilita el testing, mantenimiento y la extensibilidad.

// Agregar el using necesario si existe el método de extensión en otro namespace

// Si el método de extensión AddUserServices no existe, debe implementarse.
// Ejemplo de implementación mínima para evitar el error CS1061:
