namespace NorthWind.Exceptions.Entities.Exceptions;

/// <summary>
/// Excepción que se lanza cuando se intenta eliminar un producto que tiene detalles de órdenes asociadas.
/// Esto viola la integridad referencial de la base de datos.
/// </summary>
public class ProductHasOrderDetailsException : Exception
{
    public int ProductId { get; set; }

    public ProductHasOrderDetailsException(int productId) 
        : base($"No se puede eliminar el producto con ID '{productId}' porque tiene detalles en órdenes asociadas. " +
                "Elimine primero todas las órdenes que contengan este producto.")
    {
        ProductId = productId;
    }
}
