using NorthWind.DomainLogs.Entities.Interfaces;
using NorthWind.DomainLogs.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.DomainLogs.Entities.Services
{
    internal class DomainLogger(IDomainLogsRepository repository) : IDomainLogger
    {
        public async Task LogInformation(DomainLog log)
        {
            await repository.Add(log);
            await repository.SaveChanges();
        }
    }
}
