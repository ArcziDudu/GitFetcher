package com.fetcher.busines.service.GithubClient;

import com.fetcher.busines.service.GithubClient.configuration.WiremockConfig;
import com.fetcher.model.Repository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GithubRepositoriesClientImplTest extends WiremockConfig {
    @Autowired
    private GithubRepositoriesClientImpl fetcher;

    @Test
    public void testFindRepositoryInfoByUsername() {
        wireMockServer.stubFor(get(urlPathEqualTo("/users/arcziZajavka/repos"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("wiremock/wiremockRepos.json")));

        wireMockServer.stubFor(get(urlPathEqualTo("/repos/arcziZajavka/my-repo/branches"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("/wiremock/wiremockBranches.json")));
        Mono<List<Repository>> repositoryInfoByUsername = fetcher.findRepositoryInfoByUsername("arcziZajavka", "token");
        String expectedOwnerLogin = "arcziZajavka";

        StepVerifier.create(repositoryInfoByUsername)
                .expectNextMatches(repositoryList ->
                        repositoryList.stream().allMatch(repository ->
                                expectedOwnerLogin.equals(repository.getOwner().login())
                        ))
                .expectComplete()
                .verify();


    }


    @Test
    void testNotFoundWhenUsernameNotExists() {
        // given
        String userName = "notExistingUser";
        wireMockServer.stubFor(get(urlEqualTo("/users/" + userName + "/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"status\": 404, \"message\": \"user [" + userName + "] not found\"}")
                )
        );

        WebClientResponseException.NotFound exception = assertThrows(WebClientResponseException.NotFound.class, () -> {
            fetcher.findRepositoryInfoByUsername(userName, "some token").block();
        });

        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    void testUnauthorizedWhenBadToken() {
        // given
        String userName = "ExistingUser";
        wireMockServer.stubFor(get(urlEqualTo("/users/" + userName + "/repos"))
                .willReturn(
                        aResponse()
                                .withStatus(401)
                                .withHeader("Content-Type", "application/json")
                )
        );

        WebClientResponseException.Unauthorized exception = assertThrows(
                WebClientResponseException.Unauthorized.class,
                () -> fetcher.findRepositoryInfoByUsername(userName, "wrongToken").block()
        );
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

}