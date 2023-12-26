namespace HttpServer.Communication.Requests;

public class AddTokenRequest
{
    public string Value { get; set; }
    
    public override string ToString()
    {
        return $"AddTokenRequest: Value = {Value}";
    }
}