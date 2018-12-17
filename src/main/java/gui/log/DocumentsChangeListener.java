package gui.log;

import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Document;
import gui.AlertBuilder;
import gui.DMSApplication;
import javafx.application.Platform;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class DocumentsChangeListener
{
    private DMSApplication dmsApplication;
    private Thread monitorThread;
    private AtomicBoolean running = new AtomicBoolean();
    private FileAlterationObserver observer;

    public DocumentsChangeListener(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
        observer = generateObserver();
    }
    /**
     * Watches directory for changes, Listener only reacts on changes and calls update() in case invoked.
     * Thread sleeps for 0,2 hereafter, for good measure.
     */
    @SuppressWarnings("Duplicates")
    public void startRunning() {
        if(running.get())
            return;

        running.set(true);
        monitorThread = new Thread(() -> {
            while (running.get()) {
                try {
                    observer.checkAndNotify();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    AlertBuilder.interruptedExceptionShutdownPopUp("FileMonitor Thread");
                    LoggingErrorTools.log(e, 22);
                    e.printStackTrace();
                    System.exit(22);
                }
            }
        });
        monitorThread.setName("FileMonitorThread");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    public void stopRunning() {
        running.set(false);
    }
    public boolean isRunning() {
        return running.get();
    }

    private FileAlterationObserver generateObserver() {
        Path root = SettingsManager.getServerDocumentsPath();
        FileManager fileManager = FileManager.getInstance();
        File directory = new File(root.toString());
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
                // Don't register changes to temporary word files
                if (!(file.getName().charAt(0) == '~') || Files.exists(file.toPath())) {
                    Optional<AbstractFile> changedFile = fileManager.findInMainFiles(file.toPath());

                    if (changedFile.isPresent() && changedFile.get() instanceof Document) {
                        ((Document) changedFile.get()).setLastModified(LocalDateTime.now());
                        Platform.runLater(() ->
                                LogManager.log(new LogEvent(changedFile.get().getName(), LogEventType.CHANGED)));
                    }
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
            LoggingErrorTools.log(e); // todo maybe Alert? -kristian
            e.printStackTrace();
        }
        return observer;
    }
}
