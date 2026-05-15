using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Web;
using NorthWind.Membership.Frontend.RazorViews.ViewModels.UserRegistration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.Pages
{
    public partial class UserRegistrationPage
    {
        [Inject]
        UserRegistrationViewModel ViewModel { get; set; }
        ErrorBoundary ErrorBoundaryRef;
        void Recover()
        {
            ErrorBoundaryRef?.Recover();
        }
    }
}
