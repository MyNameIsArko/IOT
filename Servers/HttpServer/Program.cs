using System.Text;
using HttpServer.Authentication;
using HttpServer.Configuration;
using HttpServer.Data.DbContext;
using HttpServer.Listeners;
using HttpServer.Repositories;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Identity;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<ServerDbContext>(options =>
    options.UseNpgsql(AppConfiguration.GetDatabaseOptions().ServerConnectionString));
builder.Services.AddDbContext<AuthDbContext>(options =>
    options.UseNpgsql(AppConfiguration.GetDatabaseOptions().AuthConnectionString));

builder.Services.AddIdentity<IdentityUser, IdentityRole>()
    .AddEntityFrameworkStores<AuthDbContext>();

builder.Services.ConfigureApplicationCookie(options =>
{
    options.Events.OnRedirectToLogin = context =>
    {
        context.Response.StatusCode = 401;
        return Task.CompletedTask;
    };
});

builder.Services.AddScoped<ITokenService, TokenService>();
builder.Services.AddScoped<IDeviceRepository, DeviceRepository>();
builder.Services.AddScoped<ITopicDataRepository, TopicDataRepository>();
builder.Services.AddScoped<IListenersManager, ListenersManager>();

var signingKey = Encoding.UTF8.GetBytes(AppConfiguration.GetJwtOptions().EncryptionKey);
builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ClockSkew = TimeSpan.Zero,
            IssuerSigningKey = new SymmetricSecurityKey(signingKey),
            ValidateIssuerSigningKey = true,
            ValidateIssuer = false,
            ValidateAudience = false,
            RequireExpirationTime = true,
            RequireSignedTokens = true,
            ValidateLifetime = true
        };

        options.SaveToken = true;
    });

var app = builder.Build();

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthentication();
app.UseAuthorization();

using (var serviceScope = app.Services.CreateScope())
{
    var listenersManager = serviceScope.ServiceProvider.GetRequiredService<IListenersManager>();
    await listenersManager.ConnectDevices();
}

app.MapControllers();

app.Run();
