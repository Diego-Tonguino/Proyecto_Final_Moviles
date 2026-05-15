using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Identity;
using NorthWind.Sales.Backend.Controllers;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;
using System.Linq;
using System.Collections.Generic;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;

namespace NorthWind.Sales.Backend.Controllers.Auth;

public static class AuthController
{
    public record LoginDto(string UserName, string Password);

    public static WebApplication UseAuthController(this WebApplication app)
    {
        app.MapPost("/api/auth/login", Login);
        return app;
    }
    private static async Task<IResult> Login(LoginDto dto, HttpContext httpContext, IConfiguration configuration)
    {
        try
        {
            var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();
            var signInManager = httpContext.RequestServices.GetRequiredService<SignInManager<ApplicationUser>>();

            if (userManager == null || signInManager == null) return Results.StatusCode(500);

            var user = await userManager.FindByNameAsync(dto.UserName);
            if (user == null) return Results.Unauthorized();

            // lockoutOnFailure: true es OBLIGATORIO aquí
            var check = await signInManager.CheckPasswordSignInAsync(user, dto.Password, lockoutOnFailure: true);

            if (check.IsLockedOut)
            {
                return Results.BadRequest(new { message = "Tu cuenta ha sido bloqueada tras 3 intentos fallidos. Contacta al administrador para desbloquearla." });
            }

            if (!check.Succeeded)
            {
                // Calcular cuántos intentos le quedan
                var failedCount = await userManager.GetAccessFailedCountAsync(user);
                var maxAttempts = 3; // El límite que configuraste en Program.cs
                var remaining = maxAttempts - failedCount;

                if (remaining > 0)
                {
                    return Results.BadRequest(new { message = $"Contraseña incorrecta. Tienes {remaining} intento(s) más antes de bloquearse el usuario." });
                }

                return Results.BadRequest(new { message = "Correo o contraseña incorrectos." });
            }

            var roles = await userManager.GetRolesAsync(user);
            var claims = new List<Claim>
            {
                new Claim(JwtRegisteredClaimNames.Sub, user.Id),
                new Claim(ClaimTypes.Name, user.UserName ?? string.Empty),
                new Claim("FullName", $"{user.FirstName} {user.LastName}")
            };
            claims.AddRange(roles.Select(r => new Claim(ClaimTypes.Role, r)));

            var secKey = configuration["JwtOptions:SecurityKey"] ?? configuration["Jwt:Key"];
            var issuer = configuration["JwtOptions:Issuer"] ?? configuration["JwtOptions:ValidIssuer"] ?? configuration["Jwt:Issuer"] ?? configuration["Jwt:ValidIssuer"];
            var audience = configuration["JwtOptions:Audience"] ?? configuration["JwtOptions:ValidAudience"] ?? configuration["Jwt:Audience"] ?? configuration["Jwt:ValidAudience"];
            var expireMinutesString = configuration["JwtOptions:ExpireInMinutes"] ?? configuration["JwtOptions:ExpireMinutes"] ?? configuration["Jwt:ExpireMinutes"] ?? "60";

            if (string.IsNullOrEmpty(secKey)) return Results.StatusCode(500);

            var key = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(secKey));
            var creds = new SigningCredentials(key, SecurityAlgorithms.HmacSha256);
            if (!int.TryParse(expireMinutesString, out var expireMinutes)) expireMinutes = 60;

            var token = new JwtSecurityToken(
                issuer: issuer,
                audience: audience,
                claims: claims,
                expires: DateTime.UtcNow.AddMinutes(expireMinutes),
                signingCredentials: creds);

            var tokenString = new JwtSecurityTokenHandler().WriteToken(token);
            return Results.Ok(new { token = tokenString, roles = roles });
        }
        catch (Exception ex)
        {
            return Results.Problem(detail: ex.ToString(), statusCode: 500);
        }
    }
}
