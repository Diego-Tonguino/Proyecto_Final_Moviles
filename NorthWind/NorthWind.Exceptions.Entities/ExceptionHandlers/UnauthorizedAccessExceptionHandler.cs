using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using NorthWind.Exceptions.Entities.Extensions;
using NorthWind.Exceptions.Entities.Resources;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Exceptions.Entities.ExceptionHandlers
{
    internal class UnauthorizedAccessExceptionHandler : IExceptionHandler
    {
        public async ValueTask<bool> TryHandleAsync(HttpContext httpContext,
        Exception exception, CancellationToken cancellationToken)
        {
            bool Handled = false;
            if (exception is UnauthorizedAccessException Ex)
            {
                ProblemDetails Details = new ProblemDetails();
                Details.Status = StatusCodes.Status401Unauthorized;
                Details.Type =
                "https://datatracker.ietf.org/doc/html/rfc7235#section-3.1";
                Details.Title = ExceptionMessages.UnauthorizedAccessExceptionTitle;
                Details.Detail = ExceptionMessages.UnauthorizedAccessExceptionDetail;
                Details.Instance =
                $"{nameof(ProblemDetails)}/{nameof(UnauthorizedAccessException)}";
                await httpContext.WriteProblemDetailsAsync(Details);
                Handled = true;
            }
            return Handled;
        }
    }
}
