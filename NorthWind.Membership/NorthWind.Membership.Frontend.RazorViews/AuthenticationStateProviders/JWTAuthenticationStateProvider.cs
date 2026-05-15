using Microsoft.IdentityModel.JsonWebTokens;
using NorthWind.Membership.Entities.UserLogin;
using NorthWind.Membership.Frontend.RazorViews.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Components.Authorization;


namespace NorthWind.Membership.Frontend.RazorViews.AuthenticationStateProviders
{
    public class JWTAuthenticationStateProvider(ITokenStorage storage) : 
        AuthenticationStateProvider
    {
        public override async Task<AuthenticationState>
        GetAuthenticationStateAsync()
        {
            // Un ClaimsIdentity vacío indica que no hay un usuario autenticado.
            ClaimsIdentity Identity = new ClaimsIdentity();
            // Recuperar el token.
            var Tokens = await storage.GetTokensAsync();
            if (Tokens != null)
            {
                // Se encontró el Token. Procesarlo.
                var Handler = new JsonWebTokenHandler();
                var Token = Handler.ReadJsonWebToken(Tokens.AccessToken);
                // Obtener la identidad del usuario.
                Identity = new ClaimsIdentity(
                Token.Claims,
                nameof(JWTAuthenticationStateProvider));
            }
            return new AuthenticationState(
            new ClaimsPrincipal(Identity));
        }
        // Almacenar el Token.
        public async ValueTask LoginAsync(TokensDto tokens)
        {
            await storage.StoreTokensAsync(tokens);
            NotifyAuthenticationStateChanged(GetAuthenticationStateAsync());
        }
        // Eliminar el Token.
        public async ValueTask LogoutAsync()
        {
            await storage.RemoveTokensAsync();
            NotifyAuthenticationStateChanged(GetAuthenticationStateAsync());
        }
    }
}
