using MQTTnet.Server;

namespace MqttServer;

public class MqttController
{
    public Task OnClientConnected(ClientConnectedEventArgs eventArgs)
    {
        Console.WriteLine($"Client '{eventArgs.ClientId}' connected.");
        return Task.CompletedTask;
    }
        
    public Task OnClientDisconnected(ClientDisconnectedEventArgs eventArgs)
    {
        Console.WriteLine($"Client '{eventArgs.ClientId}' disconnected.");
        return Task.CompletedTask;
    }
    
    public Task OnTopicSubscribed(ClientSubscribedTopicEventArgs eventArgs)
    {
        Console.WriteLine($"Client '{eventArgs.ClientId}' subscribed topic {eventArgs.TopicFilter.Topic}.");
        return Task.CompletedTask;
    }


    public Task ValidateConnection(ValidatingConnectionEventArgs eventArgs)
    {
        Console.WriteLine($"Client '{eventArgs.ClientId}' wants to connect");

        if (eventArgs.UserName == "devicePublisher" && eventArgs.Password == "RVbySf#FV8*!xG4&o4j6")
        {
            return Task.CompletedTask;
        }

        eventArgs.ReasonCode = MQTTnet.Protocol.MqttConnectReasonCode.NotAuthorized;
        return Task.FromException(new Exception("User not authorized"));
    }
}