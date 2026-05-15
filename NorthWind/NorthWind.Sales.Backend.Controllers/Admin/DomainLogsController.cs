using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using NorthWind.Sales.Backend.Repositories.Interfaces;
using System.Threading.Tasks;
using System.Linq;
using System.Collections.Generic;
using NorthWind.Sales.Backend.Repositories.Entities;

namespace NorthWind.Sales.Backend.Controllers.Admin;

public static class DomainLogsController
{
    public record DomainLogDto(int Id, DateTime CreatedDate, string? Information, string? UserName);

    public static WebApplication UseDomainLogsController(this WebApplication app)
    {
        // Exponer endpoint GET para listar los últimos 1000 logs
        app.MapGet("/api/logs", GetLatestLogs)
            .WithName("GetLatestLogs");

        // También mapear la ruta /api/domainlogs por compatibilidad
        app.MapGet("/api/domainlogs", GetLatestLogs)
            .WithName("GetDomainLogs");

        return app;
    }

    private static async Task<IResult> GetLatestLogs(INorthWindDomainLogsQueriesDataContext queries)
    {
        try
        {
            var logs = await queries.GetLatestLogsAsync(1000);
            var dtos = logs
                .Select(l => new DomainLogDto(l.Id, l.CreatedDate, l.Information, l.UserName))
                .ToList();
            return Results.Ok(dtos);
        }
        catch (System.Exception ex)
        {
            return Results.Problem(detail: ex.Message, statusCode: 500);
        }
    }
}
