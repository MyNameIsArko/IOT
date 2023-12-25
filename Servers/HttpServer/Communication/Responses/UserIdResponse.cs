namespace HttpServer.Communication.Responses;

public class UserIdResponse
{
    public string? UserId { get; set; }

    public UserIdResponse(string? userId) => UserId = userId;
}