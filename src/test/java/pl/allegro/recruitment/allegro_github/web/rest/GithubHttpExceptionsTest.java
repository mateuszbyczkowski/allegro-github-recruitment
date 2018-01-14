package pl.allegro.recruitment.allegro_github.web.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import pl.allegro.recruitment.allegro_github.web.rest.errors.*;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;


@RunWith(SpringRunner.class)
@RestClientTest(GithubApiClient.class)
public class GithubHttpExceptionsTest {

    @Autowired
    private GithubApiClient client;

    @Autowired
    private MockRestServiceServer server;


    @Test(expected = GithubErrorOccurredException.class)
    public void shouldThrowErrorOccurredExceptionWhenBadRequest() {

        this.server.expect(requestTo("/users/IncorrectUri/IncorrectRepo/repos"))
                .andRespond(withBadRequest());

        client.getRepositoryInfo("/users/IncorrectUri/IncorrectRepo/repos");
        server.verify();
    }

    @Test (expected = GithubForbiddenUriException.class)
    public void shouldThrowGithubForbiddenUriExceptionWhenForbiddenRequest() {

        this.server.expect(requestTo("/users/ForbiddenRepo/repos"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        client.getRepositoryInfo("/users/ForbiddenRepo/repos");
        server.verify();
    }

    @Test (expected = GithubApiConnectionTimeoutException.class)
    public void shouldThrowGithubApiConnectionTimeoutExceptionWhenCorrectRequest() {

        this.server.expect(requestTo("/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.REQUEST_TIMEOUT));

        client.getRepositoryInfo("/users/CorrectRepo/repos");
        server.verify();
    }

    @Test (expected = GithubRepositoryOrUserNotFoundException.class)
    public void shouldThrowGithubRepositoryOrUserNotFoundExceptionWhenNotExistingRepository() {

        this.server.expect(requestTo("/users/NotExisting/repos"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        client.getRepositoryInfo("/users/NotExisting/repos");
        server.verify();
    }

    @Test (expected = GithubInternalServerErrorException.class)
    public void shouldThrowGithubInternalServerErrorExceptionWhenServerErrored() {

        this.server.expect(requestTo("/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        client.getRepositoryInfo("/users/CorrectRepo/repos");
        server.verify();
    }

}
