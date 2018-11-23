package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import json.AppFilesManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Optional;

public class DirectoryCloner {


    public static void main(String[] args) throws Exception {
        Settings.loadSettings();
        ArrayList<AbstractFile> newFiles = AppFilesManager.loadFileManager().getMainFiles();
        ArrayList<AbstractFile> oldFiles = new ArrayList<>();
        oldFiles.addAll(newFiles);
        printTree(newFiles, 0);
        System.out.println("\n\n");

        newFiles.add((FileManager.getInstance().createFolder("bessefar", (Folder) newFiles.get(0))));
        ((Folder) (((Folder) newFiles.get(1)).getContents().get(0))).getContents().remove(2);
        newFiles.remove(3);
        newFiles.remove(2);

        oldFiles = removeOutdatedFiles(oldFiles, newFiles, Paths.get("/test/"));
        oldFiles = addNewFiles(oldFiles, newFiles, Paths.get("/test"), Paths.get("/tester"));

        System.out.println("New tree \n\n");
        printTree(oldFiles, 0);
    }


    public static void publishFiles() throws Exception {
        FileManager fileManager = AppFilesManager.loadFileManager();
        if (fileManager == null)
            return;

        ArrayList<AbstractFile> newFiles = fileManager.getMainFiles();
        ArrayList<AbstractFile> oldFiles = AppFilesManager.loadPublishedFileList();

        oldFiles = removeOutdatedFiles(oldFiles, newFiles, Paths.get(Settings.getPublishedDocumentsPath()));
        oldFiles = addNewFiles(oldFiles, newFiles, Paths.get(Settings.getPublishedDocumentsPath()), Paths.get(Settings.getServerDocumentsPath()));

        // Replace app files
        replaceIfExists(Paths.get(Settings.getServerAppFilesPath() + AppFilesManager.FILES_LIST_FILE_NAME), Paths.get(Settings.getPublishedAppFilesPath() + AppFilesManager.FILES_LIST_FILE_NAME));
        replaceIfExists(Paths.get(Settings.getServerAppFilesPath() + AppFilesManager.FACTORY_LIST_FILE_NAME), Paths.get(Settings.getPublishedAppFilesPath() + AppFilesManager.FACTORY_LIST_FILE_NAME));
    }

    private static void replaceIfExists(Path src, Path dst) throws IOException {
        if (!Files.exists(src))
            throw new IllegalArgumentException("Given src file does not exist");

        if (Files.exists(dst))
            Files.delete(dst);

        Files.copy(src, dst);
    }

    // for testing. Recursively prints tree
    private static void printTree(ArrayList<AbstractFile> files, int offset) {
        for (AbstractFile file : files) {
            printOffset(offset);
            System.out.println(file.getName());
            if (file instanceof Folder) {
                printTree(((Folder) file).getContents(), offset + 1);
            }
        }
    }

    private static void printOffset(int i) {
        for (int j = 0; j < i; j++) {
            System.out.print("    ");
        }
    }

    // todo skrev den her rekursive klammert klokken 02:30 så det virker jo nok ikke.
    // todo ok måske virker den faktisk
    public static ArrayList<AbstractFile> removeOutdatedFiles(ArrayList<AbstractFile> oldFiles, ArrayList<AbstractFile> newFiles, Path oldFilesRoot) throws Exception {
        ArrayList<AbstractFile> modifiedOldFiles = new ArrayList<>();
        modifiedOldFiles.addAll(oldFiles);

        ArrayList<AbstractFile> filesToDelete = new ArrayList<>();
        for (AbstractFile file : oldFiles) {
            if (file instanceof Document) {
                // Documents can be compared with equals therefore we can use .contains()
                if (!newFiles.contains(file))
                    filesToDelete.add(file);
            } else if (file instanceof Folder) {
                // Folder's .equals() implementation also compares children which is not relevant here.
                // Therefore a custom contains() is used.
                if (!containsFolderWithPath(newFiles, file.getPath()))
                    filesToDelete.add(file);
            }
        }

        // Remove files from both the list and the disk
        for (AbstractFile fileToDelete : filesToDelete) {
            // Remove file from disk
            boolean success = Files.deleteIfExists(oldFilesRoot.resolve(fileToDelete.getPath()));
            if (!success)
                throw new IOException("Could not delete file " + fileToDelete.getPath() + " from " + oldFilesRoot.toString());
            // A custom .remove() is used as the folders .equals() does not fit this use case
            removeFileWithPath(modifiedOldFiles, fileToDelete.getPath());
        }

        // Recursively repeat procedure on any sub folders that are common between new and old
        for (AbstractFile oldFile : modifiedOldFiles) {
            if (oldFile instanceof Folder) {
                // Find matching folder in newFiles
                Optional<AbstractFile> newFolder = getFileByPath(newFiles, oldFile.getPath());

                if (newFolder.isPresent() && newFolder.get() instanceof Folder) {
                    Folder oldFolder = ((Folder) oldFile);
                    ArrayList<AbstractFile> newContents = removeOutdatedFiles(((Folder) oldFile).getContents(), ((Folder) newFolder.get()).getContents(), oldFilesRoot);
                    oldFolder.getContents().clear();
                    oldFolder.getContents().addAll(newContents);
                } else {
                    throw new Exception("Could not find matching folder in newFiles");
                }
            }
        }

        return modifiedOldFiles;
    }


    public static ArrayList<AbstractFile> addNewFiles(ArrayList<AbstractFile> oldFiles, ArrayList<AbstractFile> newFiles, Path oldFilesRoot, Path newFileRoot) throws IOException {
        ArrayList<AbstractFile> modifiedOldFiles = new ArrayList<>();
        modifiedOldFiles.addAll(oldFiles);

        ArrayList<AbstractFile> filesToAdd = new ArrayList<>();
        // Find files that should be added
        for (AbstractFile file : newFiles) {
            if (file instanceof Document) {
                // If the old files doesn't have this file or if the lastUpdateDate does not match
                if (!modifiedOldFiles.contains(file))
                    filesToAdd.add(file);
            } else {
                if (!containsFolderWithPath(modifiedOldFiles, file.getPath()))
                    filesToAdd.add(file);
            }
        }

        // Run procedure for any subfolder
        for (AbstractFile oldFile : oldFiles) {
            if (oldFile instanceof Folder) {
                Folder oldFolder = (Folder) oldFile;

                Optional<AbstractFile> newFolder = getFileByPath(newFiles, oldFolder.getPath()); // todo would this cause exception if they renamed a file to the name a folder had before? - Magnus

                if (newFolder.isPresent() && newFolder.get() instanceof Folder) {
                    ArrayList<AbstractFile> newChildren = addNewFiles(oldFolder.getContents(), ((Folder) newFolder.get()).getContents(), oldFilesRoot, newFileRoot);
                    oldFolder.getContents().clear();
                    oldFolder.getContents().addAll(newChildren);
                } else {
                    // todo throw exception
                }
            }
        }

        // Add the new files
        for (AbstractFile addedFile : filesToAdd) {
            Path publishPath = (oldFilesRoot.resolve(addedFile.getPath()));
            if (Files.exists(publishPath)) {
                if (addedFile instanceof Folder) {
                    deleteFolder(publishPath.toFile());
                } else {
                    Files.delete(publishPath);
                }
            }


            if (addedFile instanceof Folder) {
                copyDirectory(newFileRoot.resolve(addedFile.getPath()), publishPath);
            } else {
                Files.copy(newFileRoot.resolve(addedFile.getPath()), publishPath);
            }

            modifiedOldFiles.add(addedFile);
        }

        return modifiedOldFiles;
    }

    public static void copyDirectory(Path src, Path dst) throws IOException {
        Files.walkFileTree(src, new CopyFileVisitor(dst));
    }

    private static boolean containsFolderWithPath(ArrayList<AbstractFile> files, Path path) {
        for (AbstractFile file : files) {
            if (file.getPath().equals(path))
                return true;
        }
        return false;
    }

    private static Optional<AbstractFile> getFileByPath(ArrayList<AbstractFile> files, Path path) {
        for (AbstractFile file : files) {
            if (file.getPath().equals(path))
                return Optional.of(file);
        }
        return Optional.empty();

    }

    private static boolean removeFileWithPath(ArrayList<AbstractFile> files, Path path) {
        for (AbstractFile file : files) {
            if (file.getPath().equals(path)) {
                files.remove(file);
                return true;
            }
        }
        return false;
    }

    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    // File visitor for copying an entire directory with all children
    private static class CopyFileVisitor extends SimpleFileVisitor<Path> {
        private final Path targetPath;
        private Path sourcePath = null;

        public CopyFileVisitor(Path targetPath) {
            this.targetPath = targetPath;
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir,
                                                 final BasicFileAttributes attrs) throws IOException {
            if (sourcePath == null) {
                sourcePath = dir;
            } else {
                Files.createDirectories(targetPath.resolve(sourcePath
                        .relativize(dir)));
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(final Path file,
                                         final BasicFileAttributes attrs) throws IOException {
            Path target = targetPath.resolve(sourcePath.relativize(file));
            if (!Files.exists(target.getParent())) target.getParent().toFile().mkdirs();

            Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
            return FileVisitResult.CONTINUE;
        }
    }


}
