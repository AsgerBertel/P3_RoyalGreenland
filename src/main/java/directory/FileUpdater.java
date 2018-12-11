package directory;

import gui.AlertBuilder;
import gui.DMSApplication;
import gui.log.LoggingErrorTools;
import javafx.application.Platform;

import java.io.IOException;

public class FileUpdater extends Thread {

    // The pause between each check for updates
    private static final int UPDATE_INTERVAL_SECS = 10;
    private DMSApplication dmsApplication;
    private boolean running = true;

    public FileUpdater(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
        setDaemon(true);
    }

    @Override
    public synchronized void start() {
        super.start();
        running = true;
        try {
            DirectoryCloner.updateLocalFiles();
        } catch (IOException e) {
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    @Override
    public void run() {
        super.run();
        while(running){
            try {
                Thread.sleep(UPDATE_INTERVAL_SECS * 1000);
                DirectoryCloner.updateLocalFiles();
                Platform.runLater(() -> dmsApplication.getCurrentTab().update());
            } catch (InterruptedException e) {
                AlertBuilder.interruptedExceptionPopup("Updater Thread");
                LoggingErrorTools.log(e);
                e.printStackTrace();
            } catch (IOException e) {
                AlertBuilder.IOExceptionPopUp();
                LoggingErrorTools.log(e);
                e.printStackTrace();
            }
        }


    }
}
