using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;
using NorthWind.Sales.Backend.DataContexts.EFCore.DataContexts;
using NorthWind.Sales.Backend.DataContexts.EFCore.Options;
using NorthWind.Sales.Backend.Repositories.Interfaces;
using NorthWind.Sales.Backend.Repositories.Entities;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.DataContexts.EFCore.Services
{
    internal class NorthWindDomainLogsQueriesDataContext(IOptions<DBOptions> dbOptions) :
        NorthWindDomainLogsContext(dbOptions), INorthWindDomainLogsQueriesDataContext
    {
        public async Task<IEnumerable<DomainLog>> GetLatestLogsAsync(int top)
        {
            // Order by CreatedDate or Id descending to get latest
            return await DomainLogs
                .OrderByDescending(d => d.CreatedDate)
                .Take(top)
                .AsNoTracking()
                .ToListAsync();
        }
    }
}
