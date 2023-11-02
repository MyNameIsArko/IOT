namespace HttpServer.Authentication;

public interface ITokenService
{
    string GenerateToken(string userId);

    string? GetUserId(string token);
}