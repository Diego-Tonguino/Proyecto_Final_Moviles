using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Identity;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using System.Threading.Tasks;
using System.Linq;
using System.Collections.Generic;
using System;
using NorthWind.Sales.Backend.Controllers;
using NorthWind.Sales.Backend.Repositories.Interfaces; // <-- Añadido para acceder a INorthWindSalesQueriesDataContext

namespace NorthWind.Sales.Backend.Controllers.Admin;

public static class UsersController
{
    public record UserDto(string Id, string UserName, string Email, string? FirstName, string? LastName, bool IsLockedOut, IList<string> Roles, bool HasProfilePicture, string? ProfilePictureContentType);
    public record CreateUserDto(string UserName, string Email, string Password, string? FirstName, string? LastName, bool IsLockedOut, IList<string> Roles);
    public record UpdateUserDto(string UserName, string Email, string? FirstName, string? LastName, bool IsLockedOut, IList<string> Roles, string? Password);

    public static WebApplication UseUsersController(this WebApplication app)
    {
        app.MapGet("/api/users", GetUsers)
            .WithName("GetUsers")
            .Produces<List<UserDto>>(StatusCodes.Status200OK);
        app.MapGet("/api/users/{id}", GetUserById);
        app.MapPost("/api/users", CreateUser);
        app.MapPut("/api/users/{id}", UpdateUser);
        app.MapDelete("/api/users/{id}", DeleteUser);

        // Endpoints para foto de perfil
        app.MapPost("/api/users/{id}/profile-picture", UploadProfilePicture)
            .DisableAntiforgery();
        app.MapGet("/api/users/{id}/profile-picture", GetProfilePicture);
        app.MapDelete("/api/users/{id}/profile-picture", DeleteProfilePicture);

        return app;
    }

    private static async Task<IResult> GetUsers(UserManager<ApplicationUser> userManager)
    {
        try
        {
            var allUsers = userManager.Users.ToList();
            var usersDto = new List<UserDto>();

            foreach (var user in allUsers)
            {
                var roles = await userManager.GetRolesAsync(user);
                bool isLockedOut = user.LockoutEnd.HasValue && user.LockoutEnd.Value > DateTimeOffset.UtcNow;
                bool hasProfilePicture = user.ProfilePictureData != null && user.ProfilePictureData.Length > 0;
                usersDto.Add(new UserDto(user.Id, user.UserName, user.Email, user.FirstName, user.LastName, isLockedOut, roles, hasProfilePicture, user.ProfilePictureContentType));
            }

            return Results.Ok(usersDto);
        }
        catch (Exception ex)
        {
            return Results.Problem($"Error interno en la API: {ex.Message}");
        }
    }

    private static async Task<IResult> GetUserById(string id, HttpContext httpContext)
    {
        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();
        var user = await userManager.FindByIdAsync(id);
        if (user == null) return Results.NotFound();

        var roles = await userManager.GetRolesAsync(user);
        bool isLockedOut = user.LockoutEnd.HasValue && user.LockoutEnd.Value > DateTimeOffset.UtcNow;
        bool hasProfilePicture = user.ProfilePictureData != null && user.ProfilePictureData.Length > 0;

        return Results.Ok(new UserDto(user.Id, user.UserName, user.Email, user.FirstName, user.LastName, isLockedOut, roles, hasProfilePicture, user.ProfilePictureContentType));
    }

    private static async Task<IResult> CreateUser(CreateUserDto dto, HttpContext httpContext, INorthWindSalesQueriesDataContext queries)
    {
        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();

        // 1. Validar que el correo no esté usado por otro Usuario
        var userExists = await userManager.FindByEmailAsync(dto.Email);
        if (userExists != null)
        {
            return Results.BadRequest(new { error = "El correo electrónico ya está registrado por otra persona en el sistema." });
        }

        // 2. CROSS-VALIDATION: Validar que el correo no esté en la tabla Clientes
        var customerExists = await queries.FirstOrDefaultAync(queries.Customers.Where(c => c.Email == dto.Email));
        if (customerExists != null)
        {
            return Results.BadRequest(new { error = "El correo electrónico ya está registrado por otra persona en el sistema." });
        }

        var user = new ApplicationUser
        {
            UserName = dto.UserName,
            Email = dto.Email,
            FirstName = dto.FirstName,
            LastName = dto.LastName,
            LockoutEnabled = true
        };

        if (dto.IsLockedOut)
        {
            user.LockoutEnd = DateTimeOffset.MaxValue;
        }

        var result = await userManager.CreateAsync(user, dto.Password);
        if (!result.Succeeded) return Results.ValidationProblem(result.Errors.ToDictionary(e => e.Code, e => new[] { e.Description }));

        if (dto.Roles != null && dto.Roles.Count > 0)
        {
            foreach (var roleName in dto.Roles)
            {
                await userManager.AddToRoleAsync(user, roleName);
            }
        }

        bool isLockedOut = user.LockoutEnd.HasValue && user.LockoutEnd.Value > DateTimeOffset.UtcNow;
        return Results.Created($"/api/users/{user.Id}", new UserDto(user.Id, user.UserName, user.Email, user.FirstName, user.LastName, isLockedOut, await userManager.GetRolesAsync(user), false, null));
    }

    private static async Task<IResult> UpdateUser(string id, UpdateUserDto dto, HttpContext httpContext, INorthWindSalesQueriesDataContext queries)
    {
        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();

        var user = await userManager.FindByIdAsync(id);
        if (user == null) return Results.NotFound();

        // 1. Validar que el correo no esté usado por otro Usuario distinto al actual
        var userExists = await userManager.FindByEmailAsync(dto.Email);
        if (userExists != null && userExists.Id != id)
        {
            return Results.BadRequest(new { error = "El correo electrónico ya está registrado por otra persona en el sistema." });
        }

        // 2. CROSS-VALIDATION: Validar que el correo no esté en la tabla Clientes
        var customerExists = await queries.FirstOrDefaultAync(queries.Customers.Where(c => c.Email == dto.Email));
        if (customerExists != null)
        {
            return Results.BadRequest(new { error = "El correo electrónico ya está registrado por otra persona en el sistema." });
        }

        user.UserName = dto.UserName;
        user.Email = dto.Email;
        user.FirstName = dto.FirstName;
        user.LastName = dto.LastName;

        user.LockoutEnabled = true;

        if (dto.IsLockedOut)
        {
            user.LockoutEnd = DateTimeOffset.MaxValue;
        }
        else
        {
            user.LockoutEnd = null;
            user.AccessFailedCount = 0;
        }

        var updateResult = await userManager.UpdateAsync(user);
        if (!updateResult.Succeeded) return Results.ValidationProblem(updateResult.Errors.ToDictionary(e => e.Code, e => new[] { e.Description }));

        if (dto.Roles != null)
        {
            var currentRoles = await userManager.GetRolesAsync(user);
            var rolesToRemove = currentRoles.Except(dto.Roles).ToList();
            var rolesToAdd = dto.Roles.Except(currentRoles).ToList();

            if (rolesToRemove.Any()) await userManager.RemoveFromRolesAsync(user, rolesToRemove);

            foreach (var roleName in rolesToAdd)
            {
                await userManager.AddToRoleAsync(user, roleName);
            }
        }

        if (!string.IsNullOrEmpty(dto.Password))
        {
            var token = await userManager.GeneratePasswordResetTokenAsync(user);
            var passResult = await userManager.ResetPasswordAsync(user, token, dto.Password);
            if (!passResult.Succeeded) return Results.ValidationProblem(passResult.Errors.ToDictionary(e => e.Code, e => new[] { e.Description }));
        }

        return Results.NoContent();
    }

    private static async Task<IResult> DeleteUser(string id, HttpContext httpContext)
    {
        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();
        var user = await userManager.FindByIdAsync(id);
        if (user == null) return Results.NotFound();
        try
        {
            var roles = await userManager.GetRolesAsync(user);
            if (roles.Any()) await userManager.RemoveFromRolesAsync(user, roles);

            var claims = await userManager.GetClaimsAsync(user);
            if (claims.Any()) await userManager.RemoveClaimsAsync(user, claims);

            var logins = await userManager.GetLoginsAsync(user);
            foreach (var l in logins)
            {
                await userManager.RemoveLoginAsync(user, l.LoginProvider, l.ProviderKey);
            }

            var result = await userManager.DeleteAsync(user);
            if (!result.Succeeded)
            {
                return Results.ValidationProblem(result.Errors.ToDictionary(e => e.Code, e => new[] { e.Description }));
            }
            return Results.NoContent();
        }
        catch (Exception ex)
        {
            return Results.Problem(detail: ex.Message, statusCode: 500);
        }
    }

    // ===================== ENDPOINTS DE FOTO DE PERFIL =====================

    private static async Task<IResult> UploadProfilePicture(string id, IFormFile image, HttpContext httpContext)
    {
        if (image == null || image.Length == 0)
            return Results.BadRequest(new { error = "No se proporciono una imagen valida." });

        var allowedTypes = new[] { "image/jpeg", "image/png", "image/gif", "image/webp" };
        if (!allowedTypes.Contains(image.ContentType.ToLower()))
            return Results.BadRequest(new { error = "Tipo de archivo no permitido. Use JPEG, PNG, GIF o WebP." });

        if (image.Length > 5 * 1024 * 1024)
            return Results.BadRequest(new { error = "La imagen no debe superar los 5 MB." });

        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();
        var user = await userManager.FindByIdAsync(id);
        if (user == null) return Results.NotFound(new { error = $"Usuario con ID '{id}' no encontrado." });

        using var memoryStream = new System.IO.MemoryStream();
        await image.CopyToAsync(memoryStream);
        user.ProfilePictureData = memoryStream.ToArray();
        user.ProfilePictureContentType = image.ContentType;

        var result = await userManager.UpdateAsync(user);
        if (!result.Succeeded) return Results.Problem("Error al actualizar la foto de perfil.");

        return Results.Ok(new { message = "Foto de perfil actualizada correctamente.", userId = id });
    }

    private static async Task<IResult> GetProfilePicture(string id, HttpContext httpContext)
    {
        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();
        var user = await userManager.FindByIdAsync(id);
        if (user == null) return Results.NotFound();

        if (user.ProfilePictureData == null || user.ProfilePictureData.Length == 0)
            return Results.NotFound(new { error = "Este usuario no tiene foto de perfil." });

        return Results.File(user.ProfilePictureData, user.ProfilePictureContentType ?? "image/jpeg");
    }

    private static async Task<IResult> DeleteProfilePicture(string id, HttpContext httpContext)
    {
        var userManager = httpContext.RequestServices.GetRequiredService<UserManager<ApplicationUser>>();
        var user = await userManager.FindByIdAsync(id);
        if (user == null) return Results.NotFound(new { error = $"Usuario con ID '{id}' no encontrado." });

        user.ProfilePictureData = null;
        user.ProfilePictureContentType = null;

        var result = await userManager.UpdateAsync(user);
        if (!result.Succeeded) return Results.Problem("Error al eliminar la foto de perfil.");

        return Results.Ok(new { message = "Foto de perfil eliminada correctamente.", userId = id });
    }
}