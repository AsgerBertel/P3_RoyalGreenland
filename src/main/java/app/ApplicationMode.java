package app;

public enum ApplicationMode {

    ADMIN("Admin"), VIEWER("Viewer");

    private final String string;
    ApplicationMode(String string){this.string = string;}
}
