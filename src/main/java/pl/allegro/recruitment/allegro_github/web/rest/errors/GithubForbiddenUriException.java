package pl.allegro.recruitment.allegro_github.web.rest.errors;

public class GithubForbiddenUriException extends RuntimeException {

    public GithubForbiddenUriException(String message) {
        super(message);
    }
}
