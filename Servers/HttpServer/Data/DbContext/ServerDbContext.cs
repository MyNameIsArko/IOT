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

    public DbSet<Device> Devices { get; set; }
    
    public DbSet<TopicData> TopicDatas { get; set; }
    
    public DbSet<Token> Tokens { get; set; }
}