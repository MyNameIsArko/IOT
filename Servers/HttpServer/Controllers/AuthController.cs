using HttpServer.Authentication;
using HttpServer.Communication.Requests;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;

namespace HttpServer.Controllers;

[ApiController]
[Route("api/[controller]")]
public class AuthController : Controller
{
    private readonly UserManager<IdentityUser> _userManager;
    
    private readonly ITokenService _tokenService;

    public AuthController(UserManager<IdentityUser> userManager, ITokenService tokenService)
    {
        _userManager = userManager;
        _tokenService = tokenService;
    }
    
    [HttpPost("register")]
    public async Task<IActionResult> Register([FromBody] RegisterUserRequest request)
    {
        var identityUser = new IdentityUser
        {
            UserName = request.UserName
        };

        var registerResult = await _userManager.CreateAsync(identityUser, request.Password);
        if (!registerResult.Succeeded)
        {
            return BadRequest(string.Join("\n", registerResult.Errors.Select(e => e.Description)));
        }
        
        return Ok("User was successfully created");
    }
    
    [HttpPost("login")]
    public async Task<IActionResult> Login([FromBody] LoginUserRequest request)
    {
        var user = await _userManager.FindByNameAsync(request.UserName);

        if (user is null)
        {
            return Unauthorized("User does not exist");
        }
        
        var result = await _userManager.CheckPasswordAsync(user, request.Password);

        if (!result)
        {
            return Unauthorized("Incorrect password");
        }
        
        var token = _tokenService.GenerateToken(user!.Id);
        return Ok(token);
    }
}
