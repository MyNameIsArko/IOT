namespace HttpServer.Authentication;

using System.Security.Claims;
using Microsoft.IdentityModel.Tokens;
using System.IdentityModel.Tokens.Jwt;
using System.Text;
using Configuration;

public class TokenService : ITokenService
{

    public string GenerateToken(string userId)
    {
        var securityKey = new SymmetricSecurityKey(Encoding.ASCII.GetBytes(AppConfiguration.GetJwtOptions().EncryptionKey));
        var credentials = new SigningCredentials(securityKey, SecurityAlgorithms.HmacSha512Signature);
        var expiry = DateTime.Now.AddMinutes(30);

        var userClaims = new List<Claim>
        {
            new (ClaimTypes.NameIdentifier, userId)
        };

        var securityToken = new JwtSecurityToken(
            claims: userClaims,
            notBefore: DateTime.Now,
            expires: expiry,
            signingCredentials: credentials);

        return new JwtSecurityTokenHandler().WriteToken(securityToken);
    }

    public string? GetUserId(string token)
    {
        var tokenHandler = new JwtSecurityTokenHandler();

        if (tokenHandler.ReadToken(token) is JwtSecurityToken jwtSecurityToken)
        {
            return jwtSecurityToken.Claims
                .FirstOrDefault(claim => claim.Type == ClaimTypes.NameIdentifier)?
                .Value;
        }

        return null;
    }
}