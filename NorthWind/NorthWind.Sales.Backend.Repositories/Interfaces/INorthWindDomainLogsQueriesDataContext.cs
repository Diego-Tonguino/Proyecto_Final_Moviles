using System.Collections.Generic;
using System.Threading.Tasks;

namespace NorthWind.Sales.Backend.Repositories.Interfaces
{
    public interface INorthWindDomainLogsQueriesDataContext
    {
        Task<IEnumerable<Entities.DomainLog>> GetLatestLogsAsync(int top);
    }
}
