namespace HttpServer.Communication.Requests;

public class RegisterDeviceRequest
{
    public string UserId { get; set; }

    public string Mac { get; set; }
    
    public override string ToString()
    {
        return $"RegisterDeviceRequest: UserId = {UserId}, Mac = {Mac}";
    }
}