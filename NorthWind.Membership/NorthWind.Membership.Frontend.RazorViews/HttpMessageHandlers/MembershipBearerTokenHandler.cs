using NorthWind.Membership.Frontend.RazorViews.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.HttpMessageHandlers
{
    internal class MembershipBearerTokenHandler(ITokenStorage storage) :
  DelegatingHandler
    {
        protected async override Task<HttpResponseMessage> SendAsync(
        HttpRequestMessage request, CancellationToken cancellationToken)
        {
            // Intentamos obtener el token de acceso
            var Tokens = await storage.GetTokensAsync();
            // Si el token es encontrado, lo agregamos en el encabezado
            // de autorización.
            if (Tokens != null)
            {
                request.Headers.Authorization =
                new AuthenticationHeaderValue("Bearer", Tokens.AccessToken);
            }
            return await base.SendAsync(request, cancellationToken);
        }
    }
}
