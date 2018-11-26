package gui.log;

import directory.Settings;
import gui.DMSApplication;

import java.time.LocalDateTime;

public class LogEvent {
   private String subject,user;
   private LocalDateTime localDateTime;
   private LogEventType eventType;

    public LogEvent(String subject, String user, LocalDateTime localDateTime, LogEventType eventType) {
        this.subject = subject;
        this.user = user;
        this.localDateTime = localDateTime;
        this.eventType = eventType;
    }

    public LogEvent(String subject, LogEventType type){
        this.user = Settings.getUsername();
        this.localDateTime = LocalDateTime.now();
        this.subject = subject;
        this.eventType = type;
    }

    public String getSubject() {
        return subject;
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


    // Used in cell factory inside LogController (despite being marked as unused by intellij)
    @SuppressWarnings("unused")
    public String getEventString(){
        LoggingTools lt = new LoggingTools();
        return getSubject() + DMSApplication.getMessage("Log.Is") + eventType.getLocalizedString();
    }

    // Used in cell factory inside LogController
    @SuppressWarnings("unused")
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
