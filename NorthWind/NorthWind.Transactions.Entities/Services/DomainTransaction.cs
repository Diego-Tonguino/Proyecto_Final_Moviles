using NorthWind.Transactions.Entities.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Transactions;

namespace NorthWind.Transactions.Entities.Services
{
    internal class DomainTransaction : IDomainTransaction, IDisposable
    {
        TransactionScope? TransactionScope;
        public void BeginTransaction()
        {
            TransactionManager.ImplicitDistributedTransactions = true;
            TransactionScope = new TransactionScope(
            TransactionScopeOption.Required,
            new TransactionOptions
            {
                IsolationLevel = IsolationLevel.ReadCommitted
            },
            TransactionScopeAsyncFlowOption.Enabled);
        }
        public void CommitTransaction()
        {
            TransactionScope.Complete();
            Dispose();
        }
        public void RollbackTransaction()
        {
            Dispose();
        }
        public void Dispose()
        {
            TransactionScope?.Dispose();
        }
    }
}
