using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Identity;
using NorthWind.Sales.Backend.Controllers;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;
using System.Threading.Tasks;
using System.Collections.Generic;

namespace NorthWind.Sales.Backend.Controllers.Auth;

/// <summary>
/// Controlador para el registro de clientes.
/// Permite que los clientes se autorregistren con el rol "Cliente".
/// </summary>
public static class ClientRegistrationController
{
    public record RegisterClientDto(
        string UserName, 
        string Email, 
        string Password, 
        string PasswordConfirm,
        string FirstName, 
        string LastName);

    public record RegisterResponseDto(
        string Message, 
        string UserId, 
        string UserName);

    public static WebApplication UseClientRegistrationController(this WebApplication app)
    {
        app.MapPost("/api/auth/register-client", RegisterClient);
        return app;
    }

    /// <summary>
    /// Permite que un cliente se autorregistre en el sistema.
    /// El usuario creado se asigna automáticamente al rol "Cliente".
    /// </summary>
    private static async Task<IResult> RegisterClient(
        RegisterClientDto dto, 
        HttpContext httpContext)
    {
        try
        {
            // Validaciones básicas
            if (string.IsNullOrWhiteSpace(dto.UserName))
                return Results.BadRequest(new { error = "El nombre de usuario es requerido." });

            if (string.IsNullOrWhiteSpace(dto.Email))
                return Results.BadRequest(new { error = "El correo electrónico es requerido." });

            if (string.IsNullOrWhiteSpace(dto.Password))
                return Results.BadRequest(new { error = "La contraseña es requerida." });

            if (dto.Password != dto.PasswordConfirm)
                return Results.BadRequest(new { error = "Las contraseñas no coinciden." });

            if (dto.Password.Length < 6)
                return Results.BadRequest(new { error = "La contraseña debe tener al menos 6 caracteres." });

            var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();

            // Verificar que el usuario no exista
            var existingUser = await userManager.FindByNameAsync(dto.UserName);
            if (existingUser != null)
                return Results.BadRequest(new { error = $"El nombre de usuario '{dto.UserName}' ya está en uso." });

            // Verificar que el email no exista
            var existingEmail = await userManager.FindByEmailAsync(dto.Email);
            if (existingEmail != null)
                return Results.BadRequest(new { error = $"El correo '{dto.Email}' ya está registrado." });

            // Crear el nuevo usuario cliente
            var newUser = new ApplicationUser
            {
                UserName = dto.UserName,
                Email = dto.Email,
                FirstName = dto.FirstName ?? string.Empty,
                LastName = dto.LastName ?? string.Empty,
                LockoutEnabled = false  // Los clientes no pueden ser bloqueados por defecto
            };

            var result = await userManager.CreateAsync(newUser, dto.Password);

            if (!result.Succeeded)
            {
                var errors = string.Join("; ", result.Errors.Select(e => e.Description));
                return Results.BadRequest(new { error = errors });
            }

            // Asignar el rol "Cliente" al nuevo usuario
            var roleResult = await userManager.AddToRoleAsync(newUser, "Cliente");

            if (!roleResult.Succeeded)
            {
                // Si falla la asignación del rol, eliminar el usuario creado
                await userManager.DeleteAsync(newUser);
                var errors = string.Join("; ", roleResult.Errors.Select(e => e.Description));
                return Results.Problem(
                    detail: $"Error al asignar el rol Cliente: {errors}",
                    statusCode: 500);
            }

            return Results.Created(
                $"/api/users/{newUser.Id}",
                new RegisterResponseDto(
                    Message: $"Cliente '{dto.UserName}' registrado exitosamente. Ya puedes iniciar sesión.",
                    UserId: newUser.Id,
                    UserName: newUser.UserName));
        }
        catch (Exception ex)
        {
            return Results.Problem(
                detail: $"Error durante el registro: {ex.Message}",
                statusCode: 500);
        }
    }
}
