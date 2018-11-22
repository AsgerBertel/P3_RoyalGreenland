package gui.log;

import directory.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LoggingTools {

    public static void log(LogEvent event){
        List<String> listOfEvents = event.toStringArray();

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Settings.getServerAppFilesPath() + "logs.log", true)))) {
            pw.println(listOfEvents.get(0) + "|" + listOfEvents.get(1) + "|" + listOfEvents.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LogEvent parseEvent(String eventLine) {
        //split string
        String[] substrings = eventLine.split("[|]");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d-H:m");
        LocalDateTime localDateTime = LocalDateTime.parse(substrings[0], formatter);
        return new LogEvent(substrings[1], substrings[3], localDateTime, LogEvent.stringToLogEventType(substrings[2]));
    }

    public List<LogEvent> getAllEvents() {
        List<LogEvent> listOfEvents = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(Settings.getServerAppFilesPath() + "logs.log"))) {
            stream.forEachOrdered(event -> listOfEvents.add(parseEvent(event)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfEvents;
    }

}
