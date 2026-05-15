using NorthWind.Membership.Frontend.RazorViews.Resources;
using NorthWind.Validation.Entities.Abstractions;
using NorthWind.Validation.Entities.Enums;
using NorthWind.Validation.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.ViewModels.UserRegistration
{
    internal class UserRegistrationViewModelValidator :
 AbstractModelValidator<UserRegistrationViewModel>
    {
        public UserRegistrationViewModelValidator(
        IValidationService<UserRegistrationViewModel> validationService) :
        base(validationService, ValidationConstraint.AlwaysValidate)
        {
            AddRuleFor(c => c.PasswordConfirmation)
            .Equal(c => c.Password,
            UserRegistrationMessages.ConfirmPasswordErrorMessage);
        }
    }
}
