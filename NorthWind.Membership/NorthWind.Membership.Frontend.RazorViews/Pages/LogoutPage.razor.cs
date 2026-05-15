using Microsoft.AspNetCore.Components;
using NorthWind.Membership.Frontend.RazorViews.AuthenticationStateProviders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.Pages
{
    public partial class LogoutPage
    {
        [Inject]
        JWTAuthenticationStateProvider AuthenticationStateProvider { get; set; }
        [Inject]
        NavigationManager NavigationManager { get; set; }
        async ValueTask CloseSession()
        {
            await AuthenticationStateProvider.LogoutAsync();
            // Navegar a la raíz de la aplicación
            NavigationManager.NavigateTo("/");
        }
    }
}
