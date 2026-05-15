using NorthWind.Membership.Entities.Dtos.UserRegistration;
using NorthWind.Membership.Entities.Resources;
using NorthWind.Validation.Entities.Abstractions;
using NorthWind.Validation.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Entities.Validators.UserRegistration
{
    internal class UserRegistrationDtoValidator : AbstractModelValidator<UserRegistrationDto>
    {
        public UserRegistrationDtoValidator(
            IValidationService<UserRegistrationDto> validationService) :
            base(validationService)
        {
            AddRuleFor(u => u.FirstName)
            .NotEmpty(UserRegistrationMessages.RequiredFirstNameErrorMessage);
            AddRuleFor(u => u.LastName)
            .NotEmpty(UserRegistrationMessages.RequiredLastNameErrorMessage);
            AddRuleFor(u => u.Email)
            .NotEmpty(UserRegistrationMessages.RequiredEmailErrorMessage)
            .EmailAddress(UserRegistrationMessages.InvalidEmailErrorMessage);
            AddRuleFor(u => u.Password)
            .StopOnFirstFailure()
            .NotEmpty(UserRegistrationMessages.RequiredPasswordErrorMessage)
            .MinimumLength(6,
            UserRegistrationMessages.PasswordTooShortErrorMessage)
            .Must(p => p.Any(c => char.IsLower(c)),
            UserRegistrationMessages.PasswordRequiresLowerErrorMessage)
            .Must(p => p.Any(c => char.IsUpper(c)),
            UserRegistrationMessages.PasswordRequiresUpperErrorMessage)
            .Must(p => p.Any(c => char.IsDigit(c)),
            UserRegistrationMessages.PasswordRequiresDigitErrorMessage)
            .Must(p => p.Any(c => !char.IsLetterOrDigit(c)),
            UserRegistrationMessages
            .PasswordRequiresNonAlphanumericErrorMessage);
        }
    }
}
