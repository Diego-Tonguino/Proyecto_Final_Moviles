using Microsoft.EntityFrameworkCore; //  DbContext, DbSet, ModelBuilder
using Microsoft.Extensions.Options; // IOptions
using NorthWind.Sales.Backend.BusinessObjects.POCOEntities; // Order
using NorthWind.Sales.Backend.DataContexts.EFCore.Options; //  DBOptions
using NorthWind.Sales.Backend.Repositories.Entities;
using System.Reflection; //  Assembly

namespace NorthWind.Sales.Backend.DataContexts.EFCore.DataContexts;

//  Esta clase hereda de DbContext y representa la conexión con la base de datos
//  a través de Entity Framework Core (EF Core).
//  Es parte de la capa Frameworks and Drivers, donde se conecta tu aplicación con tecnologías
//  externas como las bases de datos.

//  Usa constructor simplificado (primary constructor) (C# 12) para inyectar IOptions<DBOptions>, que contiene
//  la cadena de conexión (ConnectionString).
//  Esto desacopla la configuración de la capa de infraestructura del código fuente (principio DIP del SOLID).
internal class NorthWindSalesContext(IOptions<DBOptions> dbOptions) : DbContext
{
  //  Esta propiedad permite que EF Core cree la tabla basada en la clase Order.
  public DbSet<Order> Orders { get; set; }
    public DbSet<Customer> Customers { get; set; }
    public DbSet<Product> Products { get; set; }

  //  Esta propiedad permite que EF Core cree la tabla basada en la clase OrderDetail.
  public DbSet<Repositories.Entities.OrderDetail> OrderDetails { get; set; }

  //  Configuración de la cadena de conexión.
  //  Usa la cadena de conexión inyectada desde DBOptions (normalmente cargada desde appsettings.json).
  protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
  {
    //  UseSqlServer: es el método de EF Core para configurar SQL Server como proveedor de base de datos.
    optionsBuilder.UseSqlServer(dbOptions.Value.ConnectionString);
  }

  //  Aplicación de configuraciones de las entidades (Order y OrderDetail).
  protected override void OnModelCreating(ModelBuilder modelBuilder)
  {
    //  Busca automáticamente todas las clases que implementan IEntityTypeConfiguration<T> en el ensamblado o
    //  proyecto actual y aplica sus configuraciones.
    //  Esto permite una organización limpia y modular del mapeo entre entidades y tablas (que se encuentran en la
    //  carpeta Configurations).
    modelBuilder.ApplyConfigurationsFromAssembly(Assembly.GetExecutingAssembly());
  }
}
