using NorthWind.Membership.Backend.AspNetIdentity.Entities;
using NorthWind.Membership.Backend.AspNetIdentity.Options;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Options;


namespace NorthWind.Membership.Backend.AspNetIdentity.DataContexts
{
    internal class NorthWindMembershipContext(
        IOptions<MembershipDBOptions> dbOptions)
        : IdentityDbContext<NorthWindUser>
    {
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseSqlServer(
            dbOptions.Value.ConnectionString);
            base.OnConfiguring(optionsBuilder);
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Seed default roles so migrations create them in AspNetRoles
            modelBuilder.Entity<IdentityRole>().HasData(
                new IdentityRole
                {
                    Id = "8EBAB1F7-0E0B-4E0B-B22E-5C3A2B49CE7B",
                    Name = "Vendedor",
                    NormalizedName = "VENDEDOR",
                    ConcurrencyStamp = "8EBAB1F7-0E0B-4E0B-B22E-5C3A2B49CE7B"
                },
                new IdentityRole
                {
                    Id = "B76F89AE-ECAB-4058-8A47-66F47805F027",
                    Name = "Administrador",
                    NormalizedName = "ADMINISTRADOR",
                    ConcurrencyStamp = "B76F89AE-ECAB-4058-8A47-66F47805F027"
                },
                new IdentityRole
                {
                    Id = "C2D4A9F8-9B3E-4F1A-A5C1-8E2B1D7F3A6C",
                    Name = "Cliente",
                    NormalizedName = "CLIENTE",
                    ConcurrencyStamp = "C2D4A9F8-9B3E-4F1A-A5C1-8E2B1D7F3A6C"
                }
            );
        }
    }
}
