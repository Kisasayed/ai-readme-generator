package com.kisa.ai_readme_generator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromptBuilderService {

    private static final Logger log = LoggerFactory.getLogger(PromptBuilderService.class);

    private final GithubService githubService;

    public PromptBuilderService(GithubService githubService) {
        this.githubService = githubService;
    }

    public String buildPrompt(String ownerRepo, List<String> filePaths) {
        StringBuilder promptBuilder = new StringBuilder();

        promptBuilder.append("You are an assistant that writes professional README.md files for GitHub repositories.\n\n");

        for (String filePath : filePaths) {
            try {
                String content = githubService.getFileContent(ownerRepo, filePath);

                if (content.length() > 2000) {
                    content = content.substring(0, 2000) + "\n... (truncated)";
                }

                promptBuilder.append("File: ").append(filePath).append("\n");
                promptBuilder.append(content).append("\n\n");

            } catch (Exception e) {
                log.warn("Skipping file due to error: " + filePath);
            }
        }

        promptBuilder.append("Now generate a complete README.md file in Markdown format based on the above files.");

        return promptBuilder.toString();
    }
}