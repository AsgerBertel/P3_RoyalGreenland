package gui.log;

import gui.DMSApplication;

public enum LogEventType {
    CREATED("Created"),ARCHIVED("Archived"),CHANGED("Changed"),RENAMED("Renamed"), FOLDER_RENAMED("FolderRenamed"),
    CHANGES_PUBLISHED("Published"), RESTORED("Restored"), PLANT_CREATED("PlantCreated"), PLANT_EDITED("PlantEdited"), PLANT_DELETED("PlantDeleted"), FILE_MOVED("MovedTo"),
    PLANT_ACCESS_GIVEN("PlantAccessGiven"), PLANT_ACCESS_REMOVED("PlantAccessRemoved");

    String messageKey;

    LogEventType(String messageKey){
        this.messageKey = messageKey;
    }

    public String getLocalizedString(){
        return DMSApplication.getMessage("Log." + messageKey);
    }
}
