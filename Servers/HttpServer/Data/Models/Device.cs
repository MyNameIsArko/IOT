using System.ComponentModel.DataAnnotations;

namespace HttpServer.Data.Models;

public class Device
{
    [Key]
    public int Id { get; set; }

    public string Name { get; set; } = string.Empty;
        
    public string UserId { get; set; }

    public DateTime RegistrationDate { get; set; }
    
    public string Mac { get; set; }
    
    public string Key { get; set; }
    
    public string IV { get; set; }
    
    public ICollection<TopicData> TopicData { get; set; } = new List<TopicData>();
}