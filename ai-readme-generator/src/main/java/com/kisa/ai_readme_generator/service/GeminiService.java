package com.kisa.ai_readme_generator.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.List;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final RestTemplate restTemplate;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public GeminiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String generateReadme(String prompt) {
        int maxRetries = 3;
        long waitTime = 1000;

        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                Map response = restTemplate.postForObject(buildUrl(), buildRequestBody(prompt), Map.class);
                return extractText(response);
            } catch (HttpClientErrorException.TooManyRequests e) {
                String errorBody = e.getResponseBodyAsString();

                if (errorBody.contains("PerDay")) {
                    log.warn("Gemini daily quota exhausted");
                    return "Error: Daily Gemini API quota exhausted. Please try again tomorrow.";
                }

                log.warn("Gemini rate limit hit, retrying after {} ms", waitTime);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                waitTime *= 2;
            }
        }
        return "Error: Gemini API rate limit exceeded after multiple retries.";
    }

    private Map<String, Object> buildRequestBody(String prompt) {
        Map<String, Object> textPart = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        return Map.of("contents", List.of(content));
    }

    private String buildUrl() {
        return apiUrl + "?key=" + apiKey;
    }

    private String extractText(Map response) {
        List<Map> candidates = (List<Map>) response.get("candidates");
        Map firstCandidate = candidates.get(0);
        Map content = (Map) firstCandidate.get("content");
        List<Map> parts = (List<Map>) content.get("parts");
        Map firstPart = parts.get(0);
        return (String) firstPart.get("text");
    }
}