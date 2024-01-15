using HttpServer.Configuration;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;

namespace HttpServer.Data.DbContext;

public class AuthDbContext : IdentityDbContext
{
    public AuthDbContext()
    {
    }
    
    public AuthDbContext(DbContextOptions<AuthDbContext> options) : base(options)
    {
    }
    
    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        if (!optionsBuilder.IsConfigured)
        {
            optionsBuilder.UseNpgsql(AppConfiguration.GetConnectionStrings().AuthConnectionString);
        }
    }
    
    public async void CheckDatabaseConnection()
    {
        while (true)
        {
            await Task.Delay(1000);
            if (!await Database.CanConnectAsync())
            {
                try
                {
                    Console.WriteLine("Database disconnected. Trying to reconnect...");
                    await Database.OpenConnectionAsync();
                }
                catch (Exception)
                {
                    Console.WriteLine("Cannot connect to DB");
                    // ignored
                }
            }
        }
    }
}