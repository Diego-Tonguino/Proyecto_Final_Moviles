namespace NorthWind.Sales.Backend.BusinessObjects.ValueObjects;

//  FUNCIÓN: Permite guardar el detalle de la orden.
//           Son inmutables (solo de lectura).
public class OrderDetail(int productId, decimal unitPrice, short quantity)
{
  // Aqui "también" se puede poner el campo "Id" para identificar una orden.
  public int ProductId => productId;
  public decimal UnitPrice => unitPrice;
  public short Quantity => quantity;
}
