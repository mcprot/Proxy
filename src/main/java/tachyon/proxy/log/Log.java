package tachyon.proxy.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
    // ANSI escape code
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    // format is called for every console log message
    public static void log(MessageType type, String message) {
        // This example will print date/time, class, and log level in yellow,
        // followed by the log message and it's parameters in white .
        StringBuilder builder = new StringBuilder();

        switch (type) {
            case INFO:
                builder.append(ANSI_CYAN);
                break;
            case WARN:
                builder.append(ANSI_YELLOW);
                break;
            case ERROR:
                builder.append(ANSI_RED);
                break;
            case DEBUG:
                builder.append(ANSI_GREEN);
                break;
        }

        builder.append("[");
        builder.append(calcDate(System.currentTimeMillis()));
        builder.append("]");

        builder.append(" - ");

        builder.append(message);
        //builder.append(ANSI_RESET);
        builder.append("\n");

        System.out.print(builder.toString());
    }

    // Here you can configure the format of the output and
    // its color by using the ANSI escape codes defined above.

    private static String calcDate(long milliSecs) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date resultDate = new Date(milliSecs);
        return date_format.format(resultDate);
    }

    public enum MessageType {
        INFO, WARN, ERROR, DEBUG
    }
}