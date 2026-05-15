using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.Repositories.Entities
{
    public class DomainLog
    {
        public int Id { get; set; }
        public DateTime CreatedDate { get; set; }
        public string? Information { get; set; }
        public string? UserName { get; set; }
    }
}
