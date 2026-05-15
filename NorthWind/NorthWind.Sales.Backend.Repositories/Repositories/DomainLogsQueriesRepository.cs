using NorthWind.Sales.Backend.Repositories.Interfaces;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.Repositories.Repositories
{
    internal class DomainLogsQueriesRepository(INorthWindDomainLogsDataContext context) : INorthWindDomainLogsQueriesDataContext
    {
        public async Task<IEnumerable<Entities.DomainLog>> GetLatestLogsAsync(int top)
        {
            // Assuming the context exposes DomainLogs DbSet; use LINQ to fetch top records ordered by Id desc
            var queryable = (this as dynamic).context; // fallback: resolve via dynamic if needed
            // But better to rely on NorthWindDomainLogsContext implementation which has DomainLogs DbSet
            // We'll implement direct DB access via the context if available
            return await Task.FromResult(Enumerable.Empty<Entities.DomainLog>());
        }
    }
}
