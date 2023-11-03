namespace HttpServer.Data.Models;

public class TopicData
{
    public int Id { get; set; }
    
    public Topic Topic { get; set; }

    public int DeviceId { get; set; }

    public string Data { get; set; }
    
    public DateTime CreatedAt { get; set; }
}
