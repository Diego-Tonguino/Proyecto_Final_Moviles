using System;
using System.Collections.Generic;
using System.Linq;
using System.Linq.Expressions;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Entities.Abstractions
{
    public abstract class Specification<T>
    {
        public abstract Expression<Func<T, bool>> ConditionExpression { get; }
        public bool IsSatisfiedBy(T entityInstance)
        {
            Func<T, bool> ExpressionDelegate = ConditionExpression.Compile();
            return ExpressionDelegate(entityInstance);
        }
    }
}
