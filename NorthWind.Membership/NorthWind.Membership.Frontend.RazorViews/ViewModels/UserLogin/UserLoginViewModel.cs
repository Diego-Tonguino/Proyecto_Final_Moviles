using NorthWind.Membership.Entities.UserLogin;
using NorthWind.Membership.Frontend.RazorViews.AuthenticationStateProviders;
using NorthWind.Membership.Frontend.RazorViews.WebApiGateways;
using NorthWind.RazorComponents.Validators;
using NorthWind.Validation.Entities.Interfaces;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.ViewModels.UserLogin
{
    public class UserLoginViewModel(
        MembershipGateway gateway,
        IModelValidatorHub<UserLoginViewModel> validator,
        JWTAuthenticationStateProvider authenticationStateProvider)
    {
        public IModelValidatorHub<UserLoginViewModel> Validator => validator;
        public ModelValidator<UserLoginViewModel> ModelValidatorComponentReference
        { get; set; }
        public event Action OnLogin;
        #region Datos de entrada
        public string Email { get; set; }
        public string Password { get; set; }
        #endregion
        public async Task Login()
        {
            try
            {
                TokensDto Tokens =
                await gateway.LoginAsync((UserCredentialsDto)this);
                //Console.WriteLine(Tokens.AccessToken);
                await authenticationStateProvider.LoginAsync(Tokens);
                OnLogin?.Invoke();
            }
            catch (HttpRequestException ex)
            {
                if (ex.Data.Contains("Errors"))
                {
                    var Errors = ex.Data["Errors"] as
                    IEnumerable<ValidationError>;
                    ModelValidatorComponentReference.AddErrors(Errors);
                }
                else
                {
                    throw;
                }
            }
        }
        public static explicit operator UserCredentialsDto(
        UserLoginViewModel model) =>
        new UserCredentialsDto(model.Email, model.Password);
    }
}
