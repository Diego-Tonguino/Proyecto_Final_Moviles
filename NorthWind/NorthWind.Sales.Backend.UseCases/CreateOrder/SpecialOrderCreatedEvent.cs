using NorthWind.Events.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.UseCases.CreateOrder
{
    internal class SpecialOrderCreatedEvent(
        int orderId, int productsCount) : IDomainEvent
    {
        public int OrderId => orderId;
        public int ProductsCount => productsCount;
    }
}