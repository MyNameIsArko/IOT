namespace HttpServer.Repositories;

using Data.Models;

public interface IDeviceRepository
{
    Task<bool> AddDevice(Device device);
    
    Task<bool> UpdateDeviceName(int deviceId, string name);

    Task<bool> RemoveDevice(Device device);

    Task<Device?> GetDevice(string mac);

    Task<List<Device>> GetDevices();
    
    Task<List<Device>> GetUserDevices(string userId);

    Task<bool> DoesDeviceExist(int deviceId);
}