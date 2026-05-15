using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.AspNetIdentity.Options
{
    public class MembershipDBOptions
    {
        public const string SectionKey = nameof(MembershipDBOptions);
        public string? ConnectionString { get; set; }
    }
}
