using HttpServer.Authentication;
using HttpServer.Communication.Requests;
using HttpServer.Communication.Responses;
using HttpServer.Repositories;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace HttpServer.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : Controller
{
    private readonly UserManager<IdentityUser> _userManager;
    
    private readonly ITokenService _tokenService;
    
    private readonly IDeviceRepository _deviceRepository;

    public AuthController(UserManager<IdentityUser> userManager, ITokenService tokenService, IDeviceRepository deviceRepository)
    {
        _userManager = userManager;
        _tokenService = tokenService;
        _deviceRepository = deviceRepository;
    }
    
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterUserRequest request)
    {
        Console.WriteLine("Register request received: " + request);
        var identityUser = new IdentityUser
        {
            UserName = request.UserName
        };

        var registerResult = await _userManager.CreateAsync(identityUser, request.Password);
        if (!registerResult.Succeeded)
        {
            Console.WriteLine("Sending response: " + string.Join("\n", registerResult.Errors.Select(e => e.Description)));
            return BadRequest(new MessageResponse(string.Join("\n", registerResult.Errors.Select(e => e.Description))));
        }
        
        Console.WriteLine("Sending response: " + "User was successfully created");
        return Ok(new MessageResponse("User was successfully created"));
    }
    
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginUserRequest request)
    {
        Console.WriteLine("Login request received: " + request);
        
        IdentityUser? user;
        try
        {
            user = await _userManager.FindByNameAsync(request.UserName);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            Console.WriteLine("Sending response: \"Cannot check user\"");
            return Unauthorized(new MessageResponse("Cannot check user"));
        }

        if (user is null)
        {
            Console.WriteLine("Sending response: \"User does not exist\"");
            return Unauthorized(new MessageResponse("User does not exist"));
        }

        bool result;
        try
        {
            result = await _userManager.CheckPasswordAsync(user, request.Password);
        }
        catch (Exception e)
        {
            Console.WriteLine(e);
            Console.WriteLine("Sending response: \"Cannot check password\"");
            return Unauthorized(new MessageResponse("Cannot check password"));
        }

        if (!result)
        {
            Console.WriteLine("Sending response: \"Incorrect password\"");
            return Unauthorized(new MessageResponse("Incorrect password"));
        }
        
        var token = _tokenService.GenerateToken(user.Id);
        var devices = await _deviceRepository.GetUserDevices(user.Id);
        var devicesKeys = devices.Select(device => new LoginResponse.DeviceKey { Mac = device.Mac, Key = device.Key, IV = device.IV }).ToList();

        var response = new LoginResponse
        {
            DevicesKeys = devicesKeys,
            Token = token
        };
        
        Console.WriteLine("Sending response: " + response);
        return Ok(response);
    }
}
