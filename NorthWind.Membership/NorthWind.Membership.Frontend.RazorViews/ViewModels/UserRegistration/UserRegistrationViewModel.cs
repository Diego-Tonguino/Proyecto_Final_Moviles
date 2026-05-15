using NorthWind.Membership.Entities.Dtos.UserRegistration;
using NorthWind.Membership.Frontend.RazorViews.Resources;
using NorthWind.Membership.Frontend.RazorViews.WebApiGateways;
using NorthWind.RazorComponents.Validators;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.ViewModels.UserRegistration
{
    public class UserRegistrationViewModel(
 MembershipGateway gateway,
 IModelValidatorHub<UserRegistrationViewModel> validator)
    {
        public IModelValidatorHub<UserRegistrationViewModel> Validator =>
        validator;
        public ModelValidator<UserRegistrationViewModel>
        ModelValidatorComponentReference
        { get; set; }
        public string InformationMessage { get; private set; }
        #region Datos de entrada
        public string FirstName { get; set; }
        public string LastName { get; set; }
        public string Email { get; set; }
        public string Password { get; set; }
        public string PasswordConfirmation { get; set; }
        #endregion
        public async Task Register()
        {
            try
            {
                InformationMessage = "";
                await gateway.RegisterAsync((UserRegistrationDto)this);
                InformationMessage =
                UserRegistrationMessages.RegisteredUserMessage;
            }
            catch (HttpRequestException ex)
            {
                if (ex.Data.Contains("Errors"))
                {
                    IEnumerable<ValidationError> Errors =
                    ex.Data["Errors"] as IEnumerable<ValidationError>;
                    ModelValidatorComponentReference.AddErrors(Errors);
                }
                else
                {
                    throw;
                }
            }
        }
        public static explicit operator UserRegistrationDto(
            UserRegistrationViewModel model) =>
            new UserRegistrationDto(
                model.Email, model.Password,
                model.FirstName, model.LastName);
    }
}
