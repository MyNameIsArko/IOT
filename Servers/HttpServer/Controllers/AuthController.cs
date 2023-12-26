using HttpServer.Authentication;
using HttpServer.Communication.Requests;
using HttpServer.Communication.Responses;
using HttpServer.Logger;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace HttpServer.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : Controller
{
    private readonly UserManager<IdentityUser> _userManager;
    
    private readonly ITokenService _tokenService;
    
    private readonly LoggerMock _logger;

    public AuthController(UserManager<IdentityUser> userManager, ITokenService tokenService, LoggerMock logger)
    {
        _userManager = userManager;
        _tokenService = tokenService;
        _logger = logger;
    }
    
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterUserRequest request)
    {
        _logger.WriteLogs("Register request received: " + request);
        var identityUser = new IdentityUser
        {
            UserName = request.UserName
        };

        var registerResult = await _userManager.CreateAsync(identityUser, request.Password);
        if (!registerResult.Succeeded)
        {
            return BadRequest(new MessageResponse(string.Join("\n", registerResult.Errors.Select(e => e.Description))));
        }
        
        _logger.WriteLogs("Sending response: " + "User was successfully created");
        return Ok(new MessageResponse("User was successfully created"));
    }
    
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginUserRequest request)
    {
        _logger.WriteLogs("Login request received: " + request);
        var user = await _userManager.FindByNameAsync(request.UserName);

        if (user is null)
        {
            return Unauthorized(new MessageResponse("User does not exist"));
        }
        
        var result = await _userManager.CheckPasswordAsync(user, request.Password);

        if (!result)
        {
            return Unauthorized(new MessageResponse("Incorrect password"));
        }
        
        var token = _tokenService.GenerateToken(user.Id);
        
        _logger.WriteLogs("Sending response: " + token);
        return Ok(new TokenResponse(token));
    }
}
