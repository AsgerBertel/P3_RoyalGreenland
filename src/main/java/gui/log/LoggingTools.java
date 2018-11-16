package gui.log;


import directory.PathsManager;
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

    public void LogEvent(String fileName,LogEventType eventType){

         //Get current system time
         LocalDateTime localDateTime = LocalDateTime.now();

         //get Username
         String userName = System.getProperty("user.name");

         //create and write event
         rgEvent event = new rgEvent(fileName, userName, localDateTime,eventType);

         writeEventAsLog(event);
    }
    public List<rgEvent> listOfAllEvents(){
        List<rgEvent> listOfEvents = new ArrayList<>();
        try(Stream<String> stream = Files.lines(Paths.get(PathsManager.getInstance().getServerAppFilesPath() + "logs.log"))){
            stream.forEachOrdered(event -> listOfEvents.add(parseEvent(event)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return listOfEvents;
    }
    private void writeEventAsLog(rgEvent event){
        List<String> listOfEvents = EventToStringArray(event);

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(PathsManager.getInstance().getServerAppFilesPath() + "logs.log",true)))){
            pw.println(listOfEvents.get(0) + "|" +listOfEvents.get(1) +"|"+listOfEvents.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String EventTypeToString(LogEventType eventType){
        // Todo Should we maybe not switch on a danish word? We need greenlandic as well - Philip
         switch (eventType){
             case CHANGED:
                 return DMSApplication.getMessage("Log.Changed");
             case CREATED:
                 return DMSApplication.getMessage("Log.Added");
             case ARCHIVED:
                 return DMSApplication.getMessage("Log.Archived");
             case DELETED:
                 return DMSApplication.getMessage("Log.Deleted");
         }
         return "error: no event named " + eventType.toString();
    }
    private List<String> EventToStringArray(rgEvent event){

        // [YEAR/MONTH/DATE - HOUR:MINUTES]
        String eventDate =event.getLocalDateTime().getYear() + "-" + event.getLocalDateTime().getMonthValue() + "-" + event.getLocalDateTime().getDayOfMonth()
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

    private LogEventType stringToLogEventType(String string){

        // Todo Should we maybe not switch on a danish word? We need greenlandic as well - Philip
        // Doesnt matter here since danish word is stored in .log file only, which will never be opened
        switch (string){
            case "ændret":
                return LogEventType.CHANGED;
            case "tilføjet":
                return LogEventType.CREATED;
            case "arkiveret":
                return LogEventType.ARCHIVED;
            case "slettet":
                return LogEventType.DELETED;
        }
        return LogEventType.CREATED;
    }
}
