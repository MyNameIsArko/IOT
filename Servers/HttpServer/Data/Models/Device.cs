namespace HttpServer.Data.Models;

public class Device
{
    public int DeviceId { get; set; }
    
    public string? Name { get; set; }
        
    public string UserId { get; set; }

    public DateTime RegistrationDate { get; set; }
    
    public string Mac { get; set; }
}