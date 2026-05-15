using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Transactions.Entities.Interfaces
{
    public interface IDomainTransaction
    {
        void BeginTransaction();
        void CommitTransaction();
        void RollbackTransaction();
    }
}
