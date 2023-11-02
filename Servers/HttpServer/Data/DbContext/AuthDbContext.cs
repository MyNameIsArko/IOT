namespace HttpServer.Data.DbContext;

using Configuration;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using Microsoft.AspNetCore.Identity;


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
            optionsBuilder.UseNpgsql(AppConfiguration.GetDatabaseOptions().AuthConnectionString);
        }
    }
    
    protected override void OnModelCreating(ModelBuilder builder)
    {
        base.OnModelCreating(builder);
        
        var userRoleId = "06c7e0c3-cecf-4429-afc9-593a27a6ff7f";
    
        var roles = new List<IdentityRole>
        {
            new IdentityRole
            {
                Name = "User",
                NormalizedName = "User",
                Id = userRoleId,
                ConcurrencyStamp = userRoleId
            },
        };
    
        builder.Entity<IdentityRole>().HasData(roles);
    }
}