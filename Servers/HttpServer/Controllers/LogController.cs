using HttpServer.Logger;
using Microsoft.AspNetCore.Mvc;

namespace HttpServer.Controllers;

[ApiController]
[Route("api/logs")]
public class LogController : Controller
{
    private LoggerMock _logger;
    
    public LogController(LoggerMock logger)
    {
        _logger = logger;
    }
    
    [HttpGet("get")]
    public async Task<IActionResult> Logs()
    {
        return Ok(_logger.GetLogs());
    }
}