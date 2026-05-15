using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Entities.Interfaces
{
    public interface IMailService
    {
        Task SendMailToAdministrator(string subject, string body);
    }
}
