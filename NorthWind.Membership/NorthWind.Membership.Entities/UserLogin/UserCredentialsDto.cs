using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Entities.UserLogin
{
    public class UserCredentialsDto(string email, string password)
    {
        public string Email => email;
        public string Password => password;
    }
}
