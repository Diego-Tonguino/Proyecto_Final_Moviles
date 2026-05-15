using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.Extensions
{
    public static class MembershipPages
    {
        public static Assembly Assembly =>
        typeof(MembershipPages).Assembly;
    }
}
