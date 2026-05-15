using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.JsonWebTokens;
using Microsoft.IdentityModel.Tokens;
using NorthWind.Membership.Backend.Core.Dtos;
using NorthWind.Membership.Backend.Core.Options;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Claims;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Backend.Core.Services
{
    internal class JwtService(IOptions<JwtOptions> options)
    {
        //Método que obtega la informacipon necesaria para firmar el token
        SigningCredentials GetSigningCredentials()
        {
            var Key = Encoding.UTF8.GetBytes(
            options.Value.SecurityKey);
            var Secret = new SymmetricSecurityKey(Key);
            return new SigningCredentials(Secret,
            SecurityAlgorithms.HmacSha256);
        }
        //Metodo que devuelve la lsta de Claims con la información de usuario que se inluye en el token
        List<Claim> GetClaims(UserDto userDto) =>
            [
            new Claim(ClaimTypes.Name, userDto.Email),
            new Claim("FullName",
                $"{userDto.FirstName} {userDto.LastName}")
            ];
        // Método que devuelven la información que será utilizada para crear el token de seguridad
        SecurityTokenDescriptor GetTokenDescriptor(
            SigningCredentials signingCredentials,
            List<Claim> claims) =>
            new SecurityTokenDescriptor
            {
                Subject = new ClaimsIdentity(claims),
                Issuer = options.Value.ValidIssuer,
                Audience = options.Value.ValidAudience,
                Expires = DateTime.UtcNow.AddMinutes(
                    options.Value.ExpireInMinutes),
                SigningCredentials = signingCredentials
            };
        //Devuelve el token al usuario asociado 
        public string GetToken(UserDto userData)
        {
            var SigningCredentials = GetSigningCredentials();
            var Claims = GetClaims(userData);
            var TokenDescriptor = GetTokenDescriptor(SigningCredentials, Claims);
            var TokenHandler = new JsonWebTokenHandler();
            return TokenHandler.CreateToken(TokenDescriptor);
        }
    }
}
