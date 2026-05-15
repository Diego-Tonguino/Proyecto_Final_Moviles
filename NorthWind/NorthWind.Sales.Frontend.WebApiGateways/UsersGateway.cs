using NorthWind.Sales.Frontend.BusinessObjects.Interfaces;
using System.Net.Http.Json;

namespace NorthWind.Sales.Frontend.WebApiGateways;

internal class UsersGateway(HttpClient client) : IUsersGateway
{
    public async Task<IList<UserDto>> GetUsersAsync()
    {
        var response = await client.GetAsync("/api/users");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<IList<UserDto>>() ?? new List<UserDto>();
    }

    public async Task<UserDto> GetUserByIdAsync(string id)
    {
        var response = await client.GetAsync($"/api/users/{id}");
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<UserDto>() ?? throw new InvalidOperationException("User not found");
    }

    public async Task<UserDto> CreateUserAsync(CreateUserDto userDto)
    {
        var response = await client.PostAsJsonAsync("/api/users", userDto);
        response.EnsureSuccessStatusCode();
        return await response.Content.ReadFromJsonAsync<UserDto>() ?? throw new InvalidOperationException("Failed to create user");
    }

    public async Task UpdateUserAsync(string id, UpdateUserDto userDto)
    {
        var response = await client.PutAsJsonAsync($"/api/users/{id}", userDto);
        response.EnsureSuccessStatusCode();
    }

    public async Task DeleteUserAsync(string id)
    {
        var response = await client.DeleteAsync($"/api/users/{id}");
        response.EnsureSuccessStatusCode();
    }
}
