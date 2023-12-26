using HttpServer.Data.Models;
using HttpServer.Logger;
using HttpServer.Repositories;

namespace HttpServer.Listeners;

public class ListenersManager : IListenersManager
{
    private readonly IDeviceRepository _deviceRepository;
    
    private readonly IListenerFactory _listenerFactory;
    
    private readonly LoggerMock _logger;

    private static readonly List<Listener> Listeners = new();

    public ListenersManager(IDeviceRepository deviceRepository, IListenerFactory listenerFactory, LoggerMock logger)
    {
        _deviceRepository = deviceRepository;
        _listenerFactory = listenerFactory;
        _logger = logger;
    }

    public async Task ConnectDevices()
    {
        var devices = await _deviceRepository.GetDevices();
        
        foreach (var device in devices)
        {
            try
            {
                CreateListener(device);
            }
            catch (Exception e)
            { 
                _logger.WriteLogs("Cannot create listener: " + e.Message);
            }
        }
    }
    
    public async Task<bool> AddListenerToDevice(Device device)
    {
        if (!await _deviceRepository.DoesDeviceExist(device.DeviceId) || Listeners.Any(list => list.IsListeningToDevice(device)))
        {
            return false;
        }

        return CreateListener(device);
    }

    private bool CreateListener(Device device)
    {
        try
        {
            var listener = _listenerFactory.GetListener(device);
            listener.StartListening();
            Listeners.Add(listener);

            return true;
        }
        catch (Exception e)
        {
            _logger.WriteLogs("Cannot create listener: " + e.Message);
            return false;
        }
    }

    public bool RemoveListener(Device device)
    {
        var listener = Listeners.SingleOrDefault(listener => listener.IsListeningToDevice(device));

        if (listener is null) return true;

        try
        {
            listener.CleanDisconnect();
        }
        catch (Exception e)
        {
            _logger.WriteLogs("Cannot disconnect listener: " + e.Message);
            return false;
        }

        Listeners.Remove(listener);
        return true;
    }
}