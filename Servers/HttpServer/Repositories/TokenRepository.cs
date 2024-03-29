using HttpServer.Data.DbContext;
using HttpServer.Data.Models;
using Microsoft.EntityFrameworkCore;

namespace HttpServer.Repositories;

public class TokenRepository : ITokenRepository
{
    private readonly ServerDbContext _dbContext;

    public TokenRepository(ServerDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<bool> AddToken(Token token)
    {
        try
        {
            await _dbContext.ConnectDatabase();
            await _dbContext.Tokens.AddAsync(token);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception)
        {
            return false;
        }
    }

    public async Task<bool> DoesTokenExist(string value, string userId)
    {
        try
        {
            await _dbContext.ConnectDatabase();
            return await _dbContext.Tokens.AnyAsync(t => t.Value == value && t.UserId == userId);
        }
        catch (Exception)
        {
            return false;
        }
    }

    public async Task<Token?> RemoveToken(string value)
    {
        try
        {
            await _dbContext.ConnectDatabase();
            var token = await _dbContext.Tokens.FirstOrDefaultAsync(token => token.Value == value);
            if (token is null) return null;
            _dbContext.Tokens.Remove(token);
            await _dbContext.SaveChangesAsync();
            return token;
        }
        catch (Exception)
        {
            return null;
        }
    }
}