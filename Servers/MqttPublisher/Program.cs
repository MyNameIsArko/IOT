using MQTTnet;
using MQTTnet.Client;

namespace MqttPublisher;

public static class Program
{
    public static async Task Main(string[] args)
    {
        var client = await ConnectClient();

        client.ApplicationMessageReceivedAsync += delegate(MqttApplicationMessageReceivedEventArgs args)
        {
            Console.WriteLine(System.Text.Encoding.Default.GetString(args.ApplicationMessage.PayloadSegment));
            return Task.CompletedTask;
        };
        
        var mqttSubscribeOptions = new MqttFactory().CreateSubscribeOptionsBuilder()
            .WithTopicFilter(
                f =>
                {
                    f.WithTopic("DUPA/Disconnect");
                }
            )
            .Build();
        
        await client.SubscribeAsync(mqttSubscribeOptions);

        SendMessages(client);

        await CleanDisconnect(client);
    }

    private static async Task CleanDisconnect(IMqttClient mqttClient)
    {
        await mqttClient.DisconnectAsync(new MqttClientDisconnectOptionsBuilder()
            .WithReason(MqttClientDisconnectOptionsReason.NormalDisconnection).Build());
    }

    private static async Task<IMqttClient> ConnectClient()
    {
        var mqttFactory = new MqttFactory();

        var mqttClient = mqttFactory.CreateMqttClient();
        // Use builder classes where possible in this project.
        var mqttClientOptions = new MqttClientOptionsBuilder()
            .WithTcpServer("localhost", 1883)
            .WithCredentials("devicePublisher", "RVbySf#FV8*!xG4&o4j6")
            .WithClientId("client")
            .Build();
        

        // This will throw an exception if the server is not available.
        // The result from this message returns additional data which was sent 
        // from the server. Please refer to the MQTT protocol specification for details.
        var response = await mqttClient.ConnectAsync(mqttClientOptions, CancellationToken.None);
        //
        Console.WriteLine("The MQTT client is connected.");
        
        Console.Write(response.ResponseInformation);

        return mqttClient;
    }

    private static void SendMessages(IMqttClient mqttClient)
    {
        do
        {
            // Console.WriteLine("Write your message!");
            // var message = Console.ReadLine();
            //
            // Console.WriteLine("Now name topic:");
            // var topic = Console.ReadLine();
            //
            // var messageBytes = Encoding.UTF8.GetBytes(message!);
            // mqttClient.PublishBinaryAsync(topic, messageBytes);
            //
            // Console.WriteLine("Message sent. Continue or press Q to quit");
            //wait 5 sec
            Task.Delay(TimeSpan.FromSeconds(5)).Wait();
        } while (Console.ReadKey().Key != ConsoleKey.Q);
    }
}