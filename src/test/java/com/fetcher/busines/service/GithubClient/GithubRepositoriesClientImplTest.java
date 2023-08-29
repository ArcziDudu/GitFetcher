package com.fetcher.busines.service.GithubClient;

import com.fetcher.busines.service.GithubClient.configuration.WiremockRestAssuredConfig;
import com.fetcher.exception.ErrorResponse;
import com.fetcher.model.Repository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertThrows;

class GithubRepositoriesClientImplTest extends WiremockRestAssuredConfig {
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
                )
        );

        WebClientResponseException.NotFound exception = assertThrows(WebClientResponseException.NotFound.class, () -> {
            fetcher.findRepositoryInfoByUsername(userName, "some token").block();
        });
        Assertions.assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
//        ErrorResponse errorResponse = exception.getResponseBodyAs(ErrorResponse.class);
//        Assertions.assertNotNull(errorResponse);
//        Assertions.assertEquals(404, errorResponse.getStatus());
//        Assertions.assertEquals("user [notExistingUser] not found", errorResponse.getMessage());
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
//        ErrorResponse errorResponse = exception.getResponseBodyAs(ErrorResponse.class);
//        Assertions.assertNotNull(errorResponse);
//        Assertions.assertEquals(401, errorResponse.getStatus());
//        Assertions.assertEquals("Unauthorized from external api. Please contact with project owner: artur.augustyn26@gmail.com", errorResponse.getMessage());
    }

}