namespace HttpServer.Listeners;

using Data.Models;

public interface IListenersManager
{
    Task ConnectDevices();
    
    Task<bool> AddListenerToDevice(Device device);

    bool RemoveListener(Device device);
}