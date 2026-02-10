package index;

import domain.DuplicateGroup;

import java.nio.file.Path;
import java.util.*;

public class NameIndex {

    private final boolean caseInsensitiveName;
    private final Map<String, List<Path>> groupsByKey;

    public NameIndex(boolean caseInsensitiveName) {
        this.caseInsensitiveName = caseInsensitiveName;
        this.groupsByKey = new HashMap<>();
    }

    public void add(Path path) {
        if (path == null) return;

        String fileName = extractFileName(path);
        String key = buildKey(fileName);

        List<Path> group = getOrCreateGroup(key);
        Path abs = toAbsolute(path);

        group.add(abs);
    }

    public List<DuplicateGroup> findDuplicates(int minGroupSize) {
        List<DuplicateGroup> duplicates = new ArrayList<>();

        for (Map.Entry<String, List<Path>> entry : groupsByKey.entrySet()) {
            String key = entry.getKey();
            List<Path> paths = entry.getValue();

            boolean isDuplicate = paths.size() >= minGroupSize;
            if (!isDuplicate) continue;

            List<Path> sorted = copyAndSort(paths);
            DuplicateGroup group = new DuplicateGroup(key, sorted);

            duplicates.add(group);
        }

        sortGroups(duplicates);
        return duplicates;
    }

    public int getUniqueNamesCount() {
        return groupsByKey.size();
    }

    private String extractFileName(Path path) {
        return path.getFileName().toString();
    }

    private String buildKey(String fileName) {
        if (!caseInsensitiveName) return fileName;
        return fileName.toLowerCase(Locale.ROOT);
    }

    private List<Path> getOrCreateGroup(String key) {
        List<Path> existing = groupsByKey.get(key);
        if (existing != null) return existing;

        List<Path> created = new ArrayList<>();
        groupsByKey.put(key, created);
        return created;
    }

    private Path toAbsolute(Path path) {
        return path.toAbsolutePath();
    }

    private List<Path> copyAndSort(List<Path> paths) {
        List<Path> copy = new ArrayList<>(paths);
        copy.sort(Comparator.comparing(Path::toString));
        return copy;
    }

    private void sortGroups(List<DuplicateGroup> groups) {
        groups.sort((a, b) -> {
            int byCount = Integer.compare(b.getCount(), a.getCount());
            if (byCount != 0) return byCount;
            return a.getFileName().compareTo(b.getFileName());
        });
    }
}
