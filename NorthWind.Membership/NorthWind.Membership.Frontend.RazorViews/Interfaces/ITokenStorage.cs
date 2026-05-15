using NorthWind.Membership.Entities.UserLogin;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Frontend.RazorViews.Interfaces
{
    public interface ITokenStorage
    {
        Task<TokensDto> GetTokensAsync();
        Task StoreTokensAsync(TokensDto tokens);
        Task RemoveTokensAsync();
    }
}
