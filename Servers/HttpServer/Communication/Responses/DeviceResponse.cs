namespace HttpServer.Communication.Responses;

public class DeviceResponse
{
    public string Mac { get; set; }
    
    public string? Name { get; set; }
    
    public string? Temperature { get; set; }
    
    public string? Humidity { get; set; }
    
    public DateTime? LastTemperatureUpdate { get; set; }
    
    public DateTime? LastHumidityUpdate { get; set; }
   
    public override string ToString()
    {
        return $"DeviceResponse: " +
               $"Mac = {Mac}, " +
               $"Name = {Name ?? "N/A"}, " +
               $"Temperature = {Temperature ?? "N/A"}, " +
               $"Humidity = {Humidity ?? "N/A"}, " +
               $"LastTemperatureUpdate = {LastTemperatureUpdate?.ToString("yyyy-MM-dd HH:mm:ss") ?? "N/A"}, " +
               $"LastHumidityUpdate = {LastHumidityUpdate?.ToString("yyyy-MM-dd HH:mm:ss") ?? "N/A"}";
    }
}