namespace NorthWind.Sales.Entities.Dtos.CreateOrder;

//**********************************************************
//  PRIMERA FORMA
//  Usando: "Primary Constructors de C# 12"
//**********************************************************

// Función: Los Dtos sirven para transportar información entre capas.
//          Este Dto contiene los datos solo de lectura para guardar los datos (detalle) de una
//          orden.
//          Se va a utilizar en la Web API y la aplicacion Blazor.
public class CreateOrderDetailDto(int productId, decimal unitPrice, short quantity)
{
  public int ProductId => productId;
  public decimal UnitPrice => unitPrice;
  public short Quantity => quantity;
}


//**********************************************************
//  SEGUNDA FORMA
//  Usando: código transformado a C# 3.0 o anterior:
//**********************************************************
//public class CreateOrderDetailDto
//{
//  // Campos privados para almacenar los valores
//  private int _productId;
//  private decimal _unitPrice;
//  private short _quantity;

//  // Constructor para inicializar los campos
//  public CreateOrderDetailDto(int productId, decimal unitPrice, short quantity)
//  {
//    _productId = productId;
//    _unitPrice = unitPrice;
//    _quantity = quantity;
//  }

//  // Propiedades de solo lectura para acceder a los valores
//  public int ProductId
//  {
//    get { return _productId; }
//  }

//  public decimal UnitPrice
//  {
//    get { return _unitPrice; }
//  }

//  public short Quantity
//  {
//    get { return _quantity; }
//  }
//}

//**********************************************************
// TERCERA FORMA
// Usando: código transformado a C# 3.0 o anterior: campos readonly
//**********************************************************
//public class CreateOrderDetailDto
//{
//  private readonly int _productId;
//  private readonly decimal _unitPrice;
//  private readonly short _quantity;

//  public CreateOrderDetailDto(int productId, decimal unitPrice, short quantity)
//  {
//    _productId = productId;
//    _unitPrice = unitPrice;
//    _quantity = quantity;
//  }

//  public int ProductId
//  {
//    get { return _productId; }
//  }

//  public decimal UnitPrice
//  {
//    get { return _unitPrice; }
//  }

//  public short Quantity
//  {
//    get { return _quantity; }
//  }
//}