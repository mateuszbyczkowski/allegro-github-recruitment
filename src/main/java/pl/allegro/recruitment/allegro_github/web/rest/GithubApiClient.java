package pl.allegro.recruitment.allegro_github.web.rest;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.allegro.recruitment.allegro_github.web.rest.errors.*;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

import java.util.Arrays;
import java.util.Comparator;


@Service
public class GithubApiClient {

    private final RestTemplate restTemplate;

    public GithubApiClient(RestTemplateBuilder restTemplateBuilder) {

        restTemplate = restTemplateBuilder.build();
    }

    public RepositoryInfo getRepositoryInfo(String uri) {

        RepositoryInfo[] repositories = restTemplate.getForObject(uri, RepositoryInfo[].class);

        return getRecentUpdatedRepository(repositories);
    }

    private RepositoryInfo getRecentUpdatedRepository(RepositoryInfo[] repositoryInfos) {

        return Arrays.stream(repositoryInfos)
                .max(Comparator.comparing(RepositoryInfo::getUpdatedAt))
                .orElseThrow(() ->
                        new GithubEmptyArrayException("Unable to get latest repository data, processing object may be empty."));
    }

}
