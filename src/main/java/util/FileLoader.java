package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {
    public static List<File> getFilesFromDirectory(String path, String endsWith) {
        File folder = new File(path);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(endsWith));
        List<File> result = new ArrayList<>();
        if (files != null) {
            for (File f : files) result.add(f);
        }
        return result;
    }
}
