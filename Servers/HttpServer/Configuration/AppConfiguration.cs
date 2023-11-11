namespace HttpServer.Configuration;

public static class AppConfiguration
{
    private const string ConfigurationFileName = "appsettings.json";
    
    private static readonly IConfiguration Configuration = new ConfigurationBuilder()
        .AddJsonFile(ConfigurationFileName, optional: false, reloadOnChange: true)
        .Build();

    public static JwtOptions GetJwtOptions()
    {
        return Get<JwtOptions>("Jwt");
    }
    
    public static ConnectionStrings GetConnectionStrings()
    {
        return Get<ConnectionStrings>("ConnectionStrings");
    }
    
    public static MqttOptions GetMqttOptions()
    {
        return Get<MqttOptions>("MqttOptions");
    }

    private static T Get<T>(string sectionName)
    {
        var settings = Configuration.GetSection(sectionName);
        return settings.Get<T>()!;
    }
}