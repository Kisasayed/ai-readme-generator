package com.kisa.ai_readme_generator.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class FileSelectorService {

    public List<String> selectImportantFiles(List<Map<String, Object>> allFiles) {
        // will call the helper methods below - not built yet
        return null;
    }

    private String getExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot == -1) return null;
        return path.substring(lastDot + 1);
    }

    private String detectLanguage(List<Map<String, Object>> allFiles) {
        Map<String, Integer> extensionCounts = new HashMap<>();

        for (Map<String, Object> file : allFiles) {
            String path = (String) file.get("path");
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

    private List<String> findDependencyFiles(List<Map<String, Object>> allFiles) {
        List<String> knownDependencyFiles = List.of(
                "pom.xml", "build.gradle", "package.json", "requirements.txt"
        );

        List<String> matches = new ArrayList<>();
        for (Map<String, Object> file : allFiles) {
            String path = (String) file.get("path");
            for (String knownFile : knownDependencyFiles) {
                if (path.endsWith(knownFile)) {
                    matches.add(path);
                }
            }
        }
        return matches;
    }

    private String findEntryPoint(List<Map<String, Object>> allFiles) {
        List<String> commonEntryPointNames = List.of(
                "Application.java", "Main.java", "main.py", "index.js", "app.js"
        );

        for (Map<String, Object> file : allFiles) {
            String path = (String) file.get("path");
            for (String name : commonEntryPointNames) {
                if (path.endsWith(name)) {
                    return path;
                }
            }
        }
        return null;
    }

    private String findReadme(List<Map<String, Object>> allFiles) {
        for (Map<String, Object> file : allFiles) {
            String path = (String) file.get("path");
            if (path.toLowerCase().endsWith("readme.md")) {
                return path;
            }
        }
        return null;
    }

    private List<String> findLargestSourceFiles(List<Map<String, Object>> allFiles) {
        return null; // not built yet
    }
}