package config;


import java.nio.file.Path;
import java.util.Set;

public record ScanConfig(
        Path root,
        Set<String> extensions,
        boolean caseInsensitiveName,
        boolean followLinks
) {
    public ScanConfig {
        if (root == null) throw new IllegalArgumentException("root is null");
        if (extensions == null || extensions.isEmpty()) throw new IllegalArgumentException("extensions is empty");
    }


}

