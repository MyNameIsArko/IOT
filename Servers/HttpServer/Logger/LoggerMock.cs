namespace HttpServer.Logger;

public class LoggerMock
{
    private readonly string _logsFilePath = "./Logger/logs.txt";

    public string GetLogs()
    {
        if (File.Exists(_logsFilePath))
        {
            try
            {
                using var reader = new StreamReader(_logsFilePath);
                return reader.ReadToEnd();
            }
            catch (Exception)
            {
                // ignored
            }
        }
        return string.Empty;
    }

    public void WriteLogs(string message)
    {
        try
        {
            using var writer = new StreamWriter(_logsFilePath, true);
            writer.WriteLine(message);
        }
        catch (Exception)
        {
            // ignored
        }
    }
}