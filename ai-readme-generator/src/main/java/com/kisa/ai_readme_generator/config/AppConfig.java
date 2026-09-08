package com.kisa.ai_readme_generator.config;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
private List<String> findDependencyFiles(List<String> allFilePaths) {
    String language = detectLanguage(allFilePaths);

    Map<String, List<String>> dependencyFilesByLanguage = Map.of(
            "java",   List.of("pom.xml", "build.gradle"),
            "js",     List.of("package.json"),
            "py",     List.of("requirements.txt")
    );

    List<String> knownDependencyFiles = dependencyFilesByLanguage.getOrDefault(
            language, List.of()
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