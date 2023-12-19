namespace HttpServer.Communication.Requests;

public class RegisterDeviceRequest
{
    public string UserId { get; set; }

    public string Mac { get; set; }
}