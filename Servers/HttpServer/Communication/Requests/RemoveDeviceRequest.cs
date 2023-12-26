namespace HttpServer.Communication.Requests;

public class RemoveDeviceRequest
{
    public string Mac { get; set; }
    
    public override string ToString()
    {
        return $"RemoveDeviceRequest: Mac = {Mac}";
    }
}