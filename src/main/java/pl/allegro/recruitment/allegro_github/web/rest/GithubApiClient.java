package pl.allegro.recruitment.allegro_github.web.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import pl.allegro.recruitment.allegro_github.web.rest.constants.ApplicationConstants;
import pl.allegro.recruitment.allegro_github.web.rest.errors.GithubApiConnectionTimeoutException;
import pl.allegro.recruitment.allegro_github.web.rest.errors.GithubEmptyArrayException;
import pl.allegro.recruitment.allegro_github.web.rest.errors.GithubErrorOccurredException;
import pl.allegro.recruitment.allegro_github.web.rest.errors.GithubRepositoryOrUserNotFoundException;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

import java.util.Arrays;
import java.util.Comparator;


@Service
public class GithubApiClient {

    private RepositoryInfo[] result;
    private final RestTemplate restTemplate;

    public GithubApiClient(RestTemplateBuilder restTemplateBuilder) {

        restTemplate = restTemplateBuilder.build();
    }

    public RepositoryInfo getRepositoryInfo(String uri) {

        try {
            result = restTemplate.getForObject(uri, RepositoryInfo[].class);
        } catch (HttpClientErrorException e) {
            HttpStatus statusCode = e.getStatusCode();

            switch (statusCode) {
                case BAD_REQUEST:
                    throw new GithubErrorOccurredException("Request incorrect, check request uri and try again.");
                case FORBIDDEN:
                    throw new GithubErrorOccurredException("Requesting content is forbidden!");
                case NOT_FOUND:
                    throw new GithubRepositoryOrUserNotFoundException("Repository or User not found, uri may be incorrect.");
                case REQUEST_TIMEOUT:
                    throw new GithubApiConnectionTimeoutException("Connection timeout.");
                case INTERNAL_SERVER_ERROR:
                    throw new GithubErrorOccurredException("Internal Github server error occurred, please try again later.");
            }
        }

        return getRecentUpdatedRepository(result);
    }

    private RepositoryInfo getRecentUpdatedRepository(RepositoryInfo[] repositoryInfos) {

        return Arrays.stream(repositoryInfos)
                .max(Comparator.comparing(RepositoryInfo::getUpdatedAt))
                .orElseThrow(() ->
                        new GithubEmptyArrayException("Unable to get latest repository data, processing object may be empty."));
    }
}
