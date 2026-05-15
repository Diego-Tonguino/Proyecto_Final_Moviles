using Microsoft.AspNetCore.Hosting.Server;
using Microsoft.EntityFrameworkCore.Design;
using Microsoft.Extensions.Options;
using NorthWind.Sales.Backend.DataContexts.EFCore.Options;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using static Azure.Core.HttpHeader;

namespace NorthWind.Sales.Backend.DataContexts.EFCore.DataContexts
{
    internal class NorthWindDomainLogsContextFactory :
 IDesignTimeDbContextFactory<NorthWindDomainLogsContext>
    {
        public NorthWindDomainLogsContext CreateDbContext(string[] args)
        {
            IOptions<DBOptions> DbOptions =
            Microsoft.Extensions.Options.Options.Create(
            new DBOptions
            {
                DomainLogsConnectionString =
               "Server=AlexAnder;Database=NorthWindLogsDB;Trusted_Connection=True;TrustServerCertificate=True;"
            });
            return new NorthWindDomainLogsContext(DbOptions);
        }
    }
}
