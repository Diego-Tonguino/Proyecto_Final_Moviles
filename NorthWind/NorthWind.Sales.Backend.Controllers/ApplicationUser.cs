using Microsoft.AspNetCore.Identity;

using Microsoft.AspNetCore.Identity;

namespace NorthWind.Sales.Backend.Controllers;

public class ApplicationUser : IdentityUser
{
    public string? FirstName { get; set; }
    public string? LastName { get; set; }
    public byte[]? ProfilePictureData { get; set; }
    public string? ProfilePictureContentType { get; set; }
}
