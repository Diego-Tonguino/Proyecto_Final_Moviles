using Microsoft.AspNetCore.Components;
using NorthWind.Membership.Frontend.RazorViews.ViewModels.UserRegistration;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.Components
{
    public partial class UserRegistrationComponent
    {
        [Parameter]
        public UserRegistrationViewModel ViewModel { get; set; }
    }
}
