using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using NorthWind.Sales.Backend.BusinessObjects.Interfaces.CreateOrder;
using NorthWind.Sales.Entities.Dtos.CreateOrder;
using NorthWind.Sales.Entities.ValueObjects;

namespace NorthWind.Sales.Backend.Controllers.CreateOrder;

// CreateOrderController: Esta clase define un controller minimalista en forma de clase estática.
public static class CreateOrderController
{
    //  --------------------------------------------------------------------------------------
    //  Este método podrá ser consumido por un cliente remoto a través de la API Web.
    //  --------------------------------------------------------------------------------------
    public static WebApplication UseCreateOrderController(this WebApplication app)
    {
        //  MapPost: registra una ruta y asocia la acción CreateOrder.
        app.MapPost(Endpoints.CreateOrder, CreateOrder)
                .RequireAuthorization();

        return app;
    }

    //  --------------------------------------------------------------------------------------
    //  CreateOrder: Maneja la lógica del endpoint HTTP POST y ejecuta el caso de uso asociado.
    //  --------------------------------------------------------------------------------------
    public static async Task<IResult> CreateOrder(
        CreateOrderDto orderDto,
        ICreateOrderInputPort inputPort,
        ICreateOrderOutputPort presenter
    )
    {
        try
        {
            // Execute use case
            await inputPort.Handle(orderDto);

            // Return 201 Created with location
            return Results.Created($"/api/orders/{presenter.OrderId}", presenter.OrderId);
        }
        catch (NorthWind.Exceptions.Entities.Exceptions.ValidationException vex)
        {
            // Return validation errors (e.g. customer has outstanding balance)
            var errors = new Dictionary<string, string[]>();
            if (vex.Errors != null)
            {
                foreach (var e in vex.Errors)
                {
                    if (!errors.TryGetValue(e.PropertyName, out var list))
                    {
                        errors[e.PropertyName] = new[] { e.Message };
                    }
                    else
                    {
                        errors[e.PropertyName] = list.Concat(new[] { e.Message }).ToArray();
                    }
                }
            }
            return Results.ValidationProblem(errors);
        }
        catch (NorthWind.Exceptions.Entities.Exceptions.CustomerNotFoundException cnfex)
        {
            // Cliente fue eliminado entre la validación y el guardado (race condition)
            return Results.BadRequest(new { error = cnfex.Message });
        }
        catch (NorthWind.Exceptions.Entities.Exceptions.ProductNotFoundException pnfex)
        {
            // Producto fue eliminado entre la validación y el guardado (race condition)
            return Results.BadRequest(new { error = pnfex.Message });
        }
        catch (InvalidOperationException ex)
        {
            // Custom business errors (e.g. insufficient stock)
            return Results.BadRequest(new { error = ex.Message });
        }
        catch (Exception ex)
        {
            // Unexpected errors
            return Results.Problem(detail: ex.ToString(), statusCode: 500);
        }
    }
}