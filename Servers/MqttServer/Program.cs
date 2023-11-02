namespace MqttServer;

using System.Net;
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using MQTTnet.AspNetCore;

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
                            // This will allow MQTT connections based on TCP port 1883.
                            var adress = "192.168.1.18";
                            IPAddress.TryParse(adress, out var ipAddress);
                            o.Listen(ipAddress!, 1883, l => l.UseMqtt());
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
            services.AddHostedMqttServer(
                optionsBuilder =>
                {
                    optionsBuilder.WithDefaultEndpoint();
                });

            services.AddMqttConnectionHandler();
            services.AddConnections();

            services.AddSingleton<MqttController>();
        }
    }
}