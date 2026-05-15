using NorthWind.Membership.Entities.UserLogin;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.Core.Interfaces.UserLogin
{
    internal interface IUserLoginInputPort
    {
        Task Handle(UserCredentialsDto userData);
    }
}
