package pl.allegro.recruitment.allegro_github.web.rest.errors;

public class GithubRepositoryOrUserNotFoundException extends RuntimeException {

    public GithubRepositoryOrUserNotFoundException(String message) {
        super(message);
    }
}
