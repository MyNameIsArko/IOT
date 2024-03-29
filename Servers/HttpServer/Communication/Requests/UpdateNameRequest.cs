namespace HttpServer.Communication.Requests;

public class UpdateNameRequest
{
    public string Mac { get; set; }
    
    public string Name { get; set; }
    
    public override string ToString()
    {
        return $"UpdateNameRequest: Mac = {Mac}, Name = {Name}";
    }
}