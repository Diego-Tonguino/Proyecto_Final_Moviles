using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories
{
    public interface IQueriesRepository
    {
        Task<decimal?> GetCustomerCurrentBalance(string customerId);
        Task<IEnumerable<ProductUnitsInStock>> 
            GetProductsUnitsInStock(IEnumerable<int> productIds);
    }
}
