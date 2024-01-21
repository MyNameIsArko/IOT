using System.ComponentModel.DataAnnotations;

namespace HttpServer.Data.Models;

public class TopicData
{
    [Key]
    public int Id { get; set; }
    
    public Topic Topic { get; set; }
    
    public Device Device { get; set; } = null!;

    public int DeviceId { get; set; }

    public string Data { get; set; }
    
    public DateTime CreatedAt { get; set; }
}
