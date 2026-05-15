using Microsoft.AspNetCore.Http;
using NorthWind.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.UserServices
{
    internal class UserService(IHttpContextAccessor contextAccesor) : IUserService
    {
        public bool IsAuthenticated =>
        contextAccesor.HttpContext.User.Identity.IsAuthenticated;
        public string UserName =>
        contextAccesor.HttpContext.User.Identity.Name;
        public string FullName =>
        contextAccesor.HttpContext.User.Claims
        .Where(c => c.Type == "FullName")
        .Select(c => c.Value).FirstOrDefault();
    }
}
