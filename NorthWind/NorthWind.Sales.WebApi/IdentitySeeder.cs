using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.DependencyInjection;
using System.Threading.Tasks;

namespace NorthWind.Sales.WebApi;

internal static class IdentitySeeder
{
    // Seed roles with fixed ids so UsersController can assign by id or name
    public static async Task EnsureRolesAsync(this WebApplication app)
    {
        using var scope = app.Services.CreateScope();
        var roleManager = scope.ServiceProvider.GetService<RoleManager<IdentityRole>>();
        if (roleManager == null) return;

        var roles = new[]
        {
            new { Id = "8EBAB1F7-0E0B-4E0B-B2B2-5C3A2B49CE7B", Name = "Vendedor" },
            new { Id = "B76F89AE-ECA8-4058-84A7-66F47805F027", Name = "Administrador" }
        };

        foreach (var r in roles)
        {
            // if role with this name already exists, skip
            if (await roleManager.RoleExistsAsync(r.Name)) continue;

            var role = new IdentityRole
            {
                Id = r.Id,
                Name = r.Name,
                NormalizedName = r.Name.ToUpper()
            };

            await roleManager.CreateAsync(role);
        }
    }
}
