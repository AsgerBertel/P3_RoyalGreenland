package gui.log;

import directory.Settings;
import gui.DMSApplication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogEvent {
   private String fileName,user;
   private LocalDateTime localDateTime;
   private LogEventType eventType;

    public LogEvent(String fileName, String user, LocalDateTime localDateTime, LogEventType eventType) {
        this.fileName = fileName;
        this.user = user;
        this.localDateTime = localDateTime;
        this.eventType = eventType;
    }

    public LogEvent(String fileName, LogEventType type){
        this.user = Settings.getUsername();
        this.localDateTime = LocalDateTime.now();
        this.fileName = fileName;
        this.eventType = type;
    }

    public List<String> toStringArray() {
        // [YEAR/MONTH/DATE - HOUR:MINUTES]
        String eventDate = getLocalDateTime().getYear() + "-" + getLocalDateTime().getMonthValue() + "-" + getLocalDateTime().getDayOfMonth()
                + "-" + getLocalDateTime().getHour() + ":" + getLocalDateTime().getMinute();

        // FILENAME blev EVENT
        String eventData = getFileName() + "|" + eventTypeToString(eventType);

        //USER
        String eventUser = getUser();

        List<String> listOfEvents = new ArrayList<>();
        listOfEvents.add(eventDate);
        listOfEvents.add(eventData);
        listOfEvents.add(eventUser);

        return listOfEvents;
    }

    private static String eventTypeToString(LogEventType eventType) {
        switch (eventType) {
            case CHANGED:
                return "changed";
            case CREATED:
                return "created";
            case ARCHIVED:
                return "archived";
            case RENAMED:
                return "renamed";
            case FOLDER_RENAMED:
                return "folderRenamed";
        }
        return "error: no event named " + eventType.toString();
    }

    public String EventTypeToLocalizedString(LogEventType eventType) {
        switch (eventType) {
            case CHANGED:
                return DMSApplication.getMessage("Log.Changed");
            case CREATED:
                return DMSApplication.getMessage("Log.Created");
            case ARCHIVED:
                return DMSApplication.getMessage("Log.Archived");
            case RENAMED:
                return DMSApplication.getMessage("Log.Renamed");
            case FOLDER_RENAMED:
                return DMSApplication.getMessage("Log.FolderRenamed");
        }
        return "error: no event named " + eventType.toString();
    }

    public static LogEventType stringToLogEventType(String string) {
        switch (string) {
            case "changed":
                return LogEventType.CHANGED;
            case "created":
                return LogEventType.CREATED;
            case "archived":
                return LogEventType.ARCHIVED;
            case "renamed":
                return LogEventType.RENAMED;
            case "folderRenamed":
                return LogEventType.FOLDER_RENAMED;
        }
        return LogEventType.CREATED;
    }

    public String getFileName() {
        return fileName;
    }

    public String getUser() {
        return user;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public LogEventType getEventType() {
        return eventType;
    }

    public String getEventString(){
        LoggingTools lt = new LoggingTools();
        return getFileName() + DMSApplication.getMessage("Log.Is") + EventTypeToLocalizedString(getEventType());
    }

    public String getTime(){
        String minutes;
        if(getLocalDateTime().getMinute() < 10){
            minutes = "0"+getLocalDateTime().getMinute();
        }else{
            minutes = String.valueOf(getLocalDateTime().getMinute());
        }

        return getLocalDateTime().getDayOfMonth() + "/" +getLocalDateTime().getMonthValue() + "-" + getLocalDateTime().getYear() + " - "+getLocalDateTime().getHour() +":" + minutes;
    }
}
