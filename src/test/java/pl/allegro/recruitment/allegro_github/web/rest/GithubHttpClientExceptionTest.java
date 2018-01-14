package pl.allegro.recruitment.allegro_github.web.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.net.SocketTimeoutException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@RestClientTest(GithubApiClient.class)
public class GithubHttpClientExceptionTest {

    @Autowired
    private GithubApiClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test(expected = HttpClientErrorException.class)
    public void shouldNotReturnGithubRepositoryDetailsWhenRepositoryIsNotFound() {

        server.expect(requestTo("/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        client.getRepositoryInfo("/users/CorrectRepo/repos");
        server.verify();
    }

    @Test(expected = HttpServerErrorException.class)
    public void shouldNotReturnGithubRepositoryDetailsWhenServerOccursAnError() {

        server.expect(requestTo("/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        client.getRepositoryInfo("/users/CorrectRepo/repos");
        server.verify();
    }

    @Test (expected = HttpClientErrorException.class)
    public void shouldNotReturnGithubRepositoryDetailsWhenRepositoryIsForbidden() {

        server.expect(requestTo("/users/ForbiddenRepo/repos"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        client.getRepositoryInfo("/users/ForbiddenRepo/repos");
        server.verify();
    }

    @Test (expected = HttpClientErrorException.class)
    public void shouldNotReturnGithubRepositoryDetailsWhenRequestIsBad() {

        server.expect(requestTo("/users/IncorrectUri/IncorrectRepo/repos"))
                .andRespond(withBadRequest());

        client.getRepositoryInfo("/users/IncorrectUri/IncorrectRepo/repos");
        server.verify();
    }

    @Test(expected = HttpClientErrorException.class)
    public void shouldNotReturnGithubRepositoryDetailsWhenRequestTimeout() {

        server.expect(requestTo("/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.REQUEST_TIMEOUT));

        client.getRepositoryInfo("/users/CorrectRepo/repos");
        server.verify();
    }

}