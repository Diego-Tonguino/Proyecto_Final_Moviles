using Microsoft.AspNetCore.Builder;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.AspNetCore.Identity;
using System;

namespace NorthWind.Sales.WebApi;

public class Program
{
    public static void Main(string[] args)
    {
        //  "WebApplication.CreateBuilder(args)": Inicializa el builder de la aplicación web
        //  ".CreateWebApplication()": Configura servicios e inyección de dependencias
        //  ".ConfigureWebApplication()": Configura el pipeline de middlewares
        var app = WebApplication.CreateBuilder(args)
          .CreateWebApplication()
          .ConfigureWebApplication();

        // Seed roles on startup
        app.EnsureRolesAsync().GetAwaiter().GetResult();

        // --- NUEVO: Data Seeding para el Primer Usuario Administrador ---
        using (var scope = app.Services.CreateScope())
        {
            var services = scope.ServiceProvider;
            try
            {
                // NOTA: Si en tu proyecto creaste una clase personalizada para el usuario (ej. ApplicationUser),
                // cambia 'IdentityUser' por tu clase personalizada en las siguientes líneas.
                var userManager = services.GetRequiredService<UserManager<IdentityUser>>();

                string adminEmail = "admin@northwind.com";
                string adminPassword = "Password123!"; // Asegúrate de que cumpla con los requisitos (Mayúscula, minúscula, número y símbolo)
                string roleName = "Administrador";

                // 1. Verificamos si el usuario ya existe
                var user = userManager.FindByEmailAsync(adminEmail).GetAwaiter().GetResult();

                if (user == null)
                {
                    // 2. Preparamos el nuevo usuario
                    var newAdmin = new IdentityUser
                    {
                        UserName = adminEmail,
                        Email = adminEmail,
                        EmailConfirmed = true
                    };

                    // 3. Creamos el usuario en la BD con su contraseña encriptada
                    var createResult = userManager.CreateAsync(newAdmin, adminPassword).GetAwaiter().GetResult();

                    if (createResult.Succeeded)
                    {
                        // 4. Le asignamos el rol de Administrador
                        userManager.AddToRoleAsync(newAdmin, roleName).GetAwaiter().GetResult();
                        Console.WriteLine("Usuario administrador creado exitosamente.");
                    }
                    else
                    {
                        Console.WriteLine("No se pudo crear el administrador. Revisa las reglas de la contraseña.");
                    }
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine($"Ocurrió un error al intentar crear el administrador: {ex.Message}");
            }
        }
        // ----------------------------------------------------------------

        // Inicia el servidor web y pone la aplicación a la escucha de solicitudes HTTP
        app.Run();
    }
}