namespace NorthWind.Sales.Entities.Dtos.Orders;

public class OrderDto
{
    public int Id { get; set; }
    public string CustomerId { get; set; }
    public string ShipAddress { get; set; }
    public string ShipCity { get; set; }
    public string ShipCountry { get; set; }
    public string ShipPostalCode { get; set; }
    public DateTime OrderDate { get; set; }
    public int ShippingType { get; set; }
    public int DiscountType { get; set; }
    public double Discount { get; set; }
    public List<OrderDetailDto> OrderDetails { get; set; } = new();
}

public class OrderDetailDto
{
    public int OrderId { get; set; }
    public int ProductId { get; set; }
    public decimal UnitPrice { get; set; }
    public short Quantity { get; set; }
}
