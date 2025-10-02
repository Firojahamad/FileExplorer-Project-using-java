import java.io.*;
import java.nio.file.*;
import java.awt.Desktop;
import java.util.Scanner;
import java.util.Date;

public class FileExplorer {
    private static File currentDir = new File(System.getProperty("user.dir")); // Start in current directory
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Java CMD File Explorer ===");
        System.out.println("Commands: ls, cd <dir>, pwd, copy <src> <dest>, move <src> <dest>, rename <old> <new>, del <file/dir>, search <name>, info <file/dir>, open <file>, create <file>, edit <file>, mkdir <dirname>, rmdir <dirname>, exit");

        while (true) {
            System.out.print("\n" + currentDir.getAbsolutePath() + " > ");
            String command = sc.nextLine().trim();

            if (command.equalsIgnoreCase("exit")) {
                System.out.println("Exiting File Explorer...");
                break;
            }
            processCommand(command);
        }
    }

    private static void processCommand(String command) {
        try {
            if (command.equals("ls")) {
                listFiles();
            } else if (command.equals("pwd")) {
                System.out.println(currentDir.getAbsolutePath());
            } else if (command.startsWith("cd")) {   // handle cd, cd.., cd ../, cd folder, absolute path
                String arg = command.length() > 2 ? command.substring(2).trim() : "";
                changeDirectory(arg);
            } else if (command.startsWith("copy ")) {
                String[] parts = command.split(" ", 3);
                if (parts.length == 3) copyItem(parts[1], parts[2]);
                else System.out.println("Usage: copy <source> <dest>");
            } else if (command.startsWith("move ")) {
                String[] parts = command.split(" ", 3);
                if (parts.length == 3) moveFile(parts[1], parts[2]);
                else System.out.println("Usage: move <source> <dest>");
            } else if (command.startsWith("rename ")) {
                String[] parts = command.split(" ", 3);
                if (parts.length == 3) renameFile(parts[1], parts[2]);
                else System.out.println("Usage: rename <oldName> <newName>");
            } else if (command.startsWith("del ")) {
                deleteItem(command.substring(4).trim());
            } else if (command.startsWith("info ")) {
                fileInfo(command.substring(5).trim());
            } else if (command.startsWith("search ")) {
                searchFile(command.substring(7).trim(), currentDir);
            } else if (command.startsWith("open ")) {
                openFile(command.substring(5).trim());
            } else if (command.startsWith("create ")) {
                createFile(command.substring(7).trim());
            } else if (command.startsWith("edit ")) {
                editFile(command.substring(5).trim());
            } else if (command.startsWith("mkdir ")) {
                createDirectory(command.substring(6).trim());
            } else if (command.startsWith("rmdir ")) {
                deleteDirectory(new File(currentDir, command.substring(6).trim()));
            } else {
                System.out.println("Unknown command! Try: ls, cd, pwd, copy, move, rename, del, search, info, open, create, edit, mkdir, rmdir, exit");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // === COMMAND IMPLEMENTATIONS ===

    private static void listFiles() {
        File[] files = currentDir.listFiles();
        if (files != null) {
            for (File f : files) {
                System.out.println((f.isDirectory() ? "[DIR] " : "[FILE] ") + f.getName());
            }
        }
    }

    private static void changeDirectory(String path) {
        try {
            if (path.equals("") || path.equals(".")) {
                return; // stay in same dir
            }

            // Handle cd.. and cd../ etc.
            if (path.equals("..") || path.equals("..\\") || path.equals("../") || path.equals("..")) {
                File parent = currentDir.getParentFile();
                if (parent != null) {
                    currentDir = parent;
                } else {
                    System.out.println("Already at root directory!");
                }
                return;
            }

            // Handle relative and absolute paths
            File newDir = new File(path);
            if (!newDir.isAbsolute()) {
                newDir = new File(currentDir, path);
            }

            newDir = newDir.getCanonicalFile(); // normalize path

            if (newDir.exists() && newDir.isDirectory()) {
                currentDir = newDir;
            } else {
                System.out.println("Directory not found!");
            }
        } catch (IOException e) {
            System.out.println("Invalid path: " + e.getMessage());
        }
    }

  


private static void copyItem(String src, String dest) throws IOException {
    File source = new File(currentDir, src).getCanonicalFile();   // normalize
    File destination = new File(dest);

    if (!destination.isAbsolute()) {
        destination = new File(currentDir, dest);
    }

    destination = destination.getCanonicalFile();  // normalize

    if (!source.exists()) {
        System.out.println("Source not found!");
        return;
    }

    // If destination is directory → copy inside with same name
    if (destination.exists() && destination.isDirectory()) {
        destination = new File(destination, source.getName());
    }

    // If same folder → auto rename like Windows
    if (destination.exists() && destination.getParentFile().equals(source.getParentFile())) {
        destination = getAvailableCopyName(destination, source.isFile());
    }

    if (source.isDirectory()) {
        copyDirectory(source.toPath(), destination.toPath());
        System.out.println("Directory copied to: " + destination.getName());
    } else {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("File copied to: " + destination.getName());
    }
}


// === Helper: Generate proper duplicate name ===
private static File getAvailableCopyName(File file, boolean isFile) {
    String name = file.getName();
    String baseName = name;
    String extension = "";

    // Only split extension for files
    if (isFile) {
        int dotIndex = name.lastIndexOf(".");
        if (dotIndex != -1) {
            baseName = name.substring(0, dotIndex);
            extension = name.substring(dotIndex);
        }
    }

    int count = 1;
    File newFile;
    do {
        String newName = baseName + " (" + count + ")" + extension;
        newFile = new File(file.getParentFile(), newName);
        count++;
    } while (newFile.exists());

    return newFile;
}

// === Copy directory recursively ===
private static void copyDirectory(Path source, Path target) throws IOException {
    Files.walk(source).forEach(path -> {
        try {
            Path relative = source.relativize(path);
            Path destPath = target.resolve(relative);
            if (Files.isDirectory(path)) {
                if (!Files.exists(destPath)) {
                    Files.createDirectories(destPath);
                }
            } else {
                Files.copy(path, destPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
}








    private static void moveFile(String src, String dest) throws IOException {
        File source = new File(currentDir, src);
        File destination = new File(currentDir, dest);
        Files.move(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Moved successfully!");
    }

    private static void renameFile(String oldName, String newName) {
        File oldFile = new File(currentDir, oldName);
        File newFile = new File(currentDir, newName);
        if (oldFile.renameTo(newFile)) {
            System.out.println("Renamed successfully!");
        } else {
            System.out.println("Rename failed!");
        }
    }

    private static void deleteItem(String name) {
        File file = new File(currentDir, name);
        if (!file.exists()) {
            System.out.println("File/Directory not found!");
            return;
        }
        if (file.isDirectory()) {
            deleteDirectory(file);
        } else {
            if (file.delete()) System.out.println("Deleted: " + name);
            else System.out.println("Could not delete file!");
        }
    }

    private static void fileInfo(String filename) {
        File file = new File(currentDir, filename);
        if (file.exists()) {
            System.out.println("Name: " + file.getName());
            System.out.println("Path: " + file.getAbsolutePath());
            System.out.println("Size: " + file.length() + " bytes");
            System.out.println("Last Modified: " + new Date(file.lastModified()));
            System.out.println("Type: " + (file.isDirectory() ? "Directory" : "File"));
        } else {
            System.out.println("File not found!");
        }
    }

    private static void searchFile(String name, File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().toLowerCase().contains(name.toLowerCase())) {
                    System.out.println("Found: " + f.getAbsolutePath());
                }
                if (f.isDirectory()) {
                    searchFile(name, f); // Recursive search
                }
            }
        }
    }

    private static void openFile(String filename) throws IOException {
        File file = new File(currentDir, filename);
        if (!file.exists()) {
            System.out.println("File not found!");
            return;
        }
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(file);
            System.out.println("Opened: " + file.getName());
        } else {
            System.out.println("Open not supported on this system!");
        }
    }

    private static void createFile(String filename) {
        try {
            File file = new File(currentDir, filename);
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists!");
            }
        } catch (IOException e) {
            System.out.println("Error creating file: " + e.getMessage());
        }
    }

    private static void editFile(String filename) {
        File file = new File(currentDir, filename);
        if (!file.exists()) {
            System.out.println("File not found!");
            return;
        }

        System.out.println("Enter new content (type 'SAVE' in a new line to save and exit):");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            while (true) {
                String line = sc.nextLine();
                if (line.equalsIgnoreCase("SAVE")) break;
                writer.write(line);
                writer.newLine();
            }
            System.out.println("File updated successfully!");
        } catch (IOException e) {
            System.out.println("Error editing file: " + e.getMessage());
        }
    }

    private static void createDirectory(String dirname) {
        File dir = new File(currentDir, dirname);
        if (dir.mkdir()) {
            System.out.println("Directory created: " + dir.getName());
        } else {
            System.out.println("Failed to create directory (maybe exists already?)");
        }
    }

    private static void deleteDirectory(File dir) {
        File[] contents = dir.listFiles();
        if (contents != null) {
            for (File f : contents) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        if (dir.delete()) {
            System.out.println("Directory deleted: " + dir.getName());
        } else {
            System.out.println("Failed to delete directory!");
        }
    }

}
