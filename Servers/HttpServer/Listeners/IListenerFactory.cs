using HttpServer.Data.Models;

namespace HttpServer.Listeners;

public interface IListenerFactory
{
    Listener GetListener(Device device, IListenersManager listenersManager);
}