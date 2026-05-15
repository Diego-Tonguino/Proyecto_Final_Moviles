using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace NorthWind.Membership.Entities.UserLogin
{
    public class TokensDto(string accessToken)
    {
        public string AccessToken => accessToken;
    }
}
