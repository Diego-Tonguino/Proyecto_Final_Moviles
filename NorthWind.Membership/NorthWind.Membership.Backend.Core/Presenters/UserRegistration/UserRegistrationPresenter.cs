using Microsoft.AspNetCore.Http;
using NorthWind.Membership.Backend.Core.Extensions;
using NorthWind.Membership.Backend.Core.Interfaces.UserRegistration;
using NorthWind.Membership.Backend.Core.Resources;
using NorthWind.Result.Entities;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.Core.Presenters.UserRegistration
{
    internal class UserRegistrationPresenter :
        IUserRegistrationOutputPort
    {
        public IResult Result { get; private set; }
        public Task Handle(
        Result<IEnumerable<ValidationError>> userRegistrationResult)
        {
            userRegistrationResult.HandleResult(
            errors =>
            Result = Results.Problem(
            errors.ToProblemDetails(
            UserRegistrationMessages.UserRegistrationErrorTitle,
            UserRegistrationMessages.UserRegistrationErrorDetail,
            nameof(UserRegistrationPresenter))),
            () =>
            Result = Results.Ok()
            );
            return Task.CompletedTask;
        }
    }
}
