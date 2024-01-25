using HttpServer.Data.Models;

namespace HttpServer.Listeners;

public interface IListenersManager
{
    Task ConnectDevices();
    
    Task<bool> AddListenerToDevice(Device device);

    Task<bool> RemoveListener(Device device);
}