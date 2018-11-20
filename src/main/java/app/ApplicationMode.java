package app;

public enum ApplicationMode {

    ADMIN("Admin"), VIEWER("Viewer");

    private String string;
    ApplicationMode(String string){this.string = string;}

    @Override
    public String toString() {
        return string;
    }
}
