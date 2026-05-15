using NorthWind.DomainLogs.Entities.Interfaces;
using NorthWind.DomainLogs.Entities.ValueObjects;
using NorthWind.Entities.Guards;
using NorthWind.Entities.Interfaces;
using NorthWind.Events.Entities.Interfaces;
using NorthWind.Sales.Backend.BusinessObjects.Aggregates;
using NorthWind.Sales.Backend.BusinessObjects.Guards;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories;
using NorthWind.Sales.Backend.BusinessObjects.Specifications;
using NorthWind.Sales.Backend.UseCases.Resources;
using NorthWind.Sales.Entities.Dtos.CreateOrder;
using NorthWind.Transactions.Entities.Interfaces;
using NorthWind.Validation.Entities.Interfaces;

namespace NorthWind.Sales.Backend.UseCases.CreateOrder;

// ************************************
// * InputPort                        *
// ************************************
// Función: Por medio del "Controller" el "InputPort" recibe los datos necesarios en el "Dto"
//          y los pasa al "Interactor" para que este pueda resolver el caso de uso "Crear orden".
//          Además le comunica al "Interactor" que luego de procesar el caso de uso NO DEBE
//          regresar NADA al "Controller".
// ************************************
// * OutputPort                       *
// ************************************
// Función: Una vez que el "Interactor" procesa-ejecuta el caso de uso "Crear orden"
//          el "OutputPort" le debe pasar al "Presenter" los datos que este debe
//          transformar-convertir y luego devolver al "Controller" para que algún agente
//          externo los utilice.
//
//
//  Por lo tanto el "Interactor" que "necesita" para realizar su trabajo:
//  1).- Utiliza o necesita de un "InputPort" porque este le pasa los datos (Dto) y debe implentar
//       los métodos de esta "Interface" para que este pueda ejecutar el caso de uso "Crear orden".
//  2).- Utiliza o necesita de un repositorio para realizar la lógica de la persistencia de datos.
//  3).  Utiliza o necesita de un "OutputPort" porque por medio o a través del "OutputPort" debe
//       regresar el resultadoo o datos de salida al "Presenter" una vez ejecutada la lógica del
//       caso de uso "Crear orden".
//
//  RESUMEN: El "Interactor" no sabe que clase lo va a utilizar, su función es:
//           1).- Procesar el caso de uso "Crear orden" con los datos que le pasa el "InputPort".
//           2).- Realizar la persistencia de los datos para lo cual necesita de un "Repository" y.
//           3).- Regresar el resultado del caso de uso al "OutputPort", el cual debe pasar los
//                datos al "Presenter", datos que este debe transformar-convertir y luego devolver
//                al "Controller" para que algún agente externo los utilice.
//
//  NOTA: Objetos necesarios:
//        1).- Un "InputPort" que tiene los datos, además esta interfaz tiene el o los métodos
//             que el "Interactor" debe implementar para procesar el caso de uso "Crear orden"
//             esto se muestra en el método "Handle(CreateOrderDto orderDto)".
//        2).- Un "outputPort" y un "repository" los cuales se los pasa en el "Constructor"
//             mediante la técnica-mecanismo de "Inyección de dependencias a través del
//             constructor", "outputPort, repository".

internal class CreateOrderInteractor(
ICreateOrderOutputPort outputPort,
ICommandsRepository repository,
IModelValidatorHub<CreateOrderDto> modelValidatorHub,
IDomainEventHub<SpecialOrderCreatedEvent> domainEventHub,
IDomainLogger domainLogger,
IDomainTransaction domainTransaction,
IUserService userService) :
ICreateOrderInputPort
{

  //  1).- El "Controller" le pasa los datos al "InputPort", esos "Datos" se pasan en un "Dto"
  //       desde la UI y para recibir los datos utilizamos el método "Handle" y su parámetro.
  public async Task Handle(CreateOrderDto orderDto)
  {
        //  2).- Una vez que se recibe los datos necesarios para realizar el proceso (desde un "Dto" se mapea(transforma) a un objeto
        //       de tipo "OrderAggregate" para construir la orden (maestro-detalle).
        await GuardModel.AgainstNotValid(modelValidatorHub, orderDto);
        GuardUser.AgainstUnauthenticated(userService);
        OrderAggregate Order = OrderAggregate.From(orderDto);
        try
        {
            // Iniciar la transacción
            domainTransaction.BeginTransaction();

            await domainLogger.LogInformation(
                new DomainLog(CreateOrderMessages.StartingPurchaseOrderCreation,
                userService.UserName));
            //  3).- Guardar la orden (agregado).
            await repository.CreateOrder(Order);

    //  4).- Confirmar los cambios en la base de datos y tratar todo como una unidad o
    //       "Transacción", en este caso es un maestro/detalle, usando para esto el patron
    //       "Unit Of Work" es decir como un "Commit".
    await repository.SaveChanges();
            await domainLogger.LogInformation(
            new DomainLog(string.Format(
            CreateOrderMessages.PurchaseOrderCreatedTemplate,
            Order.Id), userService.UserName));

            //  5).- Enviar la respuesta al "OuputPort" que se ha creado la orden para que pase la
            //       respuesta al "Presenter" y este lo formatee y luego lo pueda devolver al "Controller"
            //       para que algún agente externo los utilice (por ejemplo, se puede utilizar en una
            //       página web para mostrar la respuesta al usuario).
            await outputPort.Handle(Order);
        if (new SpecialOrderSpecification().IsSatisfiedBy(Order))
            {
                await domainEventHub.Raise(
                new SpecialOrderCreatedEvent(
                Order.Id, Order.OrderDetails.Count));
            }
            // Aceptar la transacción
            domainTransaction.CommitTransaction();
        }
        catch
        {
            // Cancelar la transacción
            domainTransaction.RollbackTransaction();
            string Information = string.Format(
            CreateOrderMessages.OrderCreationCancelledTemplate,
            Order.Id);
            await domainLogger.LogInformation(
            new DomainLog(Information, userService.UserName));
            throw;
        }
    }
}
