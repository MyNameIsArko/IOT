using HttpServer.Data.Models;

namespace HttpServer.Repositories;

public interface ITokenRepository
{
    Task<bool> AddToken(Token token);
    
    Task<bool> DoesTokenExist(string value, string userId);

    Task<bool> RemoveToken(string value);
}