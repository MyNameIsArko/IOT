using System.Security.Cryptography.X509Certificates;
using MQTTnet.AspNetCore;

namespace MqttServer;

public static class Program
{
    public static Task Main()
    {
        var host = Host.CreateDefaultBuilder(Array.Empty<string>())
            .ConfigureWebHostDefaults(
                webBuilder =>
                {
                    webBuilder.UseKestrel(
                        o =>
                        {
                            // This will allow MQTT connections based on TCP port 8883.;
                            o.ListenAnyIP(8883, l => l.UseMqtt());
                        });

                    webBuilder.UseStartup<Startup>();
                });
        
        return host.RunConsoleAsync();
    }

    sealed class Startup
    {
        public void Configure(IApplicationBuilder app, IWebHostEnvironment environment, MqttController mqttController)
        {
            app.UseRouting();

            app.UseMqttServer(
                server =>
                {
                    server.ValidatingConnectionAsync += mqttController.ValidateConnection;
                    server.ClientConnectedAsync += mqttController.OnClientConnected;
                    server.ClientDisconnectedAsync += mqttController.OnClientDisconnected;
                    server.ClientSubscribedTopicAsync += mqttController.OnTopicSubscribed;
                });

        }

        public void ConfigureServices(IServiceCollection services)
        {
            string certFilePath = "Certificates/server.crt";
            string password = "RVbySf#FV8*!xG4&o4j6";

            var certificate = new X509Certificate2(certFilePath, password);

            services.AddHostedMqttServer(
                optionsBuilder =>
                {
                    optionsBuilder
                        .WithDefaultEndpointPort(8883)
                        .WithEncryptionCertificate(certificate)
                        .WithEncryptedEndpoint()
                        .WithEncryptionSslProtocol(System.Security.Authentication.SslProtocols.Tls12)
                        .WithClientCertificate()
                        .Build();
                });

            services.AddMqttConnectionHandler();
            services.AddConnections();

            services.AddSingleton<MqttController>();
        }
    }
}