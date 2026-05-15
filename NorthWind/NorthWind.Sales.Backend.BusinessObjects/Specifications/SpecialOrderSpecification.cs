using NorthWind.Entities.Abstractions;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.BusinessObjects.Specifications
{
    public class SpecialOrderSpecification : Specification<OrderAggregate>
    {
        public override Expression<Func<OrderAggregate, bool>>
        ConditionExpression =>
        order => order.OrderDetails.Count > 3;
    }
}
