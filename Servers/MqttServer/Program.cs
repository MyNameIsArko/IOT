using System.Net;
using System.Security.Cryptography;
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
            var certificate = CreateSelfSignedCertificate("1.3.6.1.4.1.65432.8765.4321.555.444.333.999.777.888");

            services.AddHostedMqttServer(
                optionsBuilder =>
                {
                    optionsBuilder.WithDefaultEndpointPort(8883)
                        .WithEncryptionCertificate(certificate)
                        .WithEncryptedEndpoint()
                        .Build();
                });

            services.AddMqttConnectionHandler();
            services.AddConnections();

            services.AddSingleton<MqttController>();
        }
    }

    private static X509Certificate2 CreateSelfSignedCertificate(string oid)
    {
        var sanBuilder = new SubjectAlternativeNameBuilder();
        sanBuilder.AddIpAddress(IPAddress.Loopback);
        sanBuilder.AddIpAddress(IPAddress.IPv6Loopback);
        sanBuilder.AddDnsName("localhost");

        using var rsa = RSA.Create();
        var certRequest = new CertificateRequest("CN=localhost", rsa, HashAlgorithmName.SHA512, RSASignaturePadding.Pkcs1);

        certRequest.CertificateExtensions.Add(
            new X509KeyUsageExtension(X509KeyUsageFlags.DataEncipherment | X509KeyUsageFlags.KeyEncipherment | X509KeyUsageFlags.DigitalSignature, false));

        certRequest.CertificateExtensions.Add(new X509EnhancedKeyUsageExtension(new OidCollection { new(oid) }, false));

        certRequest.CertificateExtensions.Add(sanBuilder.Build());

        using var certificate = certRequest.CreateSelfSigned(DateTimeOffset.Now.AddMinutes(-10), DateTimeOffset.Now.AddYears(10));
        var pfxCertificate = new X509Certificate2(
            certificate.Export(X509ContentType.Pfx),
            (string)null!,
            X509KeyStorageFlags.MachineKeySet | X509KeyStorageFlags.Exportable);

        return pfxCertificate;
    }
}