using System.Text;
using MQTTnet;
using MQTTnet.Client;

namespace MqttPublisher;

public static class Program
{
    public static async Task Main(string[] args)
    {
        var client = await ConnectClient();

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
            .WithTcpServer("192.168.1.18", 1883)
            .WithClientId("Publisher")
            .Build();
        

        // This will throw an exception if the server is not available.
        // The result from this message returns additional data which was sent 
        // from the server. Please refer to the MQTT protocol specification for details.
        var response = await mqttClient.ConnectAsync(mqttClientOptions, CancellationToken.None);

        Console.WriteLine("The MQTT client is connected.");

        Console.Write(response.ResponseInformation);

        return mqttClient;
    }

    private static void SendMessages(IMqttClient mqttClient)
    {
        do
        {
            Console.WriteLine("Write your message!");
            var message = Console.ReadLine();

            Console.WriteLine("Now name topic:");
            var topic = Console.ReadLine();

            var messageBytes = Encoding.UTF8.GetBytes(message!);
            mqttClient.PublishBinaryAsync(topic, messageBytes);
            
            Console.WriteLine("Message sent. Continue or press Q to quit");
        } while (Console.ReadKey().Key != ConsoleKey.Q);
    }
}