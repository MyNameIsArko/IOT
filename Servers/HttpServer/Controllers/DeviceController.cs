using HttpServer.Authentication;
using HttpServer.Communication.Requests;
using HttpServer.Communication.Responses;
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
    
    private readonly IListenersManager _listenersManager;
    
    private readonly ITokenService _tokenService;

    public DeviceController(UserManager<IdentityUser> userManager, IDeviceRepository deviceRepository,
        ITopicDataRepository topicDataRepository, ITokenService tokenService, IListenersManager listenersManager)
    {
        _userManager = userManager;
        _deviceRepository = deviceRepository;
        _topicDataRepository = topicDataRepository;
        _tokenService = tokenService;
        _listenersManager = listenersManager;
    }
    
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterDeviceRequest request)
    {
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized("User does not exist");
        }

        if (await _deviceRepository.GetDevice(request.Mac) is { } deviceEntity)
        {
            var result = await RemoveDevice(deviceEntity);
            if (!result)
            {
                return BadRequest("Device is already registered and could not be removed");
            }
        }
        
        var device = new Device
        {
            Mac = request.Mac.ToUpper(),
            UserId = user.Id,
            RegistrationDate = DateTime.Now
        };

        var registrationResult = await _deviceRepository.AddDevice(device);

        if (!registrationResult)
        {
            return BadRequest("The device could not be registered");
        }
        
        _listenersManager.AddListenerToDevice(device);
        return Ok("Device successfully registered");
    }

    [HttpPut("name")]
    public async Task<IActionResult> UpdateName([FromBody] UpdateNameRequest request)
    {
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized("User does not exist");
        }
        
        var device = await _deviceRepository.GetDevice(request.Mac);
        if (device is null)
        {
            return BadRequest("The device does not exist");
        }

        if (!device.UserId.Equals(user.Id))
        {
            return Unauthorized($"User: {user.UserName} is not the owner of device with mac: {request.Mac}");
        }

        var result = await _deviceRepository.UpdateDeviceName(device.DeviceId, request.Name);
        if (!result)
        {
            return BadRequest("Could not change device name");
        }

        return Ok("Device name was successfully changed");
    }
    
    [HttpPost("remove")]
    public async Task<IActionResult> Remove([FromBody] RemoveDeviceRequest request)
    {
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized();
        }

        var device = await _deviceRepository.GetDevice(request.Mac);
        if (device is null)
        {
            return BadRequest("The device does not exist");
        }

        if (!device.UserId.Equals(user.Id))
        {
            return Unauthorized($"User: {user.UserName} is not the owner of device with mac: {request.Mac}");
        }

        var result = await RemoveDevice(device);
        if (!result)
        {
            return BadRequest("Device could not be removed");
        }

        return Ok("Device successfully removed");
    }
    
    [HttpGet("list")]
    public async Task<ActionResult<IEnumerable<Device>>> ListDevices()
    {
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized();
        }

        var devices = await _deviceRepository.GetUserDevices(user.Id);
        
        if (devices.IsNullOrEmpty())
        {
            return Ok(new List<Device>());
        }

        List<DeviceResponse> response = new();
        foreach (var device in devices)
        {
            var temperature = await _topicDataRepository.GetLastDataUpdate(device.DeviceId, Topic.Temperature);
            var humidity = await _topicDataRepository.GetLastDataUpdate(device.DeviceId, Topic.Humidity);
            response.Add(new DeviceResponse
            {
                Mac = device.Mac,
                Name = device.Name,
                Temperature = temperature?.Data,
                Humidity = humidity?.Data,
                LastTemperatureUpdate = temperature?.CreatedAt,
                LastHumidityUpdate = humidity?.CreatedAt
            });
        }

        return Ok(response);
    }

    private async Task<IdentityUser?> GetUserFromToken(IHeaderDictionary headers)
    {
        if (!headers.TryGetValue("Authorization", out var authHeader) || !authHeader.ToString().StartsWith("Bearer "))
        {
            return null;
        }

        var token = authHeader.ToString()["Bearer ".Length..].Trim();

        var userId = _tokenService.GetUserId(token);
        var user = await _userManager.Users.FirstOrDefaultAsync(user => user.Id.Equals(userId));

        return user;
    }

    private async Task<bool> RemoveDevice(Device device)
    {
        var listenerRemoved = _listenersManager.RemoveListener(device);
        if (!listenerRemoved)
        {
            return false;
        }
        
        var deviceRemoved = await _deviceRepository.RemoveDevice(device);
        if (!deviceRemoved)
        {
            _listenersManager.AddListenerToDevice(device);
            return false;
        }

        return true;
    }
}