using NorthWind.Sales.Backend.Repositories.Entities;
using NorthWind.Sales.Entities.Dtos.Orders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.Repositories.Interfaces
{
    public interface INorthWindSalesQueriesDataContext
    {
        IQueryable<Customer> Customers { get; }
        IQueryable<Product> Products { get; }

        Task<ReturnType> FirstOrDefaultAync<ReturnType>(IQueryable<ReturnType> queryable);
        Task<IEnumerable<ReturnType>> ToListAsync<ReturnType>(IQueryable<ReturnType> queryable);

        // Convenience methods for orders used by controllers
        Task<IEnumerable<OrderDto>> GetAllOrdersAsync();
        Task<OrderDto?> GetOrderByIdAsync(int orderId);
    }
}
