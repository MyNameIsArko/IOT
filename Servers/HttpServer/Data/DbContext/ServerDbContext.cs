using HttpServer.Data.Models;
using Microsoft.EntityFrameworkCore;

namespace HttpServer.Data.DbContext;

public class ServerDbContext : Microsoft.EntityFrameworkCore.DbContext
{
    public ServerDbContext()
    {
    }
    
    public ServerDbContext(DbContextOptions<ServerDbContext> options) : base(options)
    {
        AppContext.SetSwitch("Npgsql.EnableLegacyTimestampBehavior", true);
        AppContext.SetSwitch("Npgsql.DisableDateTimeInfinityConversions", true);
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

    public DbSet<Device> Devices { get; set; }
    
    public DbSet<TopicData> TopicDatas { get; set; }
    
    public DbSet<Token> Tokens { get; set; }
}