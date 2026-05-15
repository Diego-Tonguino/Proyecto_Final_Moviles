using Microsoft.EntityFrameworkCore; //IEntityTypeConfiguration
using Microsoft.EntityFrameworkCore.Metadata.Builders;
using NorthWind.Sales.Backend.Repositories.Entities; //EntityTypeBuilder

namespace NorthWind.Sales.Backend.DataContexts.EFCore.Configurations;

// Objetivo: configurar cómo se debe mapear la entidad "OrderDetail" a una tabla
// en la base de datos usando Entity Framework Core.
// OrderDetailConfiguration: Esta clase implementa IEntityTypeConfiguration<T>, una
// interfaz de EF Core que te permite configurar una entidad de manera modular y reutilizable.
// OrderDetail: es el POCO de la entidad que representa los detalles de una orden (detalles del pedido).
internal class OrderDetailConfiguration
    : IEntityTypeConfiguration<Repositories.Entities.OrderDetail>
{
  // Este método es requerido por la interfaz y se llama automáticamente cuando EF Core construye el modelo.
  // Aquí se definen las reglas de mapeo de la entidad a la base de datos.
  public void Configure(EntityTypeBuilder<Repositories.Entities.OrderDetail> builder)
  {
    //  Define una clave primaria compuesta usando los campos OrderId y ProductId.
    //  Esto es común en relaciones de muchos-a-muchos (una orden tiene muchos productos y un producto
    //  puede estar en muchas órdenes).
    //  EF Core generará una clave compuesta en la tabla correspondiente.
    builder.HasKey(d => new { d.OrderId, d.ProductId });
    //  UnitPrice: Indica que la columna UnitPrice tendrá una precisión de 8 dígitos en total, con 2 decimales.
    builder.Property(d => d.UnitPrice).HasPrecision(8, 2);

     builder.HasOne<Product>()
    .WithMany()
    .HasForeignKey(p => p.ProductId);
    }
}

//  ¿Por qué esto es Clean Architecture?
//  - No hay lógica de dominio aquí: Solo se define cómo se mapea al almacenamiento físico.
//  - No contamina el modelo de dominio con atributos de EF Core(como[Key], [Column]).
//  - Se puede sustituir fácilmente EF Core por otro ORM sin tocar la lógica de negocio.
//  - SRP: esta clase solo mapea una entidad.
