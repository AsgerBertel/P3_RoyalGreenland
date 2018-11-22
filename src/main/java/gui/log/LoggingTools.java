package gui.log;

import directory.Settings;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LoggingTools {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    public static void log(LogEvent event){
        List<String> listOfEvents = toStringArray(event);

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Settings.getServerAppFilesPath() + "logs.log", true)))) {
            pw.println(listOfEvents.get(0) + "|" + listOfEvents.get(1) + "|" + listOfEvents.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> toStringArray(LogEvent event) {
        // Date
        String eventDate = event.getLocalDateTime().format(formatter);

        // FILENAME blev EVENT
        String eventData = event.getFileName() + "|" + event.getEventType().toString();

        //USER
        String eventUser = event.getUser();

        List<String> listOfEvents = new ArrayList<>();
        listOfEvents.add(eventDate);
        listOfEvents.add(eventData);
        listOfEvents.add(eventUser);

        return listOfEvents;
    }

    private LogEvent parseEvent(String eventLine) {
        //split string

        String[] substrings = eventLine.split("[|]");
        System.out.println("attempting to parse : " + substrings[0]);

        LocalDateTime localDateTime = LocalDateTime.parse(substrings[0], formatter);
        return new LogEvent(substrings[1], substrings[3], localDateTime, LogEventType.valueOf(substrings[2]));
    }


    public List<LogEvent> getAllEvents() {
        List<LogEvent> listOfEvents = new ArrayList<>();

        Path logFile = Paths.get(Settings.getServerAppFilesPath() + "logs.log");

        // Return empty list if no log can be loaded from the server
        if(!Files.exists(logFile))
            return listOfEvents;

        try (Stream<String> stream = Files.lines(logFile)) {
            stream.forEachOrdered(event -> listOfEvents.add(parseEvent(event)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfEvents;
    }

}
