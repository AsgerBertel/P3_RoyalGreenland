package gui.log;

import directory.SettingsManager;
import gui.DMSApplication;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LoggingErrorTools
{
    private static final DateTimeFormatter DESCRIPTION_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");
    private static final DateTimeFormatter FILENAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void log(Throwable throwable) {
        LocalDateTime localDateTime = LocalDateTime.now();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                generateWritePath(localDateTime), true)))) {
            pw.println("EXITCODE: 0\nERROR REPORT AT: "+localDateTime.format(DESCRIPTION_FORMATTER) +" BY USER: "+ SettingsManager.getUsername()+" | STACKTRACE:");
            throwable.printStackTrace(pw);
            pw.println("------------------------------------------------------------------------------------------------------------------------");
            pw.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
            log(e);
        }
    }
    public static void log(Throwable throwable, int exitCode) {
        LocalDateTime localDateTime = LocalDateTime.now();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(
                generateWritePath(localDateTime), true)))) {
            pw.println("EXITCODE: +"+exitCode+"\nERROR REPORT AT: "+localDateTime.format(DESCRIPTION_FORMATTER) +" BY USER: "+ SettingsManager.getUsername()+" | STACKTRACE:");
            throwable.printStackTrace(pw);
            pw.println("------------------------------------------------------------------------------------------------------------------------");
            pw.println("\n");
        } catch (IOException e) {
            e.printStackTrace();
            log(e);
        }
    }
    public static String generateWritePath (LocalDateTime localDateTime) {

        switch(DMSApplication.getApplicationMode()) {
            case ADMIN:
                return SettingsManager.getServerErrorLogsPath()
                        + File.separator
                        + localDateTime.format(FILENAME_FORMATTER)+" - "+SettingsManager.getUsername()+".log";
            case VIEWER:
                return SettingsManager.getLocalErrorLogsPath()
                        + File.separator
                        + localDateTime.format(FILENAME_FORMATTER)+" - "+SettingsManager.getUsername()+".log";
                default:
                    return SettingsManager.getServerErrorLogsPath()
                            + File.separator
                            + localDateTime.format(FILENAME_FORMATTER)+" - "+SettingsManager.getUsername()+".log";
        }
    }
}
