using HttpServer.Data.Models;

namespace HttpServer.Listeners;

public class ListenerFactory : IListenerFactory
{
    private readonly IServiceProvider _serviceProvider;

    public ListenerFactory(IServiceProvider serviceProvider)
    {
        _serviceProvider = serviceProvider;
    }

    public Listener GetListener(Device device, IListenersManager listenersManager)
    {
        return new Listener(_serviceProvider.CreateScope(), device, listenersManager);
    }
}