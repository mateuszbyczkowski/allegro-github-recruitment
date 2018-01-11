package pl.allegro.recruitment.allegro_github.web.rest.errors;

public class GithubApiConnectionTimeoutException extends RuntimeException {

    public GithubApiConnectionTimeoutException(String message) {
        super(message);
    }
}
