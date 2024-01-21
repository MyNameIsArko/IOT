namespace HttpServer.Communication.Requests;

public class AddTokenRequest
{
    public string Value { get; set; }
    
    public string Key { get; set; } = string.Empty;

    public string IV { get; set; } = string.Empty;
    
    public override string ToString()
    {
        return $"AddTokenRequest: Value = {Value}";
    }
}