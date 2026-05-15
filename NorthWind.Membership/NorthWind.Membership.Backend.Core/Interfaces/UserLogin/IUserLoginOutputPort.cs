using Microsoft.AspNetCore.Http;
using NorthWind.Membership.Backend.Core.Dtos;
using NorthWind.Result.Entities;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.Core.Interfaces.UserLogin
{
    internal interface IUserLoginOutputPort
    {
        IResult Result { get; }
        Task Handle(Result<UserDto, IEnumerable<ValidationError>> userLoginResult);
    }
}
