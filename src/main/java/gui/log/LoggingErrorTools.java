package gui.log;

import directory.Settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingErrorTools
{
    private static final DateTimeFormatter DESCRIPTION_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void log(Throwable throwable) {
        LocalDateTime localDateTime = LocalDateTime.now();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Settings.getServerErrorLogsPath()
                + localDateTime.format(FILENAME_FORMATTER)+".log", true)))) {
            pw.println("ERROR REPORT AT: "+localDateTime.format(DESCRIPTION_FORMATTER) +" BY USER: "+ Settings.getUsername()+" STACKTRACE:");
            throwable.printStackTrace(pw);
            pw.println("---------------------------------------------------------------------------------------------------------------");
            pw.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
            log(e);
        }
    }
}
