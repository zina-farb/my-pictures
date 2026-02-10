package scan;

import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;

public class ExtensionFileFilter {

    private final Set<String> normalizedExtensions;

    public ExtensionFileFilter(Set<String> extensions) {
        this.normalizedExtensions = normalizeExtensions(extensions);
    }

    public boolean matches(Path path) {
        if (path == null) return false;

        String fileName = getFileName(path);
        String ext = extractExtension(fileName);
        if (ext == null) return false;

        String normalizedExt = normalize(ext);
        return containsExtension(normalizedExt);
    }

    private Set<String> normalizeExtensions(Set<String> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            throw new IllegalArgumentException("extensions is empty");
        }

        Set<String> result = new java.util.HashSet<>();
        for (String ext : extensions) {
            if (ext == null) continue;
            String normalized = normalize(ext);
            if (!normalized.isEmpty()) result.add(normalized);
        }
        return Set.copyOf(result);
    }

    private String getFileName(Path path) {
        return path.getFileName().toString();
    }

    private String extractExtension(String fileName) {
        int dotIndex = lastDotIndex(fileName);
        boolean hasNoDot = dotIndex < 0;
        if (hasNoDot) return null;

        boolean dotIsLastChar = dotIndex == fileName.length() - 1;
        if (dotIsLastChar) return null;

        return fileName.substring(dotIndex + 1);
    }

    private int lastDotIndex(String fileName) {
        return fileName.lastIndexOf('.');
    }

    private String normalize(String s) {
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private boolean containsExtension(String normalizedExt) {
        return normalizedExtensions.contains(normalizedExt);
    }
}
