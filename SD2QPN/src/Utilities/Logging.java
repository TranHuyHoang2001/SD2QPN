package Utilities;

public class Logging {
    public static void log(String message)
    {
        System.out.println(message);
    }

    public static void Warning(String message)
    {
        System.out.println("WARNING: " + message);
    }

    public static void Error(String message)
    {
        System.out.println("ERROR: " + message);
        System.exit(0);
    }
}
