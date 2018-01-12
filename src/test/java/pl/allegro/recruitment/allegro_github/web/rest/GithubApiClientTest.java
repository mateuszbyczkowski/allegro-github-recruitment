package pl.allegro.recruitment.allegro_github.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import pl.allegro.recruitment.allegro_github.web.rest.models.RepositoryInfo;

import java.time.LocalDate;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
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

    @Before
    public void setUp() throws Exception {
        String repositoryString =
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

        this.server.expect(requestTo("https://api.github.com/users/allegro/repos"))
                .andRespond(withSuccess(repositoryString, MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenCallingGetRepositoryInfo_thenClientReturnsCorrectNameAndDate() {
        RepositoryInfo repositoryInfo = this.client.getRepositoryInfo();

        assertThat(repositoryInfo.getRepositoryName()).isEqualTo("allegro/hermes");
        assertThat(repositoryInfo.getUpdatedAt()).isEqualTo(new GregorianCalendar(2018,1,11,13,13,13).getTime());
    }
}