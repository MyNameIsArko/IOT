FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build-env
WORKDIR /app
EXPOSE 80
EXPOSE 443
EXPOSE 1883

WORKDIR /app

COPY . ./
RUN dotnet restore MqttServer/MqttServer.csproj

COPY . ./ 
RUN dotnet publish -c Release -o out
 
FROM mcr.microsoft.com/dotnet/sdk:7.0 AS final-env
WORKDIR /app
COPY --from=build-env /app/out .
ENTRYPOINT ["dotnet", "MqttServer.dll"]