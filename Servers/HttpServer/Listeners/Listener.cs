using System.Security.Cryptography.X509Certificates;
using HttpServer.Configuration;
using HttpServer.Data.Models;
using HttpServer.Repositories;
using MQTTnet;
using MQTTnet.Client;

namespace HttpServer.Listeners;

public class Listener
{
    private readonly ITopicDataRepository _topicDataRepository;

    private readonly IServiceScope _serviceScope;
    
    private readonly IListenersManager _listenersManager;

    private readonly MqttOptions _mqttOptions;

    public Device Device { get; set; }

    private readonly IMqttClient _client;

    public bool IsListening = true;

    public Listener(IServiceScope serviceScope, Device device, IListenersManager listenersManager)
    {
        _serviceScope = serviceScope;
        _listenersManager = listenersManager;
        _topicDataRepository = serviceScope.ServiceProvider.GetRequiredService<ITopicDataRepository>();
        _mqttOptions = AppConfiguration.GetMqttOptions();
        Device = device;

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
        return device.Id == Device.Id;
    }

    public void StartListening()
    { 
        Task.Run(
            async () =>
            {
                while (IsListening)
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
                        Console.WriteLine("Cannot connect mqtt client for listener: " + e.Message);
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
            Console.WriteLine($"Trying to connect client for device {Device.Mac} to server");
            
            string certFilePath = "/certificates/http.pfx";
            string password = "RVbySf#FV8*!xG4&o4j6";

            var certs = new List<X509Certificate2>
            {
                new (certFilePath, password)
            };
            
            var mqttClientOptions = new MqttClientOptionsBuilder()
                .WithClientId($"https-{Device.Mac}")
                .WithCredentials("devicePublisher", "RVbySf#FV8*!xG4&o4j6")
                .WithTcpServer(_mqttOptions.IpAddress, _mqttOptions.Port)
                .WithTlsOptions(o =>
                {
                    o.UseTls();
                    o.WithClientCertificates(certs);
                    o.WithCertificateValidationHandler(_ => true);
                })
                .WithCleanSession()
                .Build();
            
            await _client.ConnectAsync(mqttClientOptions);
            
            Console.WriteLine($"Client for device {Device.Mac} connected to server");
        }
        catch (Exception e)
        {
            Console.WriteLine($"Client for device {Device.Mac} could not be connected. Error message: {e.Message}");
            return;
        }

        foreach (var topic in Enum.GetValues<Topic>())
        {
            HandleReceivingMessages(topic);
        }
        HandleDisconnectMessage();
    }

    private async void HandleReceivingMessages(Topic topic)
    {
        Console.WriteLine($"Client for device {Device.Mac} is trying to subscribe to topic {topic}");
        
        var topicName = $"{Device.Mac}/{topic}";

        _client.ApplicationMessageReceivedAsync += delegate(MqttApplicationMessageReceivedEventArgs args)
        {
            var value = System.Text.Encoding.Default.GetString(args.ApplicationMessage.PayloadSegment);
            //var value = args.ApplicationMessage.ConvertPayloadToString();

            if (topicName != args.ApplicationMessage.Topic) return Task.CompletedTask;

            var topicData = new TopicData
            {
                Topic = topic,
                DeviceId = Device.Id,
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
    
    private async void HandleDisconnectMessage()
    {
        Console.WriteLine($"Client for device {Device.Mac} is trying to subscribe to topic {Device.Mac}/DisconnectUser");
        
        var topicName = $"{Device.Mac}/DisconnectUser";

        _client.ApplicationMessageReceivedAsync += delegate(MqttApplicationMessageReceivedEventArgs args)
        {
            if (topicName != args.ApplicationMessage.Topic) return Task.CompletedTask;
            Console.WriteLine($"Client for device {Device.Mac} received disconnect message");

            _listenersManager.RemoveListener(Device);
            
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

    public async void SendDisconnectMessage()
    {
        var topic = $"{Device.Mac}/Disconnect";
        var message = new MqttApplicationMessageBuilder()
            .WithTopic(topic)
            .WithPayload("The device has been removed")
            .Build();

        await _client.PublishAsync(message);
    }

    public async void CleanDisconnect()
    {
        IsListening = false;
        
        await _client.DisconnectAsync(
            new MqttClientDisconnectOptionsBuilder()
                .WithReason(MqttClientDisconnectOptionsReason.NormalDisconnection)
                .Build()
        );
        
        _serviceScope.Dispose();
    }
}