namespace HttpServer.Communication.Requests;

public class LoginUserRequest
{
    public string UserName { get; set; }
    
    public string Password { get; set; }
}