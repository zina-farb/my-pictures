package scan;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;

public class FileScanner {

    private final ExtensionFileFilter filter;
    private final boolean followLinks;

    public FileScanner(ExtensionFileFilter filter, boolean followLinks) {
        this.filter = filter;
        this.followLinks = followLinks;
    }

    public List<Path> scan(Path root) throws IOException {
        validateRoot(root);

        List<Path> matchedFiles = new ArrayList<>();
        FileVisitor visitor = new FileVisitor(matchedFiles);

        walkFileTree(root, visitor);

        return matchedFiles;
    }

    private void validateRoot(Path root) {
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        if (!Files.exists(root)) {
            throw new IllegalArgumentException("root does not exist");
        }
        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("root is not a directory");
        }
    }

    private void walkFileTree(Path root, FileVisitor visitor) throws IOException {
        EnumSet<FileVisitOption> options = visitOptions();
        Files.walkFileTree(root, options, Integer.MAX_VALUE, visitor);
    }

    private EnumSet<FileVisitOption> visitOptions() {
        if (followLinks) {
            return EnumSet.of(FileVisitOption.FOLLOW_LINKS);
        }
        return EnumSet.noneOf(FileVisitOption.class);
    }

    private class FileVisitor extends SimpleFileVisitor<Path> {

        private final List<Path> matchedFiles;

        FileVisitor(List<Path> matchedFiles) {
            this.matchedFiles = matchedFiles;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            handleFile(file, attrs);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            handleFailedFile(file, exc);
            return FileVisitResult.CONTINUE;
        }

        private void handleFile(Path file, BasicFileAttributes attrs) {
            if (!isRegularFile(attrs)) return;

            boolean matches = filter.matches(file);
            if (!matches) return;

            addMatchedFile(file);
        }

        private boolean isRegularFile(BasicFileAttributes attrs) {
            return attrs != null && attrs.isRegularFile();
        }

        private void addMatchedFile(Path file) {
            matchedFiles.add(file.toAbsolutePath());
        }

        private void handleFailedFile(Path file, IOException exc) {
            // intentionally ignored
        }
    }
}
