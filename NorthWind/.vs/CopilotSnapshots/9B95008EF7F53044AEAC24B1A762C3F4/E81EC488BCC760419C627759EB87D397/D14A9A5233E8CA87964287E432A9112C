using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using System.Linq;
using System.Threading.Tasks;
using NorthWind.Sales.Backend.Repositories.Interfaces;
using NorthWind.Sales.Backend.BusinessObjects.POCOEntities;

namespace NorthWind.Sales.Backend.Controllers.Admin;

public static class OrdersController
{
    public static WebApplication UseOrdersController(this WebApplication app)
    {
        app.MapGet("/api/orders", GetAllOrders);
        // enforce numeric id and improve binding reliability
        app.MapGet("/api/orders/{id:int}", GetOrderById);
        app.MapGet("/api/orders/{id:int}/details", GetOrderDetails);
        return app;
    }

    static async Task<IResult> GetAllOrders(INorthWindSalesQueriesDataContext queries)
    {
        var list = await queries.GetAllOrdersAsync();
        return Results.Ok(list);
    }

    static async Task<IResult> GetOrderById(int id, INorthWindSalesQueriesDataContext queries)
    {
        try
        {
            var order = await queries.GetOrderByIdAsync(id);
            if (order == null) return Results.NotFound();
            return Results.Ok(order);
        }
        catch (Exception ex)
        {
            // return detailed problem in development to help debugging
            return Results.Problem(detail: ex.ToString(), statusCode: 500);
        }
    }

    static async Task<IResult> GetOrderDetails(int id, INorthWindSalesQueriesDataContext queries)
    {
        try
        {
            var order = await queries.GetOrderByIdAsync(id);
            if (order == null) return Results.NotFound();

            // The order already contains order details
            var response = new
            {
                order
            };

            return Results.Ok(response);
        }
        catch (Exception ex)
        {
            return Results.Problem(detail: ex.ToString(), statusCode: 500);
        }
    }
}