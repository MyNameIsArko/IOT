namespace HttpServer.Data.Models;

public class Token
{
    public int Id { get; set; }
    
    public string Value { get; set; } = string.Empty;

    public string UserId { get; set; } = string.Empty;

    public string Key { get; set; } = string.Empty;

    public string IV { get; set; } = string.Empty;
}