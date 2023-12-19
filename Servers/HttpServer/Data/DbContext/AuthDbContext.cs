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
}