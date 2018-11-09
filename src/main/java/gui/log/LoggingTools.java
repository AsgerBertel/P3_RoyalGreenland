package gui.log;


import json.JsonParser;

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

         //writeEventAsJson(event);
         writeEventAsJson(event);
         //writeEventAsLog(event);

    }

    public void writeEventAsJson(rgEvent event){

    }

    public List<rgEvent> listOfAllEvents(){
        List<rgEvent> listOfEvents = new ArrayList<>();
        try(Stream<String> stream = Files.lines(Paths.get("Sample Files/logs.log"))){
            stream.forEachOrdered(event -> listOfEvents.add(parseEvent(event)));
        }catch (IOException e){
            e.printStackTrace();
        }
        return listOfEvents;
    }

    private void writeEventAsLog(rgEvent event){
        List<String> listOfEvents = EventToStringArray(event);

        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter("Sample Files/logs.log",true)))){
            pw.println(listOfEvents.get(0) + "|" +listOfEvents.get(1) +"|"+listOfEvents.get(2));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String EventTypeToString(LogEventType eventType){
         switch (eventType){
             case CHANGED:
                 return "ændret";
             case CREATED:
                 return "tilføjet";
             case ARCHIVED:
                 return "arkiveret";
             case DELETED:
                 return "slettet";
         }
         return "error: no event named " + eventType.toString();
    }
    private List<String> EventToStringArray(rgEvent event){

        // [YEAR/MONTH/DATE - HOUR:MINUTES]
        String eventDate =event.getLocalDateTime().getYear() + "/" + event.getLocalDateTime().getMonthValue() + "/" + event.getLocalDateTime().getDayOfMonth()
                + "/" + event.getLocalDateTime().getHour() + ":" + event.getLocalDateTime().getMinute();

        // FILENAME blev EVENT
        String eventData = event.getFileName() + "/" + EventTypeToString(event.getEventType());

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
        String [] substrings =  eventLine.split("[/:\\|]"); //NOT REDUNDANT
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d:H:m");
        LocalDateTime localDateTime = LocalDateTime.parse(substrings[0] + "-"+substrings[1]+"-"+substrings[2] + ":"+ substrings[3] +":"+substrings[4],formatter);

        for(String s : substrings){
            System.out.println(s);
        }

        return new rgEvent(substrings[5],substrings[7],localDateTime, stringToLogEventType(substrings[6]));
    }
    private LogEventType stringToLogEventType(String string){

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
