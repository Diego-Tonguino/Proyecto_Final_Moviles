using Microsoft.OpenApi.Models;

namespace Microsoft.Extensions.DependencyInjection;
public static class SwaggerExtensions
{
    public static IServiceCollection AddSwaggerGenBearer(
    this IServiceCollection services)
    {
        services.AddSwaggerGen(options =>
        {
            // Agregar definición de seguridad.
            options.AddSecurityDefinition("BearerToken",
            new OpenApiSecurityScheme
            {
                // Nombre del encabezado HTTP que se agregará en la petición.
                Name = "Authorization",
                Description = "Proporciona el valor del token.",
                // Ubicación del Token
                In = ParameterLocation.Header,
                // Tipo del esquema de seguridad a utilizar.
                // Al especificar HTTP, no necesitamos proporcionar
                // la palabra bearer antes del token, solo necesitamos
                // proporcionar el Token.
                Type = SecuritySchemeType.Http,
                // Nombre del esquema de autenticación.
                // Para HTTP puede ser Basic o Bearer (no es sensible
                // a mayúsculas/minúsculas)
                Scheme = "Bearer"
            });
            // Para agregar el encabezado de autorización en las
            // peticiones de endpoints.
            options.AddSecurityRequirement(
            new OpenApiSecurityRequirement
            {
                {
                    new OpenApiSecurityScheme
                    {
                        Reference = new OpenApiReference
                        {
                            Type = ReferenceType.SecurityScheme,
                            // Id definido en AddSecurityDefinition.
                            Id = "BearerToken"
                        }
                    },
                // Solo se requiere en oauth2 o OIDC para enviar
                // nombres de scopes.
                new string[] { }
                }
            });
        });
        return services;
    }
}