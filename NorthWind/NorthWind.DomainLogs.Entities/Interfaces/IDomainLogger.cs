using NorthWind.DomainLogs.Entities.ValueObjects;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.DomainLogs.Entities.Interfaces
{
    public interface IDomainLogger
    {
        Task LogInformation(DomainLog log);
    }
}
