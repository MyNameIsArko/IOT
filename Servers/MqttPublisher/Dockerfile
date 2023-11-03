FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY ["MqttPublisher/MqttPublisher.csproj", "MqttPublisher/"]
RUN dotnet restore "MqttPublisher/MqttPublisher.csproj"
COPY . .
WORKDIR "/src/MqttPublisher"
RUN dotnet build "MqttPublisher.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "MqttPublisher.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "MqttPublisher.dll"]
