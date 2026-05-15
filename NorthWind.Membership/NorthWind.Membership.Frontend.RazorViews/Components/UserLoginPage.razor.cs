using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using NorthWind.Membership.Frontend.RazorViews.ViewModels.UserLogin;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.Components
{
    public partial class UserLoginPage
    {
        // Plantilla de ruta de la página
        const string RouteTemplate = "/user/login";
        [Inject]
        UserLoginViewModel ViewModel { get; set; }
        [Inject]
        NavigationManager NavigationManager { get; set; }
        ErrorBoundary ErrorBoundaryRef;
        void Recover()
        {
            ErrorBoundaryRef?.Recover();
        }
        protected override void OnInitialized()
        {
            ViewModel.OnLogin += OnLogin;
        }
        void OnLogin()
        {
            if (NavigationManager.Uri.EndsWith(RouteTemplate))
            {
                // Si se navegó explícitamente a la página 'user/login',
                // después de iniciar sesión navegar a la raíz.
                NavigationManager.NavigateTo("");
            }
            else
            {
                // El usuario fue dirigido a la página '/user/login',
                // navegar a la página original
                NavigationManager.NavigateTo(NavigationManager.Uri);
            }
        }
    }

}
