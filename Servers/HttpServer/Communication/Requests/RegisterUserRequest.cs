namespace HttpServer.Communication.Requests;

public class RegisterUserRequest
{
    public string UserName { get; set; }
    
    public string Password { get; set; }
}