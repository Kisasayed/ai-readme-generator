package com.kisa.ai_readme_generator.service; // match your actual package

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class FileSelectorService {
    // the 3 methods we wrote
}
@Service
public class FileSelectorService {

    private String getExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot == -1) return null;
        return path.substring(lastDot + 1);
    }

    private String detectLanguage(List<String> allFilePaths) {
        Map<String, Integer> extensionCounts = new HashMap<>();

        for (String path : allFilePaths) {
            String extension = getExtension(path);
            if (extension != null) {
                extensionCounts.merge(extension, 1, Integer::sum);
            }
        }

        return extensionCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private List<String> findDependencyFiles(List<String> allFilePaths) {
        List<String> knownDependencyFiles = List.of(
                "pom.xml", "build.gradle", "package.json", "requirements.txt"
        );

        List<String> matches = new ArrayList<>();
        for (String path : allFilePaths) {
            for (String knownFile : knownDependencyFiles) {
                if (path.endsWith(knownFile)) {
                    matches.add(path);
                }
            }
        }
        return matches;
    }
}