package directory.update;

import gui.AlertBuilder;
import gui.DMSApplication;
import gui.log.LoggingErrorTools;
import javafx.application.Platform;

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
        } catch (UpdateFailException e) {
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
                DirectoryCloner.updateLocalFiles();
                Platform.runLater(() -> dmsApplication.getCurrentTab().update());
                Thread.sleep(UPDATE_INTERVAL_SECS * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                AlertBuilder.interruptedExceptionPopUp("Updater Thread");
                LoggingErrorTools.log(e);
            } catch (UpdateFailException e) {
                e.printStackTrace();
                AlertBuilder.updateFailExceptionPopUp();
                LoggingErrorTools.log(e);
            }
        }
    }
}
