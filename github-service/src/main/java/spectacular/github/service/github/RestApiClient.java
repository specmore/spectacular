package spectacular.github.service.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.github.service.github.domain.Repository;

public class RestApiClient {
    private static final String REPO_CONTENT_PATH = "/repos/{repo}/contents/{path}";
    private static final String RAW_CONTENT_ACCEPT_HEADER = "application/vnd.github.3.raw";

    private final RestTemplate restTemplate;

    public RestApiClient(@Value("${github.api.root-url}") String rootUrl, RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.rootUri(rootUrl).build();
    }

    public String getRepositoryContent(Repository repo, String path, String ref) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_CONTENT_PATH);
        if(ref != null && ref.length() > 0) uriComponentsBuilder.queryParam("ref", ref);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", RAW_CONTENT_ACCEPT_HEADER);
        //headers.setBearerAuth(authToken);
        HttpEntity entity = new HttpEntity(headers);

        String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner(), path).toUriString();
        ResponseEntity<String> response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }
}
