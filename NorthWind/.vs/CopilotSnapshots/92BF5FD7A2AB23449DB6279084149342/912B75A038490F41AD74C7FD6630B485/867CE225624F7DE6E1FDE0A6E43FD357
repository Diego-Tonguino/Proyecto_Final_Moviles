using NorthWind.Sales.Backend.BusinessObjects.Interfaces.Repositories;
using NorthWind.Sales.Backend.BusinessObjects.ValueObjects;
using NorthWind.Sales.Backend.Repositories.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.Repositories.Repositories
{
    internal class QueriesRepository(INorthWindSalesQueriesDataContext context) :
 IQueriesRepository
    {
        public async Task<decimal?> GetCustomerCurrentBalance(
        string customerId)
        {
            var Queryable = context.Customers
            .Where(c => c.Id == customerId)
            .Select(c => new { c.CurrentBalance });
            var Result = await context.FirstOrDefaultAync(Queryable);
            return Result?.CurrentBalance;
        }
        public async Task<IEnumerable<ProductUnitsInStock>>
        GetProductsUnitsInStock(IEnumerable<int> productIds)
        {
            var Queryable = context.Products
            .Where(p => productIds.Contains(p.Id))
            .Select(p => new ProductUnitsInStock(
            p.Id, p.UnitsInStock));
            return await context.ToListAsync(Queryable);
        }
    }
}
