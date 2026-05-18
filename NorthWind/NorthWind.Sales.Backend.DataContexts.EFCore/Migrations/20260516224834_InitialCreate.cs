using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

#pragma warning disable CA1814 // Prefer jagged arrays over multidimensional

namespace NorthWind.Sales.Backend.DataContexts.EFCore.Migrations
{
    /// <inheritdoc />
    public partial class InitialCreate : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "Customers",
                columns: table => new
                {
                    Id = table.Column<string>(type: "nchar(5)", fixedLength: true, maxLength: 5, nullable: false),
                    FirstName = table.Column<string>(type: "nvarchar(40)", maxLength: 40, nullable: false),
                    LastName = table.Column<string>(type: "nvarchar(40)", maxLength: 40, nullable: false),
                    CurrentBalance = table.Column<decimal>(type: "decimal(8,2)", precision: 8, scale: 2, nullable: false),
                    Email = table.Column<string>(type: "nvarchar(256)", maxLength: 256, nullable: false),
                    PasswordHash = table.Column<string>(type: "nvarchar(255)", maxLength: 255, nullable: false),
                    SecurityStamp = table.Column<string>(type: "nvarchar(128)", maxLength: 128, nullable: false),
                    City = table.Column<string>(type: "nvarchar(15)", maxLength: 15, nullable: true),
                    Phone = table.Column<string>(type: "nvarchar(24)", maxLength: 24, nullable: true),
                    ProfilePictureData = table.Column<byte[]>(type: "varbinary(max)", nullable: true),
                    ProfilePictureContentType = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: true),
                    Cedula = table.Column<string>(type: "nvarchar(10)", maxLength: 10, nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Customers", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Products",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Name = table.Column<string>(type: "nvarchar(40)", maxLength: 40, nullable: false),
                    UnitsInStock = table.Column<short>(type: "smallint", nullable: false),
                    UnitPrice = table.Column<decimal>(type: "decimal(8,2)", precision: 8, scale: 2, nullable: false),
                    ImageData = table.Column<byte[]>(type: "varbinary(max)", nullable: true),
                    ImageContentType = table.Column<string>(type: "nvarchar(100)", maxLength: 100, nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Products", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Orders",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    CustomerId = table.Column<string>(type: "nchar(5)", fixedLength: true, maxLength: 5, nullable: false),
                    ShipAddress = table.Column<string>(type: "nvarchar(60)", maxLength: 60, nullable: false),
                    ShipCity = table.Column<string>(type: "nvarchar(15)", maxLength: 15, nullable: false),
                    ShipCountry = table.Column<string>(type: "nvarchar(15)", maxLength: 15, nullable: false),
                    ShipPostalCode = table.Column<string>(type: "nvarchar(10)", maxLength: 10, nullable: false),
                    ShippingType = table.Column<int>(type: "int", nullable: false),
                    DiscountType = table.Column<int>(type: "int", nullable: false),
                    Discount = table.Column<double>(type: "float", nullable: false),
                    OrderDate = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Orders", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Orders_Customers_CustomerId",
                        column: x => x.CustomerId,
                        principalTable: "Customers",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "OrderDetails",
                columns: table => new
                {
                    OrderId = table.Column<int>(type: "int", nullable: false),
                    ProductId = table.Column<int>(type: "int", nullable: false),
                    UnitPrice = table.Column<decimal>(type: "decimal(8,2)", precision: 8, scale: 2, nullable: false),
                    Quantity = table.Column<short>(type: "smallint", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_OrderDetails", x => new { x.OrderId, x.ProductId });
                    table.ForeignKey(
                        name: "FK_OrderDetails_Orders_OrderId",
                        column: x => x.OrderId,
                        principalTable: "Orders",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_OrderDetails_Products_ProductId",
                        column: x => x.ProductId,
                        principalTable: "Products",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.InsertData(
                table: "Customers",
                columns: new[] { "Id", "Cedula", "City", "CurrentBalance", "Email", "FirstName", "LastName", "PasswordHash", "Phone", "ProfilePictureContentType", "ProfilePictureData", "SecurityStamp" },
                values: new object[,]
                {
                    { "ALFKI", "0102030405", "Berlin", 0m, "alfreds@mail.com", "Alfreds", "Futterkiste", "AQAAAAIAAYagAAAAEKz3F1o5Z2tVhZ4kF0K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1", "+49-30-007-1234", null, null, "ALFKISTAMP" },
                    { "ANATR", "0203040506", "México D.F.", 0m, "ana@mail.com", "Ana", "Trujillo", "AQAAAAIAAYagAAAAEKz3F1o5Z2tVhZ4kF0K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1", "+52-55-1234-5678", null, null, "ANATRSTAMP" },
                    { "ANTON", "0304050607", "México D.F.", 100m, "antonio@mail.com", "Antonio", "Moreno", "AQAAAAIAAYagAAAAEKz3F1o5Z2tVhZ4kF0K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1V2K1", "+52-55-8765-4321", null, null, "ANTONSTAMP" }
                });

            migrationBuilder.InsertData(
                table: "Products",
                columns: new[] { "Id", "ImageContentType", "ImageData", "Name", "UnitPrice", "UnitsInStock" },
                values: new object[,]
                {
                    { 1, null, null, "Chai", 35m, (short)20 },
                    { 2, null, null, "Chang", 55m, (short)0 },
                    { 3, null, null, "Aniseed Syrup", 65m, (short)20 },
                    { 4, null, null, "Chef Anton's Cajun Seasoning", 75m, (short)40 },
                    { 5, null, null, "Chef Anton's Gumbo Mix", 50m, (short)20 }
                });

            migrationBuilder.CreateIndex(
                name: "IX_Customers_Cedula",
                table: "Customers",
                column: "Cedula",
                unique: true);

            migrationBuilder.CreateIndex(
                name: "IX_OrderDetails_ProductId",
                table: "OrderDetails",
                column: "ProductId");

            migrationBuilder.CreateIndex(
                name: "IX_Orders_CustomerId",
                table: "Orders",
                column: "CustomerId");
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "OrderDetails");

            migrationBuilder.DropTable(
                name: "Orders");

            migrationBuilder.DropTable(
                name: "Products");

            migrationBuilder.DropTable(
                name: "Customers");
        }
    }
}
