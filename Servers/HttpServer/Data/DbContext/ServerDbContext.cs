namespace HttpServer.Data.DbContext;

using Models;
using Microsoft.EntityFrameworkCore;

public class ServerDbContext : DbContext
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
}