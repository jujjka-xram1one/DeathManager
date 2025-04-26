package xi.xrami1one.deathManager.other;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public class FilterConsole implements Filter {
    public Result checkMessage(String message) {
        if (message.contains("Could not save"))
            return Result.DENY;
        return Result.NEUTRAL;
    }

    public State getState() {
        try {
            return State.STARTED;
        } catch (Exception exception) {
            return null;
        }
    }

    public void initialize() {}

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    public void start() {}

    public void stop() {}

    public Result filter(LogEvent event) {
        return checkMessage(event.getMessage().getFormattedMessage());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return checkMessage(message.toString());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return checkMessage(message.getFormattedMessage());
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return checkMessage(message);
    }

    public Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
        return checkMessage(message);
    }

    public Result getOnMatch() {
        return Result.NEUTRAL;
    }

    public Result getOnMismatch() {
        return Result.NEUTRAL;
    }
}
