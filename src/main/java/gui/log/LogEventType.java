package gui.log;

import gui.DMSApplication;

public enum LogEventType {
    Created("Created"),ARCHIVED("Archived"),CHANGED("Changed"),RENAMED("Renamed"), FOLDER_RENAMED("FolderRenamed"), CHANGES_PUBLISHED("Published");

    String messageKey;

    LogEventType(String messageKey){
        this.messageKey = messageKey;
    }

    public String getLocalizedString(){
        return DMSApplication.getMessage("Log." + messageKey);
    }


}
