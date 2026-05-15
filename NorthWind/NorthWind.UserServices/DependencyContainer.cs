using Microsoft.Extensions.DependencyInjection;
using NorthWind.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.UserServices
{
    public static class DependencyContainer
    {
        public static IServiceCollection AddUserServices(
        this IServiceCollection services)
        {
            services.AddHttpContextAccessor();
            services.AddSingleton<IUserService, UserService>();
            //services.AddSingleton<IUserService, UserServiceFake>();
            return services;
        }
    }
}
