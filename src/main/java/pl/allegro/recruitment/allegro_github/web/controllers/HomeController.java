package pl.allegro.recruitment.allegro_github.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.allegro.recruitment.allegro_github.web.rest.GithubApiClient;
import pl.allegro.recruitment.allegro_github.web.rest.constants.ApplicationConstants;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

@Controller
public class HomeController {

    private final GithubApiClient githubApiClient;
    private final String uri;

    public HomeController(GithubApiClient githubApiClient) {

        this.githubApiClient = githubApiClient;
        this.uri = ApplicationConstants.GITHUB_API_USERS_URI + "allegro/repos";
    }

    @RequestMapping("/")
    public String index(Model model) {

        RepositoryInfo repositoryInfo = githubApiClient.getRepositoryInfo(uri);
        model.addAttribute("repository", repositoryInfo);

        return "index";
    }
}
