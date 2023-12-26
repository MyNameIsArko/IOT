using HttpServer.Configuration;
using HttpServer.Data.Models;
using HttpServer.Logger;
using HttpServer.Repositories;
using MQTTnet;
using MQTTnet.Client;

namespace HttpServer.Listeners;

public class Listener
{
    private readonly ITopicDataRepository _topicDataRepository;

    private readonly IServiceScope _serviceScope;
    
    private readonly MqttOptions _mqttOptions;

    private readonly Device _device;
    
    private readonly LoggerMock _logger;

    private readonly IMqttClient _client;

    private bool _isListening = true;

    public Listener(IServiceScope serviceScope, Device device)
    {
        _serviceScope = serviceScope;
        _topicDataRepository = serviceScope.ServiceProvider.GetRequiredService<ITopicDataRepository>();
        _mqttOptions = AppConfiguration.GetMqttOptions();
        _device = device;
        _logger = serviceScope.ServiceProvider.GetRequiredService<LoggerMock>();

        try
        {
            _client = new MqttFactory().CreateMqttClient();
        }
        catch (Exception e)
        {
            throw new Exception("Could not create mqtt client", e);
        }
    }

    public bool IsListeningToDevice(Device device)
    {
        return device.DeviceId == _device.DeviceId;
    }

    public void StartListening()
    { 
        Task.Run(
            async () =>
            {
                while (_isListening)
                {
                    try
                    {
                        if (!await _client.TryPingAsync())
                        {
                            await ConnectClient();
                        }
                    }
                    catch(Exception e)
                    {
                        _logger.WriteLogs("Cannot connect mqtt client for listener: " + e.Message);
                    }
                    finally
                    {
                        await Task.Delay(TimeSpan.FromSeconds(10));
                    }
                }
            });
    }
    
    private async Task ConnectClient()
    {
        try
        {
            _logger.WriteLogs($"Trying to connect client for device {_device.Mac} to server");
            
            var mqttClientOptions = new MqttClientOptionsBuilder()
                .WithClientId($"C#Client-{_device.Mac}")
                .WithCredentials("devicePublisher", "RVbySf#FV8*!xG4&o4j6")
                .WithTcpServer(_mqttOptions.IpAddress, _mqttOptions.Port)
                .WithCleanSession()
                .WithRequestProblemInformation(false)
                .WithTryPrivate(false)
                .Build();
            
            await _client.ConnectAsync(mqttClientOptions);
            
            _logger.WriteLogs($"Client for device {_device.Mac} connected to server");
        }
        catch (Exception e)
        {
            _logger.WriteLogs($"Client for device {_device.Mac} could not be connected. Error message: {e.Message}");
            return;
        }

        foreach (var topic in Enum.GetValues<Topic>())
        {
            HandleReceivingMessages(topic);
        }
    }

    private async void HandleReceivingMessages(Topic topic)
    {
        _logger.WriteLogs($"Client for device {_device.Mac} is trying to subscribe to topic {topic}");
        
        var topicName = $"{_device.Mac}/{topic}";

        _client.ApplicationMessageReceivedAsync += delegate(MqttApplicationMessageReceivedEventArgs args)
        {
            var value = System.Text.Encoding.Default.GetString(args.ApplicationMessage.PayloadSegment);

            if (topicName != args.ApplicationMessage.Topic) return Task.CompletedTask;

            var topicData = new TopicData
            {
                Topic = topic,
                DeviceId = _device.DeviceId,
                Data = value,
                CreatedAt = DateTime.Now
            };
                
            _topicDataRepository.AddTopicData(topicData);
            
            return Task.CompletedTask;
        };

        var mqttSubscribeOptions = new MqttFactory().CreateSubscribeOptionsBuilder()
            .WithTopicFilter(
                f =>
                {
                    f.WithTopic(topicName);
                }
            )
            .Build();
        
        await _client.SubscribeAsync(mqttSubscribeOptions);
    }

    public async void CleanDisconnect()
    {
        _isListening = false;
        
        await _client.DisconnectAsync(
            new MqttClientDisconnectOptionsBuilder()
                .WithReason(MqttClientDisconnectOptionsReason.NormalDisconnection)
                .Build()
        );
        
        _serviceScope.Dispose();
    }
}