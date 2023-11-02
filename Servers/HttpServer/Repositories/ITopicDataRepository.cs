namespace HttpServer.Repositories;

using Data.Models;

public interface ITopicDataRepository
{
    Task<bool> AddTopicData(TopicData topicData);

    Task<TopicData?> GetLastDataUpdate(int deviceId, Topic topic);
}