namespace NorthWind.Sales.Frontend.BusinessObjects.Interfaces;

public interface IUsersGateway
{
    Task<IList<UserDto>> GetUsersAsync();
    Task<UserDto> GetUserByIdAsync(string id);
    Task<UserDto> CreateUserAsync(CreateUserDto userDto);
    Task UpdateUserAsync(string id, UpdateUserDto userDto);
    Task DeleteUserAsync(string id);
}

public record UserDto(string Id, string UserName, string Email, string? FirstName, string? LastName, IList<string> Roles);
public record CreateUserDto(string UserName, string Email, string Password, string? FirstName, string? LastName, IList<string> Roles);
public record UpdateUserDto(string UserName, string Email, string? FirstName, string? LastName, IList<string> Roles);
