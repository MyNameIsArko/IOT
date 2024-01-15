namespace HttpServer.Communication.Requests;

public class CheckDeviceRequest
{
    public string Mac { get; set; }
    
    public override string ToString()
    {
        return $"CheckDeviceRequest: Mac = {Mac}";
    }
}