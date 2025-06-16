package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {
    public static List<File> getFilesFromDirectory(String folderPath, String extension) {
        List<File> fileList = new ArrayList<>();
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(extension));
            if (files != null) {
                for (File file : files) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }
}
