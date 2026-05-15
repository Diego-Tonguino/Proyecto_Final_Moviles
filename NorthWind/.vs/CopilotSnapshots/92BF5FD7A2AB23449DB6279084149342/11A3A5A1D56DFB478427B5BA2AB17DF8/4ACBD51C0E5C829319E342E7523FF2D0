using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.Extensions.Options;
using NorthWind.Sales.Backend.DataContexts.EFCore.DataContexts;
using NorthWind.Sales.Backend.DataContexts.EFCore.Options;
using NorthWind.Sales.Backend.Repositories.Entities;
using NorthWind.Sales.Backend.Repositories.Interfaces;
using NorthWind.Sales.Entities.Dtos.Orders;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.DataContexts.EFCore.Services
{
    internal class NorthWindSalesQueriesDataContext :
        NorthWindSalesContext,
        INorthWindSalesQueriesDataContext
    {
        public NorthWindSalesQueriesDataContext(IOptions<DBOptions> dbOptions)
        : base(dbOptions)
        {
            ChangeTracker.QueryTrackingBehavior =
            QueryTrackingBehavior.NoTracking;
        }

        public new IQueryable<Customer> Customers => base.Customers;
        public new IQueryable<Product> Products => base.Products;
        public Task<ReturnType> FirstOrDefaultAync<ReturnType>(
        IQueryable<ReturnType> queryable) =>
        queryable.FirstOrDefaultAsync();
        public async Task<IEnumerable<ReturnType>> ToListAsync<ReturnType>(
            IQueryable<ReturnType> queryable) =>
    await queryable.ToListAsync();

        // Implement convenience methods declared on the interface
        public async Task<IEnumerable<OrderDto>> GetAllOrdersAsync()
        {
            var query = Orders.Select(o => new OrderDto
            {
                Id = o.Id,
                CustomerId = o.CustomerId,
                ShipAddress = o.ShipAddress,
                ShipCity = o.ShipCity,
                ShipCountry = o.ShipCountry,
                ShipPostalCode = o.ShipPostalCode,
                OrderDate = o.OrderDate,
                ShippingType = (int)o.ShippingType,
                DiscountType = (int)o.DiscountType,
                Discount = o.Discount,
                OrderDetails = OrderDetails
                    .Where(od => od.OrderId == o.Id)
                    .Select(od => new OrderDetailDto
                    {
                        OrderId = od.OrderId,
                        ProductId = od.ProductId,
                        UnitPrice = od.UnitPrice,
                        Quantity = od.Quantity
                    }).ToList()
            });

            return await query.ToListAsync();
        }

        public async Task<OrderDto?> GetOrderByIdAsync(int orderId)
        {
            var query = Orders
                .Where(o => o.Id == orderId)
                .Select(o => new OrderDto
                {
                    Id = o.Id,
                    CustomerId = o.CustomerId,
                    ShipAddress = o.ShipAddress,
                    ShipCity = o.ShipCity,
                    ShipCountry = o.ShipCountry,
                    ShipPostalCode = o.ShipPostalCode,
                    OrderDate = o.OrderDate,
                    ShippingType = (int)o.ShippingType,
                    DiscountType = (int)o.DiscountType,
                    Discount = o.Discount,
                    OrderDetails = OrderDetails
                        .Where(od => od.OrderId == o.Id)
                        .Select(od => new OrderDetailDto
                        {
                            OrderId = od.OrderId,
                            ProductId = od.ProductId,
                            UnitPrice = od.UnitPrice,
                            Quantity = od.Quantity
                        }).ToList()
                });

            return await query.FirstOrDefaultAsync();
        }
    }
}
