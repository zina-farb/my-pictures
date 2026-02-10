package app;

import domain.DuplicateGroup;
import index.NameIndex;
import scan.ExtensionFileFilter;
import scan.FileScanner;
import config.ScanConfig;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class App {
    public static void main(String[] args) throws IOException {
        // configuration
        ScanConfig config = new ScanConfig(Path.of("C:\\backup\\Albom"),Set.of("jpg","jpeg","png"),true,true);

        ExtensionFileFilter filter = new ExtensionFileFilter(config.extensions());
        FileScanner scanner = new FileScanner(filter, false);

        List<Path> files = scanner.scan(config.root());

        NameIndex index = new NameIndex(true);
        for (Path file : files) {
            index.add(file);
        }

        int minGroupSize = 2;
        List<DuplicateGroup> duplicates = index.findDuplicates(minGroupSize);

        System.out.println("Matched files: " + files.size());
        System.out.println("Unique names: " + index.getUniqueNamesCount());
        System.out.println("Duplicate names: " + duplicates.size());

        int limit = Math.min(5, duplicates.size());
        for (int i = 0; i < limit; i++) {
            DuplicateGroup g = duplicates.get(i);
            System.out.println(g.getFileName() + " -> " + g.getCount());
        }
    }
}
