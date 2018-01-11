package pl.allegro.recruitment.allegro_github.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.allegro.recruitment.allegro_github.web.rest.GithubApiClient;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

@Controller
public class HomeController {

    private final GithubApiClient githubApiClient;

    public HomeController(GithubApiClient githubApiClient) {
        this.githubApiClient = githubApiClient;
    }

    @RequestMapping("/")
    public String index(Model model) {
        RepositoryInfo repositoryInfo = githubApiClient.getRepositoryInfo();
        model.addAttribute("repository", repositoryInfo);

        return "index";
    }
}
