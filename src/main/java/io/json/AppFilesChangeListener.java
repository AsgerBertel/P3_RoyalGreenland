package io.json;

import model.managing.FileManager;
import model.managing.SettingsManager;
import model.managing.PlantManager;
import gui.AlertBuilder;
import app.DMSApplication;
import log.LoggingErrorTools;
import javafx.application.Platform;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

public class AppFilesChangeListener {

    private DMSApplication dmsApplication;
    private volatile boolean running = false;
    private FileAlterationObserver observer;

    public AppFilesChangeListener(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
        init();
    }

    private void init() {
        File directory = SettingsManager.getServerAppFilesPath().toFile();
        observer = new FileAlterationObserver(directory);
        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver fileAlterationObserver) {
            }

            @Override
            public void onDirectoryCreate(File file) {
            }

            @Override
            public void onDirectoryChange(File file) {
            }

            @Override
            public void onDirectoryDelete(File file) {
            }

            @Override
            public void onFileCreate(File file) {
            }

            @Override
            public void onFileChange(File file) {
                if (!SettingsManager.getUsername().equals(AppFilesManager.getLastEditor())) {
                    Platform.runLater(() -> {
                        FileManager.resetInstance();
                        PlantManager.resetInstance();
                        dmsApplication.getCurrentTab().update();
                    });
                }
            }

            @Override
            public void onFileDelete(File file) {
            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {
            }
        });
        try {
            observer.initialize();
        } catch (Exception e) {
            LoggingErrorTools.log(e);
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return running;
    }

    @SuppressWarnings("Duplicates")
    public void start() {
        running = true;
        Thread monitorThread = new Thread(() -> {
            while (running) {
                try {
                    observer.checkAndNotify();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    AlertBuilder.interruptedExceptionPopUp("FileMonitorThread");
                    LoggingErrorTools.log(e, 2);
                    e.printStackTrace();
                    System.exit(2);
                }
            }
        });
        monitorThread.setName("FileMonitorThread");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void stop() {
        running = false;
    }

}
