namespace HttpServer.Listeners;

using Repositories;
using Data.Models;

public class ListenersManager : IListenersManager
{
    private readonly ITopicDataRepository _topicDataRepository;

    private readonly IDeviceRepository _deviceRepository;

    private static readonly List<Listener> Listeners = new();

    private readonly IServiceProvider _serviceProvider;

    public ListenersManager(ITopicDataRepository topicDataRepository, IDeviceRepository deviceRepository, IServiceProvider serviceProvider)
    {
        _topicDataRepository = topicDataRepository;
        _deviceRepository = deviceRepository;
        _serviceProvider = serviceProvider;
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
                Console.WriteLine(e);
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
            var listener = new Listener(_serviceProvider.CreateScope(), device);
            listener.StartListening();
            Listeners.Add(listener);

            return true;
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
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
        catch (Exception)
        {
            return false;
        }

        Listeners.Remove(listener);
        return true;
    }
}