using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using NorthWind.Exceptions.Entities.Exceptions;
using NorthWind.Exceptions.Entities.Extensions;
using NorthWind.Exceptions.Entities.Resources;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Exceptions.Entities.ExceptionHandlers
{
    internal class UpdateExceptionHandler(ILogger<UpdateExceptionHandler> logger) :
IExceptionHandler
    {
        public async ValueTask<bool> TryHandleAsync(HttpContext httpContext,
        Exception exception, CancellationToken cancellationToken)
        {
            bool Handled = false;
            if (exception is UpdateException Ex)
            {
                ProblemDetails Details = new ProblemDetails();
                Details.Status = StatusCodes.Status500InternalServerError;
                Details.Type =
                "https://datatracker.ietf.org/doc/html/rfc7231#section-6.6.1";
                Details.Title = ExceptionMessages.UpdateExceptionTitle;
                Details.Detail = ExceptionMessages.UpdateExceptionDetail;
                Details.Instance =
                $"{nameof(ProblemDetails)}/{nameof(UpdateException)}";
                logger.LogError(exception,
                    ExceptionMessages.UpdateExceptionTitle +
": " + string.Join(" ", Ex.Entities));
                await httpContext.WriteProblemDetailsAsync(Details);
                Handled = true;
            }
            return Handled;
        }
    }
}
