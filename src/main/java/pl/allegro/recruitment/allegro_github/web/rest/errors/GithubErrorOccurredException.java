package pl.allegro.recruitment.allegro_github.web.rest.errors;

public class GithubErrorOccurredException extends RuntimeException {

    public GithubErrorOccurredException(String message) {
        super(message);
    }
}
