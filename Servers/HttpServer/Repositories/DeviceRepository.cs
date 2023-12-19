namespace HttpServer.Repositories;

using Data.DbContext;
using Data.Models;
using Microsoft.EntityFrameworkCore;

public class DeviceRepository : IDeviceRepository
{
    private readonly ServerDbContext _dbContext;

    public DeviceRepository(ServerDbContext dbContext)
    {
        _dbContext = dbContext;
    }
    
    public async Task<bool> AddDevice(Device device)
    {
        try
        {
            await _dbContext.Devices.AddAsync(device);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception)
        {
            return false;
        }
    }

    public async Task<bool> UpdateDeviceName(int deviceId, string name)
    {
        try
        {
            var device = _dbContext.Devices.SingleOrDefault(device => device.DeviceId == deviceId);
            device!.Name = name;
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception)
        {
            return false;
        }
    }

    public async Task<bool> RemoveDevice(Device device)
    {
        try
        {
            _dbContext.Devices.Remove(device);
            await _dbContext.SaveChangesAsync();
            return true;
        }
        catch (Exception)
        {
            return false;
        }
    }
    
    public async Task<Device?> GetDevice(string mac)
    {
        try
        {
            mac = mac.ToUpper();
            var device = await _dbContext.Devices.SingleOrDefaultAsync(d => d.Mac == mac);
            return device;
        }
        catch (Exception)
        {
            return null;
        }
    }

    public async Task<List<Device>> GetDevices()
    {
        try
        {
            var devices = await _dbContext.Devices.ToListAsync();
            return devices;
        }
        catch (Exception)
        {
            return new List<Device>();
        }
    }

    public async Task<List<Device>> GetUserDevices(string userId)
    {
        try
        {
            var devices = await _dbContext.Devices.Where(d => d.UserId.Equals(userId)).ToListAsync();
            return devices;
        }
        catch (Exception)
        {
            return new List<Device>();
        }
    }

    public async Task<bool> DoesDeviceExist(int deviceId)
    {
        try
        {
            var device = await _dbContext.Devices.SingleOrDefaultAsync(d => d.DeviceId == deviceId);
            return device is not null;
        }
        catch (Exception)
        {
            return false;
        }
    }
}