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

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    public static void log(LogEvent event) {
        List<String> listOfEvents = toStringArray(event);

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Settings.getServerAppFilesPath() + "logs.log", true)))) {
            pw.println(listOfEvents.get(0) + "|" + listOfEvents.get(1) + "|" + listOfEvents.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> toStringArray(LogEvent event) {
        // Date
        String eventDate = event.getLocalDateTime().format(FORMATTER);

        // FILENAME blev EVENT
        String eventData = event.getPrefixString() + "|" + event.getEventType().toString() + "|" + event.getSuffixString();

        //USER
        String eventUser = event.getUser();

        List<String> listOfEvents = new ArrayList<>();
        listOfEvents.add(eventDate);
        listOfEvents.add(eventData);
        listOfEvents.add(eventUser);

        return listOfEvents;
    }

    private static LogEvent parseEvent(String eventLine) {
        //split string

        String[] substrings = eventLine.split("[|]");

        LocalDateTime localDateTime = LocalDateTime.parse(substrings[0], FORMATTER);

        return new LogEvent(substrings[1], substrings[3], substrings[4], localDateTime, LogEventType.valueOf(substrings[2]));
    }


    public static List<LogEvent> getAllEvents() {
        List<LogEvent> listOfEvents = new ArrayList<>();

        Path logFile = Paths.get(Settings.getServerAppFilesPath() + "logs.log");

        // Return empty list if no log can be loaded from the server
        if (!Files.exists(logFile))
            return listOfEvents;

        try (Stream<String> stream = Files.lines(logFile)) {
            stream.forEachOrdered(event -> listOfEvents.add(parseEvent(event)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listOfEvents;
    }

    // Returns all changes that has been made since last push
    public static List<LogEvent> getAllUnpublishedEvents() {
        List<LogEvent> allEvents = getAllEvents();
        ArrayList<LogEvent> eventsSinceLastPublish = new ArrayList<>();

        // Add events until a push is encountered
        for (int i = allEvents.size() - 1; i >= 0; i--) {
            if (allEvents.get(i).getEventType().equals(LogEventType.CHANGES_PUBLISHED)) break;
            eventsSinceLastPublish.add(allEvents.get(i));
        }

        return eventsSinceLastPublish;
    }

    // Returns all changes that has been made since last push
    public static String getLastPublished() {
        List<LogEvent> allEvents = getAllEvents();

        // Find latest publish
        for (int i = allEvents.size() - 1; i > 0; i--) {
            if (allEvents.get(i).getEventType().equals(LogEventType.CHANGES_PUBLISHED)){
                return allEvents.get(i).getTime();
            }
        }

        return "--";
    }




}
