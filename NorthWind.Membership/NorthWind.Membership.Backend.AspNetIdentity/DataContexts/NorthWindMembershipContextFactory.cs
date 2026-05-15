using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using Microsoft.Extensions.Options;
using NorthWind.Membership.Backend.AspNetIdentity.Options;

namespace NorthWind.Membership.Backend.AspNetIdentity.DataContexts
{
    internal class NorthWindMembershipContextFactory :
        IDesignTimeDbContextFactory<NorthWindMembershipContext>
    {
        public NorthWindMembershipContext CreateDbContext(string[] args)
        {
            IOptions<MembershipDBOptions> DbOptions =
            Microsoft.Extensions.Options.Options.Create(
            new MembershipDBOptions()
            {
                ConnectionString =
            "Server=AlexAnder;Database=NorthWindUsersDB;Trusted_Connection=True;TrustServerCertificate=True;"
            });
            return new NorthWindMembershipContext(DbOptions);
        }
    }
}
