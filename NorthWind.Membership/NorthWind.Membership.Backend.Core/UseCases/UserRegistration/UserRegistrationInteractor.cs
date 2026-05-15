using NorthWind.Membership.Backend.Core.Interfaces.Common;
using NorthWind.Membership.Backend.Core.Interfaces.UserRegistration;
using NorthWind.Membership.Entities.Dtos.UserRegistration;
using NorthWind.Result.Entities;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.Core.UseCases.UserRegistration
{
    internal class UserRegistrationInteractor(
        IMembershipService membershipService,
        IUserRegistrationOutputPort presenter,
        IModelValidatorHub<UserRegistrationDto> validationService)
        : IUserRegistrationInputPort
    {
        public async Task Handle(UserRegistrationDto userData)
        {
            Result<IEnumerable<ValidationError>> Result;
            if (!await validationService.Validate(userData))
            {
                Result = new Result<IEnumerable<ValidationError>>(
                validationService.Errors);
            }
            else
            {
                Result = await membershipService.Register(userData);
            }
            await presenter.Handle(Result);
        }
    }
}
