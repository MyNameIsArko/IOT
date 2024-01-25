using HttpServer.Authentication;
using HttpServer.Communication.Requests;
using HttpServer.Communication.Responses;
using HttpServer.Data.DbContext;
using HttpServer.Data.Models;
using HttpServer.Listeners;
using HttpServer.Repositories;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;

namespace HttpServer.Controllers;

[ApiController]
[Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
[Route("api/[controller]")]
public class DeviceController : Controller
{
    private readonly UserManager<IdentityUser> _userManager;
    
    private readonly IDeviceRepository _deviceRepository;
    
    private readonly ITopicDataRepository _topicDataRepository;
    
    private readonly ITokenRepository _tokenRepository;

    private readonly IListenersManager _listenersManager;
    
    private readonly AuthDbContext _dbContext;

    private readonly ITokenService _tokenService;

    public DeviceController(UserManager<IdentityUser> userManager, IDeviceRepository deviceRepository,
        ITopicDataRepository topicDataRepository, ITokenRepository tokenRepository, ITokenService tokenService,
        IListenersManager listenersManager, AuthDbContext dbContext)
    {
        _userManager = userManager;
        _deviceRepository = deviceRepository;
        _topicDataRepository = topicDataRepository;
        _tokenRepository = tokenRepository;
        _tokenService = tokenService;
        _listenersManager = listenersManager;
        _dbContext = dbContext;
    }
    
    [AllowAnonymous]
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterDeviceRequest request)
    {
        Console.WriteLine("Register device request received: " + request);
        
        if (!HttpContext.Request.Headers.TryGetValue("Authorization", out var authHeader) || !authHeader.ToString().StartsWith("Bearer "))
        {
            Console.WriteLine("Sending response: " + "No token provided");
            return Unauthorized("No token provided");
        }

        var token = authHeader.ToString()["Bearer ".Length..].Trim();

        if (!await _tokenRepository.DoesTokenExist(token, request.UserId) || await _tokenRepository.RemoveToken(token) is not { } deviceToken)
        {
            Console.WriteLine("Sending response: " + "Unknown token");
            return Unauthorized("Unknown token");
        }

        if (await _deviceRepository.GetDevice(request.Mac) is { } deviceEntity)
        {
            var result = await RemoveDevice(deviceEntity);
            if (!result)
            {
                Console.WriteLine("Sending response: " + "Device is already registered and could not be removed");
                return BadRequest("Device is already registered and could not be removed");
            }
        }
        
        var device = new Device
        {
            Mac = request.Mac.ToUpper(),
            UserId = request.UserId,
            RegistrationDate = DateTime.Now,
            Key = deviceToken.Key,
            IV = deviceToken.IV
        };

        var registrationResult = await _deviceRepository.AddDevice(device);

        if (!registrationResult)
        {
            Console.WriteLine("Sending response: " + "The device could not be registered");
            return BadRequest("The device could not be registered");
        }
        
        var listenerAdded = await _listenersManager.AddListenerToDevice(device);

        if (!listenerAdded)
        {
            Console.WriteLine("Sending response: " + "The device could not be registered (mqtt connection problem)");
            return BadRequest("The device could not be registered (mqtt connection problem)");
        }
        
        Console.WriteLine("Sending response: " + "Device successfully registered");
        return Ok("Device successfully registered");
    }

    [HttpPut("name")]
    public async Task<IActionResult> UpdateName([FromBody] UpdateNameRequest request)
    {
        Console.WriteLine("Update name request received: " + request);
        
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            Console.WriteLine("Sending response: " + "User does not exist");
            return Unauthorized(new MessageResponse("User does not exist"));
        }
        
        var device = await _deviceRepository.GetDevice(request.Mac);
        if (device is null)
        {
            Console.WriteLine("Sending response: " + "The device does not exist");
            return BadRequest(new MessageResponse("The device does not exist"));
        }

        if (!device.UserId.Equals(user.Id))
        {
            Console.WriteLine("Sending response: " + $"User: {user.UserName} is not the owner of device with mac: {request.Mac}");
            return Unauthorized(new MessageResponse($"User: {user.UserName} is not the owner of device with mac: {request.Mac}"));
        }

        var result = await _deviceRepository.UpdateDeviceName(device.Id, request.Name);
        if (!result)
        {
            Console.WriteLine("Sending response: " + "Could not change device name");
            return BadRequest(new MessageResponse("Could not change device name"));
        }

        Console.WriteLine("Sending response: " + "Device name was successfully changed");
        return Ok(new MessageResponse("Device name was successfully changed"));
    }
    
    [HttpDelete("remove")]
    public async Task<IActionResult> Remove([FromBody] RemoveDeviceRequest request)
    {
        Console.WriteLine("Remove request received: " + request);
        
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            Console.WriteLine("Sending response: " + "User does not exist");
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        var device = await _deviceRepository.GetDevice(request.Mac);
        if (device is null)
        {
            Console.WriteLine("Sending response: " + "The device does not exist");
            return BadRequest(new MessageResponse("The device does not exist"));
        }

        if (!device.UserId.Equals(user.Id))
        {
            Console.WriteLine("Sending response: " + $"User: {user.UserName} is not the owner of device with mac: {request.Mac}");
            return Unauthorized(new MessageResponse($"User: {user.UserName} is not the owner of device with mac: {request.Mac}"));
        }

        var result = await RemoveDevice(device);
        if (!result)
        {
            Console.WriteLine("Sending response: " + "Device could not be removed");
            return BadRequest(new MessageResponse("Device could not be removed"));
        }

        Console.WriteLine("Sending response: " + "Device successfully removed");
        return Ok(new MessageResponse("Device successfully removed"));
    }
    
    [HttpGet("list")]
    public async Task<ActionResult<IEnumerable<Device>>> ListDevices()
    {
        Console.WriteLine("List request received");
        
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            Console.WriteLine("Sending response: " + "User does not exist");
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        var devices = await _deviceRepository.GetUserDevices(user.Id);
        
        if (devices.IsNullOrEmpty())
        {
            Console.WriteLine("Sending response: " + "User does not have any devices");
            return Ok(new DeviceListResponse(new List<DeviceResponse>()));
        }

        List<DeviceResponse> response = new();
        foreach (var device in devices)
        {
            var temperature = await _topicDataRepository.GetLastDataUpdate(device.Id, Topic.Temperature);
            var humidity = await _topicDataRepository.GetLastDataUpdate(device.Id, Topic.Humidity);
            response.Add(new DeviceResponse
            {
                Mac = device.Mac,
                Name = device.Name,
                Temperature = temperature?.Data ?? string.Empty,
                Humidity = humidity?.Data ?? string.Empty,
                LastTemperatureUpdate = temperature?.CreatedAt ?? DateTime.MinValue,
                LastHumidityUpdate = humidity?.CreatedAt ?? DateTime.MinValue
            });
        }

        Console.WriteLine("Sending response: " + response);
        return Ok(new DeviceListResponse(response));
    }

    [HttpPost("token")]
    public async Task<ActionResult<string>> AddToken([FromBody] AddTokenRequest request)
    {
        Console.WriteLine("Token request received: " + request);
        
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            Console.WriteLine("Sending response: " + "User does not exist");
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        var token = new Token
        {
            Value = request.Value,
            Key = request.Key,
            IV = request.IV,
            UserId = user.Id
        };

        var result = await _tokenRepository.AddToken(token);
        if (result)
        {
            Console.WriteLine("Sending response: " + user.Id);
            return Ok(new UserIdResponse(user.Id));
        }
        
        Console.WriteLine("Sending response: " + "Token could not be added");
        return BadRequest(new MessageResponse("Token could not be added"));
    }

    private async Task<IdentityUser?> GetUserFromToken(IHeaderDictionary headers)
    {
        if (!headers.TryGetValue("Authorization", out var authHeader) || !authHeader.ToString().StartsWith("Bearer "))
        {
            return null;
        }

        var token = authHeader.ToString()["Bearer ".Length..].Trim();

        var userId = _tokenService.GetUserId(token);
        
        try
        {
            await _dbContext.ConnectDatabase();
            return await _userManager.Users.FirstOrDefaultAsync(user => user.Id.Equals(userId));
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private async Task<bool> RemoveDevice(Device device)
    {
        return await _listenersManager.RemoveListener(device);
    }
}