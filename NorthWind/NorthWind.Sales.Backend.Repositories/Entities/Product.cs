using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.Repositories.Entities
{
    public class Product
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public short UnitsInStock { get; set; }
        public decimal UnitPrice { get; set; }
        public byte[]? ImageData { get; set; }
        public string? ImageContentType { get; set; }
    }
}
