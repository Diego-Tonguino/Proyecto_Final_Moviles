using Microsoft.AspNetCore.Identity;
using NorthWind.Membership.Backend.AspNetIdentity.Entities;
using NorthWind.Membership.Backend.AspNetIdentity.Extensions;
using NorthWind.Membership.Backend.Core.Dtos;
using NorthWind.Membership.Backend.Core.Interfaces.Common;
using NorthWind.Membership.Entities.Dtos.UserRegistration;
using NorthWind.Membership.Entities.UserLogin;
using NorthWind.Result.Entities;
using NorthWind.Validation.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.AspNetIdentity.Services
{
    internal class MembershipService(UserManager<NorthWindUser> manager) :
IMembershipService
    {
        public async Task<Result<IEnumerable<ValidationError>>> Register(
        UserRegistrationDto userData)
        {
            Result<IEnumerable<ValidationError>> Result;
            var User = new NorthWindUser
            {
                UserName = userData.Email,
                Email = userData.Email,
                FirstName = userData.FirstName,
                LastName = userData.LastName
            };
            var CreateResult =
            await manager.CreateAsync(User, userData.Password);
            if (CreateResult.Succeeded)
            {
                // Resultado sin errores
                Result = new Result<IEnumerable<ValidationError>>();
            }
            else
            {
                // Resultado con errores
                Result = new Result<IEnumerable<ValidationError>>(
                CreateResult.Errors.ToValidationErrors());
            }
            return Result;
        }
        public async Task<UserDto> GetUserByCredentials(
            UserCredentialsDto userData)
        {
            UserDto FoundUser = null;
            var User = await manager.FindByNameAsync(userData.Email);
            if (User != null &&
            await manager.CheckPasswordAsync(User, userData.Password))
            {
                FoundUser = new UserDto(User.UserName, User.FirstName,
                User.LastName);
            }
            return FoundUser;
        }

    }
}
