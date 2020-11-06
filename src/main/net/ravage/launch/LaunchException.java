package net.ravage.launch;

public class LaunchException extends Exception
{
    public LaunchException(final String message) {
        super(message);
    }
    
    public LaunchException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
