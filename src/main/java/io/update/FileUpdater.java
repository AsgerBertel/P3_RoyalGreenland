package io.update;

import gui.AlertBuilder;
import app.DMSApplication;
import log.LoggingErrorTools;
import javafx.application.Platform;

public class FileUpdater extends Thread {

    // The pause between each check for updates
    private static final int UPDATE_INTERVAL_SECS = 10;
    private final DMSApplication dmsApplication;
    private volatile boolean running = true;

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
            AlertBuilder.updateFailExceptionPopUp();
            LoggingErrorTools.log(e);
            e.printStackTrace();
        }
    }

    public void setRunning(boolean running){
        this.running = running;
    }

    public boolean isRunning(){
        return running;
    }

    @Override
    public void run() {
        super.run();
        while(running){
            try {
                System.out.println("Checking for updates");
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
