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
    
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<Device>()
            .HasMany(d => d.TopicData)
            .WithOne(td => td.Device)
            .HasForeignKey(td => td.DeviceId);
    }
    
    public async Task ConnectDatabase()
    {
        if (!await Database.CanConnectAsync())
        {
            Console.WriteLine("Database disconnected. Trying to reconnect...");
            await Database.OpenConnectionAsync();
        }
    }

    public DbSet<Device> Devices { get; set; }
    
    public DbSet<TopicData> TopicDatas { get; set; }
    
    public DbSet<Token> Tokens { get; set; }
}