package domain;

import java.nio.file.Path;
import java.util.List;

public class DuplicateGroup {

    private final String fileName;
    private final List<Path> paths;

    public DuplicateGroup(String fileName, List<Path> paths) {
        this.fileName = fileName;
        this.paths = paths;
    }

    public String getFileName() {
        return fileName;
    }

    public List<Path> getPaths() {
        return paths;
    }

    public int getCount() {
        return paths.size();
    }
}

