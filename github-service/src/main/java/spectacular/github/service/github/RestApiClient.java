package spectacular.github.service.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.github.service.github.app.AppInstallationAuthenticationHeaderRequestInterceptor;
import spectacular.github.service.common.Repository;
import spectacular.github.service.github.domain.SearchCodeResults;

import java.util.StringJoiner;

@Component
public class RestApiClient {
    private static final String SEARCH_CODE_PATH = "/search/code";
    private static final String REPO_CONTENT_PATH = "/repos/{repo}/contents/{path}";
    private static final String REPO_COLLABORATORS_PATH = "/repos/{repo}/collaborators/{username}";
    private static final String RAW_CONTENT_ACCEPT_HEADER = "application/vnd.github.3.raw";

    private final RestTemplate restTemplate;

    public RestApiClient(@Value("${github.api.root-url}") String rootUrl, RestTemplateBuilder restTemplateBuilder, AppInstallationAuthenticationHeaderRequestInterceptor appInstallationAuthenticationHeaderRequestInterceptor) {
        this.restTemplate = restTemplateBuilder.rootUri(rootUrl).additionalInterceptors(appInstallationAuthenticationHeaderRequestInterceptor).build();
    }

    public String getRepositoryContent(Repository repo, String path, String ref) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_CONTENT_PATH);
        if(ref != null && ref.length() > 0) uriComponentsBuilder.queryParam("ref", ref);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", RAW_CONTENT_ACCEPT_HEADER);
        HttpEntity entity = new HttpEntity(headers);

        String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner(), path).toUriString();
        ResponseEntity<String> response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }

    public SearchCodeResults findFiles(String filename, String fileExtension, String path, String org) {
        StringJoiner joiner = new StringJoiner("+");
        if(filename != null && filename.length() > 0) joiner.add("filename:"+filename);
        if(fileExtension != null && fileExtension.length() > 0) joiner.add("extension:"+fileExtension);
        if(path != null && path.length() > 0) joiner.add("path:"+path);
        if(org != null && org.length() > 0) joiner.add("org:"+org);
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(SEARCH_CODE_PATH).queryParam("q", joiner.toString());

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        ResponseEntity<SearchCodeResults> response = restTemplate.exchange(uriComponentsBuilder.build().toUriString(), HttpMethod.GET, entity, SearchCodeResults.class);
        return response.getBody();
    }

    public boolean isUserRepositoryCollaborator(Repository repo, String username) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_COLLABORATORS_PATH);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner(), username).toUriString();
        ResponseEntity<Void> response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, Void.class);
        return response.getStatusCode().is2xxSuccessful();
    }
}
