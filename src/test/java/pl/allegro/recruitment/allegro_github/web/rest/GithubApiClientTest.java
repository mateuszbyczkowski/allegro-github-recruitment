package pl.allegro.recruitment.allegro_github.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import pl.allegro.recruitment.allegro_github.web.rest.errors.GithubRepositoryOrUserNotFoundException;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.shouldHaveThrown;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@RestClientTest(GithubApiClient.class)
public class GithubApiClientTest {

    @Autowired
    private GithubApiClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    private String repositoryString;

    private String emptyRepositoryString;

    @Before
    public void setUp() throws Exception {

        repositoryString =
                objectMapper.writeValueAsString(new RepositoryInfo[]{
                        new RepositoryInfo(
                                "allegro/hermes",
                                new GregorianCalendar(2018,1,11,13,13,13).getTime()),
                        new RepositoryInfo(
                                "allegro/bigcache",
                                new GregorianCalendar(2015,1,11,19,0,0).getTime()),
                        new RepositoryInfo(
                                "allegro/vaas",
                                new GregorianCalendar(2017,5,30,9,54,33).getTime())
                });

        emptyRepositoryString = objectMapper.writeValueAsString(new RepositoryInfo[]{});
    }

    @Test
    public void whenCallingGetRepositoryInfo_thenClientReturnsCorrectNameAndDate() {

        this.server.expect(requestTo("https://api.github.com/users/allegro/repos"))
                .andRespond(withSuccess(repositoryString, MediaType.APPLICATION_JSON));
        RepositoryInfo repositoryInfo = this.client.getRepositoryInfo("https://api.github.com/users/allegro/repos");

        server.verify();

        assertThat(repositoryInfo.getRepositoryName())
                .isEqualTo("allegro/hermes");
        assertThat(repositoryInfo.getUpdatedAt())
                .isEqualTo(new GregorianCalendar(2018,1,11,13,13,13).getTime());
    }

    @Test
    public void whenCallingIncorrectGithubApiAddress_thenClientReturnsErrorOccurredException() {

        this.server.expect(requestTo("https://api.github.com/users/IncorrectUri/IncorrectRepo/repos"))
                .andRespond(withBadRequest());

        try{
            client.getRepositoryInfo("https://api.github.com/users/IncorrectUri/IncorrectRepo/repos");
            server.verify();
        }catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Request incorrect, check request uri and try again.");
        }
    }

    @Test
    public void whenCallingForbiddenGithubApiPath_thenClientReturnsForbiddenException() {

        this.server.expect(requestTo("https://api.github.com/users/ForbiddenRepo/repos"))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        try{
            client.getRepositoryInfo("https://api.github.com/users/ForbiddenRepo/repos");
            server.verify();
        }catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Requesting content is forbidden!");
        }
    }

    @Test
    public void whenCallingGithubApiTooLong_thenClientReturnsConnectionTimeOutException() {

        this.server.expect(requestTo("https://api.github.com/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.REQUEST_TIMEOUT));

        try{
            client.getRepositoryInfo("https://api.github.com/users/CorrectRepo/repos");
            server.verify();
        }catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Connection timeout.");
        }
    }

    @Test
    public void whenCallingGithubApiNotExistingRepository_thenClientReturnsNotFoundException() {

        this.server.expect(requestTo("https://api.github.com/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        try{
            client.getRepositoryInfo("https://api.github.com/users/CorrectRepo/repos");
            server.verify();
        }catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Repository or User not found, uri may be incorrect.");
        }
    }

/*    @Test
    public void whenCallingGithubApi_thenClientReturnsInternalServerErrorException() {

        this.server.expect(requestTo("https://api.github.com/users/CorrectRepo/repos"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        try{
            client.getRepositoryInfo("https://api.github.com/users/CorrectRepo/repos");
            server.verify();
        }catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Internal Github server error occurred, please try again later.");
        }
    }*/

    @Test
    public void whenCallingGithubApi_thenClientReturnsEmptyArrayAndThrowException() {

        this.server.expect(requestTo("https://api.github.com/users/CorrectRepo/repos"))
                .andRespond(withSuccess(emptyRepositoryString, MediaType.APPLICATION_JSON));

        try{
            client.getRepositoryInfo("https://api.github.com/users/CorrectRepo/repos");
            server.verify();
        }catch(Exception e) {
            assertThat(e.getMessage()).isEqualTo("Unable to get latest repository data, processing object may be empty.");
        }
    }

}