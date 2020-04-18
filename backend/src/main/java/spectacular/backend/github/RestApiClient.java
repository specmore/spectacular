package spectacular.backend.github;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import spectacular.backend.common.Repository;
import spectacular.backend.github.app.AppInstallationAuthenticationHeaderRequestInterceptor;
import spectacular.backend.github.domain.ContentItem;
import spectacular.backend.github.domain.SearchCodeResults;
import spectacular.backend.github.graphql.GraphQLRequest;
import spectacular.backend.github.graphql.GraphQLResponse;

import java.time.Instant;
import java.util.StringJoiner;

@Component
public class RestApiClient {
    private static final String GRAPH_QL = "/graphql";
    private static final String RATE_LIMIT = "/rate_limit";
    private static final String SEARCH_CODE_PATH = "/search/code";
    private static final String REPO_PATH = "/repos/{repo}";
    private static final String REPO_CONTENT_PATH = "/repos/{repo}/contents/{path}";
    private static final String REPO_COLLABORATORS_PATH = "/repos/{repo}/collaborators/{username}";
    private static final String RAW_CONTENT_ACCEPT_HEADER = "application/vnd.github.3.raw";

    private final RestTemplate restTemplate;

    public RestApiClient(@Value("${github.api.root-url}") String rootUrl, RestTemplateBuilder restTemplateBuilder, HttpComponentsClientHttpRequestFactory requestFactory, AppInstallationAuthenticationHeaderRequestInterceptor appInstallationAuthenticationHeaderRequestInterceptor) {
        this.restTemplate = restTemplateBuilder
                .rootUri(rootUrl)
                .requestFactory(() -> requestFactory)
                .additionalInterceptors(appInstallationAuthenticationHeaderRequestInterceptor)
                .build();
    }

    public ContentItem getRepositoryContent(Repository repo, String path, String ref) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_CONTENT_PATH);
        if(ref != null && ref.length() > 0) uriComponentsBuilder.queryParam("ref", ref);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity entity = new HttpEntity(headers);

        String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner(), path).toUriString();
        var response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, ContentItem.class);

        var contentItem = response.getBody();
        var lastModified = Instant.ofEpochMilli(response.getHeaders().getLastModified());
        contentItem.setLastModified(lastModified);

        return contentItem;
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

    public spectacular.backend.github.domain.Repository getRepository(Repository repo) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(REPO_PATH);

        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);

        String contentUri = uriComponentsBuilder.buildAndExpand(repo.getNameWithOwner()).toUriString();
        ResponseEntity<spectacular.backend.github.domain.Repository> response = restTemplate.exchange(contentUri, HttpMethod.GET, entity, spectacular.backend.github.domain.Repository.class);
        return response.getBody();
    }

    public GraphQLResponse graphQLQuery(GraphQLRequest graphQLRequest) {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<GraphQLRequest> entity = new HttpEntity<>(graphQLRequest, headers);

        ResponseEntity<GraphQLResponse> response = restTemplate.postForEntity(GRAPH_QL, entity, GraphQLResponse.class);
        return response.getBody();
    }

    public String getRateLimit() {
        final String response = restTemplate.getForObject(RATE_LIMIT, String.class);
        return response;
    }
}