//using NorthWind.Sales.Backend.BusinessObjects.Enums;

namespace NorthWind.Sales.Backend.BusinessObjects.POCOEntities;

//  FUNCION: Una entidad contiene las reglas del negocio es decir las reglas de la aplicación.
//  Una entidad si tiene identidad (un campo que haga unico a una orden del mismo tipo).
//  En las entidades se puede empezar a implementar los requerimientos del software.
//  NOTA: La clase "Order" es el objeto root (raiz) del agregado.
public class Order
{
  public int Id { get; set; } // este campo hace que una orden sea distinta de otra orden.
  public string CustomerId { get; set; }
  public string ShipAddress { get; set; }
  public string ShipCity { get; set; }
  public string ShipCountry { get; set; }
  public string ShipPostalCode { get; set; }

  //  Aqui implementamos reglas de negocio.
  //  NOTA: Estos valores se pasan por defecto por lo tanto no es necesario suministrarlos
  //        en el Dto.
  public ShippingType ShippingType { get; set; } = ShippingType.Road;
  public DiscountType DiscountType { get; set; } = DiscountType.Percentage;
  public double Discount { get; set; } = 10;
  public DateTime OrderDate { get; set; } = DateTime.Now;

  // Aqui "También" se puede poner el listado de los detalles de la orden
  // para lo cual se puede utilzar un campo que sea una colección.
}