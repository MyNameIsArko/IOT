using HttpServer.Authentication;
using HttpServer.Communication.Requests;
using HttpServer.Communication.Responses;
using HttpServer.Data.Models;
using HttpServer.Listeners;
using HttpServer.Logger;
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
    
    private readonly LoggerMock _logger;

    private readonly ITokenService _tokenService;

    public DeviceController(UserManager<IdentityUser> userManager, IDeviceRepository deviceRepository,
        ITopicDataRepository topicDataRepository, ITokenRepository tokenRepository, ITokenService tokenService,
        IListenersManager listenersManager, LoggerMock logger)
    {
        _userManager = userManager;
        _deviceRepository = deviceRepository;
        _topicDataRepository = topicDataRepository;
        _tokenRepository = tokenRepository;
        _tokenService = tokenService;
        _listenersManager = listenersManager;
        _logger = logger;
    }
    
    [AllowAnonymous]
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterDeviceRequest request)
    {
        _logger.WriteLogs("Register device request received: " + request);
        if (!HttpContext.Request.Headers.TryGetValue("Authorization", out var authHeader) || !authHeader.ToString().StartsWith("Bearer "))
        {
            return Unauthorized(new MessageResponse("No token provided"));
        }

        var token = authHeader.ToString()["Bearer ".Length..].Trim();

        if (!await _tokenRepository.DoesTokenExist(token, request.UserId))
        {
            return Unauthorized(new MessageResponse("Unknown token"));
        }

        await _tokenRepository.RemoveToken(token);

        if (await _deviceRepository.GetDevice(request.Mac) is { } deviceEntity)
        {
            var result = await RemoveDevice(deviceEntity);
            if (!result)
            {
                return BadRequest(new MessageResponse("Device is already registered and could not be removed"));
            }
        }
        
        var device = new Device
        {
            Mac = request.Mac.ToUpper(),
            UserId = request.UserId,
            RegistrationDate = DateTime.Now
        };

        var registrationResult = await _deviceRepository.AddDevice(device);

        if (!registrationResult)
        {
            return BadRequest(new MessageResponse("The device could not be registered"));
        }
        
        _listenersManager.AddListenerToDevice(device);
        
        _logger.WriteLogs("Sending response: " + "Device successfully registered");
        return Ok(new MessageResponse("Device successfully registered"));
    }

    [HttpPut("name")]
    public async Task<IActionResult> UpdateName([FromBody] UpdateNameRequest request)
    {
        _logger.WriteLogs("Update name request received: " + request);
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized(new MessageResponse("User does not exist"));
        }
        
        var device = await _deviceRepository.GetDevice(request.Mac);
        if (device is null)
        {
            return BadRequest(new MessageResponse("The device does not exist"));
        }

        if (!device.UserId.Equals(user.Id))
        {
            return Unauthorized(new MessageResponse($"User: {user.UserName} is not the owner of device with mac: {request.Mac}"));
        }

        var result = await _deviceRepository.UpdateDeviceName(device.DeviceId, request.Name);
        if (!result)
        {
            return BadRequest(new MessageResponse("Could not change device name"));
        }

        _logger.WriteLogs("Sending response: " + "Device name was successfully changed");
        return Ok(new MessageResponse("Device name was successfully changed"));
    }
    
    [HttpDelete("remove")]
    public async Task<IActionResult> Remove([FromBody] RemoveDeviceRequest request)
    {
        _logger.WriteLogs("Remove request received: " + request);
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        var device = await _deviceRepository.GetDevice(request.Mac);
        if (device is null)
        {
            return BadRequest(new MessageResponse("The device does not exist"));
        }

        if (!device.UserId.Equals(user.Id))
        {
            return Unauthorized(new MessageResponse($"User: {user.UserName} is not the owner of device with mac: {request.Mac}"));
        }

        var result = await RemoveDevice(device);
        if (!result)
        {
            return BadRequest(new MessageResponse("Device could not be removed"));
        }

        _logger.WriteLogs("Sending response: " + "Device successfully removed");
        return Ok(new MessageResponse("Device successfully removed"));
    }
    
    [HttpGet("list")]
    public async Task<ActionResult<IEnumerable<Device>>> ListDevices()
    {
        _logger.WriteLogs("List request received");
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        var devices = await _deviceRepository.GetUserDevices(user.Id);
        
        if (devices.IsNullOrEmpty())
        {
            return Ok(new DeviceListResponse(new List<DeviceResponse>()));
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

        _logger.WriteLogs("Sending response: " + response);
        return Ok(new DeviceListResponse(response));
    }

    [HttpPost("token")]
    public async Task<ActionResult<string>> AddToken([FromBody] AddTokenRequest request)
    {
        _logger.WriteLogs("Token request received: " + request);
        var user = await GetUserFromToken(HttpContext.Request.Headers);
        if (user is null)
        {
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        var token = new Token
        {
            Value = request.Value,
            UserId = user.Id
        };

        var result = await _tokenRepository.AddToken(token);
        if (result)
        {
            _logger.WriteLogs("Sending response: " + user.Id);
            return Ok(new UserIdResponse(user.Id));
        }
        
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
        
        return await _deviceRepository.RemoveDevice(device);
    }
}