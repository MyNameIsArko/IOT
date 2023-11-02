namespace HttpServer.Communication.Responses;

public class DeviceResponse
{
    public string Mac { get; set; }
    
    public string? Name { get; set; }
    
    public string? Temperature { get; set; }
    
    public string? Humidity { get; set; }
    
    public DateTime? LastTemperatureUpdate { get; set; }
    
    public DateTime? LastHumidityUpdate { get; set; }
    
}