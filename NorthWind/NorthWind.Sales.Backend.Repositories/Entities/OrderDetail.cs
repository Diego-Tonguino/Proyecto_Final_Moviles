using NorthWind.Sales.Backend.BusinessObjects.POCOEntities;

namespace NorthWind.Sales.Backend.Repositories.Entities;

/// <summary>
/// Aqui ya pensamos en sentido de Base de Datos.
/// Esta estructura de datos permite almacenar los datos de una orden. Sirve para
/// hacer referencia a la tabla de base de datos y al ORM Entity Framework.
/// Aqui definiremos las entidades con las cuales nos comunicaremos con la herramienta ORM.
/// La propiedad "Order" creada en la clase permite establecer la relación
/// entre una "orden" y su "detalle".
/// 
/// NOTA: Para comunicarnos con el ORM (Entity Framework) se esta reutilizando la entity "Order"
///       de la capa "BusinessObjects" la cual se encuentra en la carpeta (POCOEntities). 
///       Se puede reutilizar la entity "Order" porque esta tiene la información que se requiere.
///       Pero si se desea se puede crear otra entity para "Order" aquí en este proyecto que
///       se ubicaría en la carpeta "Entities" .
///       En cambio se debe "crear necesariamente" una entity para "OrderDetail" porque la
///       entidad (Datos) que se requiere debe tener el campo "OrderId", campo que no tiene el
///       value object de la capa "BusinessObjects", como es diferente por el campo "OrderId"
///       por lo tanto es diferente de otras entities o value object de la capa "BusinessObjects".
///       El campo "OrderId" es necesario crear porque actúa como la "clave foranea" para poder
///       guardar el detalle de la orden en la Base de Datos por medio del ORM Entity Framework
///       que vamos a utilizar.
///       
/// </summary>
public class OrderDetail
{
  public int OrderId { get; set; } // Para relacionar el detalle con la orden.
  public Order Order { get; set; } // Permite establecer la relación entre una orden y su detalle.
  public int ProductId { get; set; } // Id del producto.
  public decimal UnitPrice { get; set; }
  public short Quantity { get; set; }
}
