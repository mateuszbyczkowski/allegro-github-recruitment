package pl.allegro.recruitment.allegro_github.web.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import pl.allegro.recruitment.allegro_github.web.rest.errors.*;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

import java.util.Arrays;
import java.util.Comparator;

@Service
public class GithubApiClient {

    private final RestTemplate restTemplate;

    private RepositoryInfo[] repositories;

    public GithubApiClient(RestTemplateBuilder restTemplateBuilder) {

        restTemplate = restTemplateBuilder.build();
    }

    public RepositoryInfo getRepositoryInfo(String uri) {

        try {
            repositories = restTemplate.getForObject(uri, RepositoryInfo[].class);
        } catch (HttpStatusCodeException e) {
            throwExceptionForStatusCode(e.getStatusCode());
        }

        return getRecentUpdatedRepository(repositories);
    }

    private RepositoryInfo getRecentUpdatedRepository(RepositoryInfo[] repositoryInfos) {

        return Arrays.stream(repositoryInfos)
                .max(Comparator.comparing(RepositoryInfo::getUpdatedAt))
                .orElseThrow(() ->
                        new GithubEmptyArrayException("Unable to get latest repository data, processing object may be empty."));
    }

    private void throwExceptionForStatusCode(HttpStatus status) {
        switch (status) {
            case INTERNAL_SERVER_ERROR:
                throw new GithubInternalServerErrorException("Internal Github server error occurred, please try again later.");
            case BAD_REQUEST:
                throw new GithubErrorOccurredException("Request incorrect, check request uri and try again.");
            case FORBIDDEN:
                throw new GithubForbiddenUriException("Requesting content is forbidden!");
            case NOT_FOUND:
                throw new GithubRepositoryOrUserNotFoundException("Repository or User not found, uri may be incorrect.");
            case REQUEST_TIMEOUT:
                throw new GithubApiConnectionTimeoutException("Connection timeout.");
        }
    }

}
