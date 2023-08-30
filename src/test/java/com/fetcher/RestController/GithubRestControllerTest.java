package com.fetcher.RestController;

import com.fetcher.busines.service.GitHubRepoFetcherService;
import com.fetcher.exception.ErrorResponse;
import com.fetcher.exception.GlobalExceptionRestHandler;
import com.fetcher.exception.badHeaderException;
import com.fetcher.model.BranchInfo;
import com.fetcher.model.Commit;
import com.fetcher.model.Owner;
import com.fetcher.model.Repository;
import com.github.jknack.handlebars.internal.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springdoc.api.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.fetcher.RestController.GithubRestController.API_GITHUB;
import static com.fetcher.RestController.GithubRestController.API_GITHUB_REPOS;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@WebFluxTest(GithubRestController.class)
class GithubRestControllerTest {
    @Autowired
    WebTestClient client;
    @MockBean
    private GitHubRepoFetcherService fetcher;


    @Test
    public void testGetRepositories() {
        String username = "arcziZajavka";
        Repository repository = new Repository();
        List<Repository> repositories = new java.util.ArrayList<>();
        BranchInfo branch = new BranchInfo("main", new Commit("abcdef1234567890", "https://github.com/myusername/my-repo/commit/abcdef1234567890"));
        repository.setName("my-repo");
        repository.setHtml_url("https://github.com/arcziZajavka/my-repo");
        repository.setFork(false);
        repository.setDescription("some description");
        repository.setOwner(new Owner("arcziZajavka"));
        repository.setBranches(List.of(branch));
        repositories.add(repository);
        String authorization = "Bearer yourAccessToken";
        String acceptHeader = "application/json";

        given(fetcher.fetchRepositoriesInfo(username, acceptHeader, authorization))
                .willReturn(Mono.just(repositories));


        client.get()
                .uri(API_GITHUB + API_GITHUB_REPOS, username)
                .header("Authorization", authorization)
                .header("Accept", APPLICATION_JSON_VALUE)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Repository.class)
                .hasSize(1)
                .consumeWith(response -> {
                    List<Repository> responseBody = response.getResponseBody();
                    Assertions.assertNotNull(responseBody);
                    Repository actualRepository = responseBody.get(0);
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(repository, actualRepository));
                });
    }

    @Test
    void thatThrowBadHeaderException() {
        String username = "ExistingUser";
        String authorization = "Bearer yourAccessToken";
        String acceptHeader = "application/xml";
        badHeaderException badHeaderException = new badHeaderException("'Accept: application/xml' is unsupported header");
        given(fetcher.fetchRepositoriesInfo(username, acceptHeader, authorization))
                .willThrow(badHeaderException);

        client.get()
                .uri(API_GITHUB + API_GITHUB_REPOS, username)
                .header("Authorization", authorization)
                .header("Accept", acceptHeader)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.NOT_ACCEPTABLE)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(ErrorResponse.class)
                .consumeWith(response -> {
                    ErrorResponse errorResponse = response.getResponseBody();
                    Assertions.assertNotNull(errorResponse);
                    Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE.value(), errorResponse.getStatus());
                    Assertions.assertEquals("'Accept: application/xml' is unsupported header", errorResponse.getMessage());
                });
    }

}