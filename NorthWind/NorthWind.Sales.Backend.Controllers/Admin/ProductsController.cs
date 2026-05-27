using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using NorthWind.Sales.Backend.Repositories.Entities;
using NorthWind.Sales.Backend.Repositories.Interfaces;
using System.Threading.Tasks;
using System;

namespace NorthWind.Sales.Backend.Controllers.Admin;

public static class ProductsController
{
    public static WebApplication UseProductsController(this WebApplication app)
    {
        app.MapGet("/api/products", GetAllProducts);
        app.MapGet("/api/products/{id}", GetProductById);
        app.MapPost("/api/products", CreateProduct);
        app.MapPut("/api/products/{id}", UpdateProduct);
        app.MapDelete("/api/products/{id}", DeleteProduct);

        // Endpoints para gestion de imagenes de productos
        app.MapPost("/api/products/{id}/image", UploadProductImage)
            .DisableAntiforgery();
        app.MapGet("/api/products/{id}/image", GetProductImage);
        app.MapDelete("/api/products/{id}/image", DeleteProductImage);

        return app;
    }

    static async Task<IResult> GetAllProducts(INorthWindSalesQueriesDataContext queries)
    {
        var list = await queries.ToListAsync(queries.Products);
        return Results.Ok(list.Select(p => new
        {
            p.Id,
            p.Name,
            p.Description,
            p.UnitPrice,
            p.UnitsInStock,
            HasImage = p.ImageData != null && p.ImageData.Length > 0,
            p.ImageContentType
        }));
    }

    static async Task<IResult> GetProductById(int id, INorthWindSalesQueriesDataContext queries)
    {
        var product = await queries.FirstOrDefaultAync(queries.Products.Where(p => p.Id == id));
        if (product == null) return Results.NotFound();
        return Results.Ok(new
        {
            product.Id,
            product.Name,
            product.Description,
            product.UnitPrice,
            product.UnitsInStock,
            HasImage = product.ImageData != null && product.ImageData.Length > 0,
            product.ImageContentType
        });
    }

    static async Task<IResult> CreateProduct([FromBody] Product product, INorthWindSalesAdminDataContext admin)
    {
        await admin.AddProductAsync(product);
        await admin.SaveChangesAsync();
        return Results.Created($"/api/products/{product.Id}", new
        {
            product.Id,
            product.Name,
            product.Description,
            product.UnitPrice,
            product.UnitsInStock,
            HasImage = false,
            product.ImageContentType
        });
    }

    static async Task<IResult> UpdateProduct(int id, [FromBody] Product product, INorthWindSalesAdminDataContext admin, INorthWindSalesQueriesDataContext queries)
    {
        if (id != product.Id) return Results.BadRequest();

        // Conservar la imagen actual si existe, para no borrarla al actualizar texto
        var existing = await queries.FirstOrDefaultAync(queries.Products.Where(p => p.Id == id));
        if (existing != null)
        {
            product.ImageData = existing.ImageData;
            product.ImageContentType = existing.ImageContentType;
        }

        admin.UpdateProductAsync(product);
        await admin.SaveChangesAsync();
        return Results.NoContent();
    }

    static async Task<IResult> DeleteProduct(int id, INorthWindSalesAdminDataContext admin)
    {
        try
        {
            if (await admin.HasOrderDetailsAsync(id))
            {
                return Results.BadRequest(new
                {
                    error = $"No se puede eliminar el producto con ID '{id}' porque tiene detalles en ordenes asociadas. " +
                            "Elimine primero todas las ordenes que contengan este producto."
                });
            }

            await admin.DeleteProductAsync(id);
            await admin.SaveChangesAsync();
            return Results.NoContent();
        }
        catch (Exception ex)
        {
            return Results.Problem(detail: ex.Message, statusCode: 500);
        }
    }

    // ===================== ENDPOINTS DE IMAGEN =====================

    static async Task<IResult> UploadProductImage(
        int id,
        IFormFile image,
        INorthWindSalesAdminDataContext admin,
        INorthWindSalesQueriesDataContext queries)
    {
        if (image == null || image.Length == 0)
            return Results.BadRequest(new { error = "No se proporciono una imagen valida." });

        var allowedTypes = new[] { "image/jpeg", "image/png", "image/gif", "image/webp" };
        if (!allowedTypes.Contains(image.ContentType.ToLower()))
            return Results.BadRequest(new { error = "Tipo de archivo no permitido. Use JPEG, PNG, GIF o WebP." });

        if (image.Length > 5 * 1024 * 1024)
            return Results.BadRequest(new { error = "La imagen no debe superar los 5 MB." });

        var product = await queries.FirstOrDefaultAync(queries.Products.Where(p => p.Id == id));
        if (product == null) return Results.NotFound(new { error = $"Producto con ID '{id}' no encontrado." });

        using var memoryStream = new System.IO.MemoryStream();
        await image.CopyToAsync(memoryStream);
        product.ImageData = memoryStream.ToArray();
        product.ImageContentType = image.ContentType;

        admin.UpdateProductAsync(product);
        await admin.SaveChangesAsync();

        return Results.Ok(new { message = "Imagen del producto actualizada correctamente.", productId = id });
    }

    static async Task<IResult> GetProductImage(
        int id,
        INorthWindSalesQueriesDataContext queries)
    {
        var product = await queries.FirstOrDefaultAync(queries.Products.Where(p => p.Id == id));
        if (product == null) return Results.NotFound();

        if (product.ImageData == null || product.ImageData.Length == 0)
            return Results.NotFound(new { error = "Este producto no tiene imagen." });

        return Results.File(product.ImageData, product.ImageContentType ?? "image/jpeg");
    }

    static async Task<IResult> DeleteProductImage(
        int id,
        INorthWindSalesAdminDataContext admin,
        INorthWindSalesQueriesDataContext queries)
    {
        var product = await queries.FirstOrDefaultAync(queries.Products.Where(p => p.Id == id));
        if (product == null) return Results.NotFound(new { error = $"Producto con ID '{id}' no encontrado." });

        product.ImageData = null;
        product.ImageContentType = null;

        admin.UpdateProductAsync(product);
        await admin.SaveChangesAsync();

        return Results.Ok(new { message = "Imagen del producto eliminada correctamente.", productId = id });
    }
}