namespace HttpServer.Data.Models;

public class Device
{
    public int DeviceId { get; set; }

    public string Name { get; set; } = string.Empty;
        
    public string UserId { get; set; }

    public DateTime RegistrationDate { get; set; }
    
    public string Mac { get; set; }
    
    public string Key { get; set; }
    
    public string IV { get; set; }
}