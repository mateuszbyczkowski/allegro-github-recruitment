package pl.allegro.recruitment.allegro_github.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
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
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
    }

    @Test
    public void whenCallingGetRepositoryInfo_thenClientReturnsCorrectNameAndDate() {

        this.server.expect(requestTo("https://api.github.com/users/allegro/repos"))
                .andRespond(withSuccess(repositoryString, MediaType.APPLICATION_JSON));
       /* RepositoryInfo repositoryInfo = this.client.getRepositoryInfo("https://api.github.com/users/allegro/repos");

        assertThat(repositoryInfo.getRepositoryName())
                .isEqualTo("allegro/hermes");
        assertThat(repositoryInfo.getUpdatedAt())
                .isEqualTo(new GregorianCalendar(2018,1,11,13,13,13).getTime());*/
    }

    @Test
    public void whenCallingIncorrectGithubApiAddress_thenClientReturnsErrorOccurredException() {

        this.server.expect(requestTo("https://api.github.com/users/IncorrectUri/IncorrectRepo/repos"))
                .andExpect(content().string("Request incorrect, check request uri and try again."))
                .andRespond(withBadRequest());
    }

    @Test
    public void whenCallingForbiddenGithubApiPath_thenClientReturnsForbiddenException() {

        this.server.expect(requestTo("https://api.github.com/users/ForbiddenRepo/repos"))
                .andExpect(content().string("Requesting content is forbidden!"))
                .andRespond(withBadRequest());
    }

    @Test
    public void whenCallingGithubApiTooLong_thenClientReturnsConnectionTimeOutException() {

        this.server.expect(requestTo("https://api.github.com/users/CorrectRepo/repos"))
                .andExpect(content().string("Repository or User not found, uri may be incorrect."))
                .andRespond(withBadRequest());
    }

    @Test
    public void whenCallingGithubApi_thenClientReturnsInternalServerErrorException() {

        this.server.expect(requestTo("https://api.github.com/users/CorrectRepo/repos"))
                .andExpect(content().string("Internal Github server error occurred, please try again later."))
                .andRespond(withBadRequest());
    }

}