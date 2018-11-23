package gui.log;

import gui.DMSApplication;

import java.time.LocalDateTime;

public class rgEvent {
   private String fileName,user;
   private LocalDateTime localDateTime;
   private LogEventType eventType;

    public rgEvent(String fileName, String user, LocalDateTime localDateTime, LogEventType eventType) {
        this.fileName = fileName;
        this.user = user;
        this.localDateTime = localDateTime;
        this.eventType = eventType;
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

    public String getTime(){
        String minutes;
        if(getLocalDateTime().getMinute() < 10){
            minutes = "0"+getLocalDateTime().getMinute();
        }else{
            minutes = String.valueOf(getLocalDateTime().getMinute());
        }

        return getLocalDateTime().getDayOfMonth() + "/" +getLocalDateTime().getMonthValue() + "-" + getLocalDateTime().getYear() + " - "+getLocalDateTime().getHour() +":" + minutes;
    }

    public String getEvent(){
        LoggingTools lt = new LoggingTools();
        return getFileName() + DMSApplication.getMessage("Log.Is") + lt.EventTypeToLocalizedString(getEventType());
    }
}
