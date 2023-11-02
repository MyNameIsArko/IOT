FROM mcr.microsoft.com/dotnet/aspnet:7.0 AS base
WORKDIR /app
EXPOSE 80
EXPOSE 443

FROM mcr.microsoft.com/dotnet/sdk:7.0 AS build
WORKDIR /src
COPY ["HttpServer/HttpServer.csproj", "HttpServer/"]
RUN dotnet restore "HttpServer/HttpServer.csproj"
COPY . .
WORKDIR "/src/HttpServer"
RUN dotnet build "HttpServer.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "HttpServer.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "HttpServer.dll"]
