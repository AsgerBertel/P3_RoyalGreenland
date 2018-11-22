package gui.log;


import directory.Settings;
import gui.DMSApplication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LoggingTools {

    public static void LogEvent(String fileName, LogEventType eventType){

         //Get current system time
         LocalDateTime localDateTime = LocalDateTime.now();

         //get Username
         String userName = Settings.getUsername();

         //create and write event
         rgEvent event = new rgEvent(fileName, userName, localDateTime, eventType);

         writeEventAsLog(event);
    }

    public List<rgEvent> getAllEvents(){
        List<rgEvent> listOfEvents = new ArrayList<>();
        try(Stream<String> stream = Files.lines(Paths.get(Settings.getServerAppFilesPath() + "logs.log"))){
            stream.forEachOrdered(event -> listOfEvents.add(parseEvent(event)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return listOfEvents;
    }
    private static void writeEventAsLog(rgEvent event){
        List<String> listOfEvents = EventToStringArray(event);

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Settings.getServerAppFilesPath() + "logs.log",true)))){
            pw.println(listOfEvents.get(0) + "|" +listOfEvents.get(1) + "|" +listOfEvents.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> EventToStringArray(rgEvent event){
        // [YEAR/MONTH/DATE - HOUR:MINUTES]
        String eventDate = event.getLocalDateTime().getYear() + "-" + event.getLocalDateTime().getMonthValue() + "-" + event.getLocalDateTime().getDayOfMonth()
                + "-" + event.getLocalDateTime().getHour() + ":" + event.getLocalDateTime().getMinute();

        // FILENAME blev EVENT
        String eventData = event.getFileName() + "|" + EventTypeToString(event.getEventType());

        //USER
        String eventUser = event.getUser();

        List<String> listOfEvents = new ArrayList<>();
        listOfEvents.add(eventDate);
        listOfEvents.add(eventData);
        listOfEvents.add(eventUser);

        return listOfEvents;
    }
    private rgEvent parseEvent(String eventLine){
        //split string
        String [] substrings = eventLine.split("[|]");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d-H:m");
        LocalDateTime localDateTime = LocalDateTime.parse(substrings[0],formatter);
        return new rgEvent(substrings[1],substrings[3],localDateTime, stringToLogEventType(substrings[2]));
    }

    private static String EventTypeToString(LogEventType eventType){
        switch (eventType){
            case CHANGED:
                return "changed";
            case CREATED:
                return "created";
            case ARCHIVED:
                return "archived";
            case RENAMED:
                return "renamed";
            case FOLDERRENAMED:
                return "folderRenamed";
        }
        return "error: no event named " + eventType.toString();
    }

    public String EventTypeToLocalizedString(LogEventType eventType){
        switch (eventType){
            case CHANGED:
                return DMSApplication.getMessage("Log.Changed");
            case CREATED:
                return DMSApplication.getMessage("Log.Created");
            case ARCHIVED:
                return DMSApplication.getMessage("Log.Archived");
            case RENAMED:
                return DMSApplication.getMessage("Log.Renamed");
            case FOLDERRENAMED:
                return DMSApplication.getMessage("Log.FolderRenamed");
        }
        return "error: no event named " + eventType.toString();
    }

    private LogEventType stringToLogEventType(String string){
        switch (string){
            case "changed":
                return LogEventType.CHANGED;
            case "created":
                return LogEventType.CREATED;
            case "archived":
                return LogEventType.ARCHIVED;
            case "renamed":
                return LogEventType.RENAMED;
            case "folderRenamed":
                return LogEventType.FOLDERRENAMED;
        }
        return LogEventType.CREATED;
    }
}
