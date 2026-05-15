using Microsoft.AspNetCore.Identity;


namespace NorthWind.Membership.Backend.AspNetIdentity.Entities
{
    internal class NorthWindUser : IdentityUser
    {
        public string? FirstName { get; set; }
        public string? LastName { get; set; }
        // Added fields to match database columns
        public byte[]? ProfilePictureData { get; set; }
        public string? ProfilePictureContentType { get; set; }
    }
}
