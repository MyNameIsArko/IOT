using HttpServer.Data.Models;

namespace HttpServer.Listeners;

public interface IListenersManager
{
    Task ConnectDevices();
    
    Task<bool> AddListenerToDevice(Device device);

    bool RemoveListener(Device device);
}