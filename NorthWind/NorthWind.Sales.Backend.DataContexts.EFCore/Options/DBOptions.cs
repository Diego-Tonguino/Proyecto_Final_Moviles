namespace NorthWind.Sales.Backend.DataContexts.EFCore.Options;

//  Esta clase llamada DBOptions y es muy común en aplicaciones ASP.NET Core
//  que siguen buenas prácticas como Inyección de Dependencias, Clean Architecture
//  y configuración basada en el archivo appsettings.json.
public class DBOptions
{
  //  Esto declara una constante de solo lectura llamada SectionKey.
  //  guarda el nombre de la clase, es decir, "DBOptions".
  //  Se va a utilizar para leer una sección del archivo appsettings.json que tenga ese
  //  mismo nombre es decir el nombre de la sección debe ser "DBOptions".
  public const string SectionKey = nameof(DBOptions);

  //  Esta propiedad representa la cadena de conexión a la base de datos SQL Server.
  //  Se vincula automáticamente con la clave o sección "ConnectionString" del archivo
  //  appsettings.json usando mecanismos de configuración en ASP.NET Core esto es:
  //  (IOptions<DBOptions> o IConfiguration).
  public  string? ConnectionString { get; set; }
  public string? DomainLogsConnectionString { get; set; }
}

// ¿Por qué es útil en Clean Architecture?
//  Separa la configuración externa del código de infraestructura.
//  - Permite cambiar el string de conexión sin recompilar la app.
//  - Facilita las pruebas, la migración a puesta en producción y entornos múltiples
//    como (Desarrollo, QA, Producción).
//  - Sigue el principio Open/Closed(OCP) de SOLID: que establece que el código
//    está abierto para la extensión(cambiando el archivo.json) pero cerrado para
//    la modificación (no se tiene que reescribir la lógica ya creada).