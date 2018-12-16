package log;

import model.managing.SettingsManager;
import app.DMSApplication;

import java.time.LocalDateTime;

public class LogEvent {
   private String prefixString,user;
   private String suffixString;
   private LocalDateTime localDateTime;
   private LogEventType eventType;

    public LogEvent(String prefixString, LogEventType type){
        this(prefixString, "", type);
    }

    public LogEvent(String prefixString, String suffixString, LogEventType type){
        this.prefixString = prefixString;
        this.suffixString = suffixString;
        this.user = SettingsManager.getUsername();
        this.localDateTime = LocalDateTime.now();
        this.eventType = type;
    }

    public LogEvent(String prefixString, String suffixString, String user, LocalDateTime localDateTime, LogEventType logEventType) {
        this.prefixString = prefixString;
        this.suffixString = suffixString;
        this.user = user;
        this.localDateTime = localDateTime;
        this.eventType =logEventType;
    }

    public String getPrefixString() {
        return prefixString;
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
        LogManager lt = new LogManager();
        return prefixString + DMSApplication.getMessage("Log.Is") + eventType.getLocalizedString() + " " + suffixString;
    }

    // Used in cell factory inside LogController
    @SuppressWarnings("unused")
    public String getTime(){
        String minutes;
        if(getLocalDateTime().getMinute() < 10)
            minutes = "0"+getLocalDateTime().getMinute();
        else
            minutes = String.valueOf(getLocalDateTime().getMinute());

        return getLocalDateTime().getDayOfMonth() + "/" +getLocalDateTime().getMonthValue() + "-" + getLocalDateTime().getYear() + " - "+getLocalDateTime().getHour() +":" + minutes;
    }

    public String getSuffixString() {
        return suffixString;
    }
}
