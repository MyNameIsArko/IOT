namespace HttpServer.Communication.Responses;

public class DeviceResponse
{
    public string Mac { get; set; }

    public string Name { get; set; } = string.Empty;

    public string Temperature { get; set; } = string.Empty;
    
    public string Humidity { get; set; } = string.Empty;

    public DateTime LastTemperatureUpdate { get; set; } = DateTime.MinValue;
    
    public DateTime LastHumidityUpdate { get; set; } = DateTime.MinValue;
   
    public override string ToString()
    {
        return $"DeviceResponse: " +
               $"Mac = {Mac}, " +
               $"Name = {Name}, " +
               $"Temperature = {Temperature}, " +
               $"Humidity = {Humidity}, " +
               $"LastTemperatureUpdate = {LastTemperatureUpdate:yyyy-MM-dd HH:mm:ss}, " +
               $"LastHumidityUpdate = {LastHumidityUpdate:yyyy-MM-dd HH:mm:ss}";
    }
}