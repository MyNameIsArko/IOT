namespace HttpServer.Repositories;

using Microsoft.EntityFrameworkCore;
using Data.DbContext;
using Data.Models;

public class TopicDataRepository : ITopicDataRepository
{
    private readonly ServerDbContext _dbContext;

    public TopicDataRepository(ServerDbContext dbContext)
    {
        _dbContext = dbContext;
    }

    public async Task<bool> AddTopicData(TopicData topicData)
    {
        try
        {
            await _dbContext.ConnectDatabase();
            await _dbContext.TopicDatas.AddAsync(topicData);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception)
        {
            return false;
        }
    }
    
    public async Task<TopicData?> GetLastDataUpdate(int deviceId, Topic topic)
    {
        try
        {
            await _dbContext.ConnectDatabase();
            var lastTopicData = await _dbContext.TopicDatas
                .Where(td => td.DeviceId == deviceId && td.Topic == topic)
                .OrderByDescending(t => t.CreatedAt)
                .FirstOrDefaultAsync();
            
            return lastTopicData;
        }
        catch (Exception)
        {
            return null;
        }
    }
}