namespace HttpServer.Communication.Responses;

public class LoginResponse
{
    public string Token { get; set; }
    
    public List<DeviceKey> DevicesKeys { get; set; }

    public class DeviceKey
    {
        public string Mac { get; set; }
        
        public string Key { get; set; }
        
        public string IV { get; set; }
    }
}