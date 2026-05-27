using Microsoft.EntityFrameworkCore;
using NorthWind.Sales.Backend.BusinessObjects.POCOEntities;
using NorthWind.Sales.Backend.Repositories.Entities;
using System.Reflection;

namespace NorthWind.Sales.Backend.DataContexts.EFCore.DataContexts;

//  Esta clase define el contexto de base de datos de Entity Framework Core.
//  Representa la unidad de trabajo (Unit of Work) y contiene los DbSet<T> que
//  representan las tablas de la base de datos.
internal class NorthWindContext : DbContext
{
  //  Configura SQL Server LocalDB como proveedor de base de datos.
  protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
  {
        //  Establece la cadena de conexión a una base de datos llamada NorthWindB.
        //optionsBuilder.UseSqlServer("Server=(localdb)\\SQLEXPRESS;Database=NorthWindDB");
        optionsBuilder.UseSqlServer(
        "Server=jhon\\MOVILES;Database=NorthWindDB;Trusted_Connection=True;TrustServerCertificate=True;");

  }

    //  Orders: representa la tabla de órdenes.
    public DbSet<Order> Orders { get; set; }

  //  OrderDetails: representa los detalles de una orden (líneas de productos, cantidad, precio, etc.).
  public DbSet<Repositories.Entities.OrderDetail> OrderDetails { get; set; }
    public DbSet<Customer> Customers { get; set; }
    public DbSet<Product> Products { get; set; }
    //  Busca automáticamente todas las clases que implementen IEntityTypeConfiguration<T> en el ensamblado
    //  actual y aplica sus configuraciones. Esto es: Orders y OrderDetails
    protected override void OnModelCreating(ModelBuilder modelBuilder)
  {
    modelBuilder.ApplyConfigurationsFromAssembly(Assembly.GetExecutingAssembly());
  }
}
