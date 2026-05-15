namespace NorthWind.Exceptions.Entities.Exceptions;

/// <summary>
/// Excepción que se lanza cuando se intenta eliminar un cliente que tiene órdenes asociadas.
/// Esto viola la integridad referencial de la base de datos.
/// </summary>
public class CustomerHasOrdersException : Exception
{
    public string CustomerId { get; set; }

    public CustomerHasOrdersException(string customerId) 
        : base($"No se puede eliminar el cliente '{customerId}' porque tiene órdenes asociadas. " +
                "Elimine primero todas las órdenes relacionadas.")
    {
        CustomerId = customerId;
    }
}
