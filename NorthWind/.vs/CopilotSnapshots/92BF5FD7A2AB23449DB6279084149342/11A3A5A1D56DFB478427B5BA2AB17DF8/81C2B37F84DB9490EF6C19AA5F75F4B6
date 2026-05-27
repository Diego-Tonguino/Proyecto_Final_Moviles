namespace NorthWind.Sales.Entities.Dtos.CreateOrder;

//  Usando: "Primary Constructors de C# 12"
//  Función: Los Dtos sirver para transportar información de solo lectura.
//  Va a guardar los datos (encabezado) de una orden.
//  Se va a utilizar en la Web API y la aplicacion Blazor.
public class CreateOrderDto(string customerId, string shipAddress, string shipCity,
  string shipCountry, string shipPostalCode, IEnumerable<CreateOrderDetailDto> orderDetails)
{
  public string CustomerId => customerId;
  public string ShipAddress => shipAddress;
  public string ShipCity => shipCity;
  public string ShipCountry => shipCountry;
  public string ShipPostalCode => shipPostalCode;
  public IEnumerable<CreateOrderDetailDto> OrderDetails => orderDetails;// detalles de la orden.
}
