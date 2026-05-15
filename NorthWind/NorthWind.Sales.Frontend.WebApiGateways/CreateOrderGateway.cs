using NorthWind.Sales.Entities.Dtos.CreateOrder;
using NorthWind.Sales.Entities.ValueObjects;
using NorthWind.Sales.Frontend.BusinessObjects.Interfaces;
using System.Net.Http.Json;
using static System.Net.WebRequestMethods;

//  Indica que la clase forma parte del gateway de Web API del frontend del proyecto NorthWind.
namespace NorthWind.Sales.Frontend.WebApiGateways;

//  Implementa la interfaz "ICreateOrderGateway", que define el contrato para crear órdenes.
//  Recibe por inyección de dependencias un HttpClient (Que se puede configurar en el archivo Program.cs).
//  Este código implementa un patrón "API Gateway" del lado del cliente.
//  Encapsula la comunicación con una Web API RESTful para crear órdenes de compra.
//  Así, otras partes del frontend (como servicios o componentes Blazor, por ejemplo)
//  pueden utilizar esta clase sin preocuparse por los detalles del protocolo HTTP.
internal class CreateOrderGateway(HttpClient client) : ICreateOrderGateway
{
    //  Enviar una solicitud POST a un endpoint HTTP (representado por Endpoints.CreateOrder) con los
    //  datos de la orden, y devolver el ID de la orden creada.
    public async Task<int> CreateOrderAsync(CreateOrderDto order)
    {
        var Response = await client.PostAsJsonAsync(
        Endpoints.CreateOrder, order);
        return await Response.Content.ReadFromJsonAsync<int>();
    }
}

//  Dependencias clave:
//  "HttpClient": Permite enviar solicitudes HTTP.
//  "PostAsJsonAsync": "Serializa" un objeto como "JSON" y lo envía como contenido de una solicitud "POST".
//  "ReadFromJsonAsync<T>()": "Deserializa" el contenido de la respuesta "HTTP" a un tipo específico (int en este caso).
//  "CreateOrderDto": DTO con los datos de la orden.
//  "Endpoints.CreateOrder": Se espera que sea una constante o propiedad estática con la URL
//  del endpoint(por ejemplo, "/api/orders"). En el código tenemos: $"/{nameof(CreateOrder)}";.