using NorthWind.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.UserServices
{
    internal class UserServiceFake : IUserService
    {
        public bool IsAuthenticated => true;
        public string UserName => "user@northwind.com";
        public string FullName => "Usuario de prueba";
    }
}
