using Microsoft.EntityFrameworkCore;//IEntityTypeConfiguration
using Microsoft.EntityFrameworkCore.Metadata.Builders;//EntityTypeBuilder
using NorthWind.Sales.Backend.BusinessObjects.POCOEntities;
using NorthWind.Sales.Backend.Repositories.Entities;//Order

namespace NorthWind.Sales.Backend.DataContexts.EFCore.Configurations;

//  Objetivo de la clase: Definir cómo se mapea la entidad "Order" a una tabla a la base de datos (ORM mapping).
//  OrderConfiguration: Implementa la interfaz "IEntityTypeConfiguration<Order>" que EF Core utiliza
//  para configurar entidades de forma modular y limpia. Esto evita meter la configuración en el
//  propio "DbContext", manteniéndolo limpio y separado por entidad.
//  Order: es la entidad del dominio (POCO) que se desea mapear a la base de datos.
internal class OrderConfiguration : IEntityTypeConfiguration<Order>
{
  //  builder: permite especificar las reglas de mapeo entre las propiedades del objeto "Order" y
  //  las columnas de la tabla en la base de datos.
  public void Configure(EntityTypeBuilder<Order> builder)
  {
    //  Reglas de configuración:
    //  CustomerId: es obligatorio, con una longitud fija de 5 caracteres.
    builder.Property(o => o.CustomerId).IsRequired().HasMaxLength(5).IsFixedLength();
    //  ShipAddress: es obligatorio y puede tener hasta 60 caracteres.
    builder.Property(o => o.ShipAddress).IsRequired().HasMaxLength(60);
    //  ShipCity: Es opcional (no IsRequired()) y con longitud fija de 15 caracteres.
    builder.Property(o => o.ShipCity).HasMaxLength(15);
    //  ShipCountry: Es opcional (no IsRequired()) y con longitud fija de 15 caracteres.
    builder.Property(o => o.ShipCountry).HasMaxLength(15);
    //  ShipPostalCode: Es opcional (no IsRequired()) y con longitud fija de 10 caracteres.
    builder.Property(o => o.ShipPostalCode).HasMaxLength(10);

        builder.HasOne<Customer>()
.WithMany()
.HasForeignKey(o => o.CustomerId);
    }
}
