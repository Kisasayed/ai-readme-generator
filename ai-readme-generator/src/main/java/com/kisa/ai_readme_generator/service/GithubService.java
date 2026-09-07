package com.kisa.ai_readme_generator.service;
import java.util.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@Service
public class GithubService {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;

    public GithubService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        return headers;
    }

    public List<Map<String, Object>> getFileTree(String ownerRepo, String branch) {
        String url = "https://api.github.com/repos/" + ownerRepo + "/git/trees/" + branch + "?recursive=1";
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        return (List<Map<String, Object>>) response.getBody().get("tree");
    }

    public String getFileContent(String ownerRepo, String filePath) {
        String url = "https://api.github.com/repos/" + ownerRepo + "/contents/" + filePath;
        HttpEntity<Void> entity = new HttpEntity<>(buildHeaders());

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
        String base64Content = (String) response.getBody().get("content");

        byte[] decodedBytes = Base64.getDecoder().decode(base64Content.replace("\n", ""));
        return new String(decodedBytes);
    }
}