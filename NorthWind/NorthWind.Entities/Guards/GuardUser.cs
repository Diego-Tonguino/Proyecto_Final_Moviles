using NorthWind.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Entities.Guards
{
    public static class GuardUser
    {
        public static void AgainstUnauthenticated(IUserService userService)
        {
            if (!userService.IsAuthenticated)
                throw new UnauthorizedAccessException();
        }
    }
}
