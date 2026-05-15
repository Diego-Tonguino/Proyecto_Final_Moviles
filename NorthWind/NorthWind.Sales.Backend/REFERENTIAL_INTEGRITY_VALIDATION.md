# Implementación de Validaciones de Integridad Referencial

## Problema
Al intentar eliminar un cliente o un producto que tiene facturas (órdenes) asociadas, la base de datos fallaba debido a violaciones de integridad referencial, ya que:
- **Clientes**: Tienen una relación uno-a-muchos con **Orders** (tabla Orders.CustomerId ? Customers.Id)
- **Productos**: Tienen una relación uno-a-muchos con **OrderDetails** (tabla OrderDetails.ProductId ? Products.Id)

## Solución Implementada

### 1. Nuevas Excepciones Personalizadas

Se crearon dos nuevas excepciones en `NorthWind.Exceptions.Entities\Exceptions\`:

#### `CustomerHasOrdersException.cs`
```csharp
public class CustomerHasOrdersException : Exception
{
    public string CustomerId { get; set; }
    // Se lanza cuando se intenta eliminar un cliente con órdenes asociadas
}
```

#### `ProductHasOrderDetailsException.cs`
```csharp
public class ProductHasOrderDetailsException : Exception
{
    public int ProductId { get; set; }
    // Se lanza cuando se intenta eliminar un producto con detalles de órdenes asociadas
}
```

### 2. Métodos de Validación en Repositorio

Se agregaron dos nuevos métodos a la interfaz `INorthWindSalesAdminDataContext`:

```csharp
Task<bool> HasOrdersAsync(string customerId);
Task<bool> HasOrderDetailsAsync(int productId);
```

Implementados en `NorthWindSalesCommandsDataContext`:

```csharp
public async Task<bool> HasOrdersAsync(string customerId)
{
    return await Orders.AnyAsync(o => o.CustomerId == customerId);
}

public async Task<bool> HasOrderDetailsAsync(int productId)
{
    return await OrderDetails.AnyAsync(od => od.ProductId == productId);
}
```

### 3. Validaciones en Controladores

#### `CustomersController.cs` - Método `DeleteCustomer`
```csharp
static async Task<IResult> DeleteCustomer(string id, 
    INorthWindSalesAdminDataContext admin)
{
    try
    {
        // Validar que el cliente no tenga órdenes asociadas
        if (await admin.HasOrdersAsync(id))
        {
            return Results.BadRequest(new 
            { 
                error = $"No se puede eliminar el cliente '{id}' porque tiene órdenes asociadas. " +
                        "Elimine primero todas las órdenes relacionadas."
            });
        }

        await admin.DeleteCustomerAsync(id);
        await admin.SaveChangesAsync();
        return Results.NoContent();
    }
    catch (Exception ex)
    {
        return Results.Problem(detail: ex.Message, statusCode: 500);
    }
}
```

#### `ProductsController.cs` - Método `DeleteProduct`
```csharp
static async Task<IResult> DeleteProduct(int id, 
    INorthWindSalesAdminDataContext admin)
{
    try
    {
        // Validar que el producto no tenga detalles de órdenes asociadas
        if (await admin.HasOrderDetailsAsync(id))
        {
            return Results.BadRequest(new 
            { 
                error = $"No se puede eliminar el producto con ID '{id}' porque tiene detalles en órdenes asociadas. " +
                        "Elimine primero todas las órdenes que contengan este producto."
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
```

## Archivos Modificados

1. **NorthWind.Sales.Backend.Repositories\Interfaces\INorthWindSalesCommandsDataContext.cs**
   - Agregadas dos nuevas interfaces de método

2. **NorthWind.Sales.Backend.DataContexts.EFCore\Services\NorthWindSalesCommandsDataContext.cs**
   - Implementados los métodos `HasOrdersAsync()` y `HasOrderDetailsAsync()`
   - Agregado `using Microsoft.EntityFrameworkCore;`

3. **NorthWind.Sales.Backend.Controllers\Admin\CustomersController.cs**
   - Actualizado método `DeleteCustomer()` con validación

4. **NorthWind.Sales.Backend.Controllers\Admin\ProductsController.cs**
   - Actualizado método `DeleteProduct()` con validación

## Archivos Creados

1. **NorthWind.Exceptions.Entities\Exceptions\CustomerHasOrdersException.cs**
2. **NorthWind.Exceptions.Entities\Exceptions\ProductHasOrderDetailsException.cs**

## Flujo de Validación

### Para Eliminar un Cliente:
1. El cliente envía una solicitud DELETE a `/api/customers/{id}`
2. El controlador verifica si el cliente tiene órdenes asociadas
3. Si tiene órdenes: Retorna **400 Bad Request** con mensaje descriptivo
4. Si no tiene órdenes: Procede a eliminar y retorna **204 No Content**

### Para Eliminar un Producto:
1. El cliente envía una solicitud DELETE a `/api/products/{id}`
2. El controlador verifica si el producto tiene detalles de órdenes asociadas
3. Si tiene detalles: Retorna **400 Bad Request** con mensaje descriptivo
4. Si no tiene detalles: Procede a eliminar y retorna **204 No Content**

## Ejemplo de Respuesta de Error

```json
{
    "error": "No se puede eliminar el cliente 'ALFKI' porque tiene órdenes asociadas. Elimine primero todas las órdenes relacionadas."
}
```

## Beneficios

? **Integridad Referencial**: Se previene la eliminación de datos relacionados  
? **Mensajes Claros**: El usuario entiende por qué no puede eliminar  
? **Seguridad de Datos**: Protege la consistencia de la base de datos  
? **Escalabilidad**: El patrón se puede extender a otras entidades  
? **Clean Architecture**: Mantiene las validaciones en la capa apropiada
