package com.fetcher.RestController;

import com.fetcher.busines.service.GitHubRepoFetcherService;
import com.fetcher.model.Repository;
import io.restassured.RestAssured;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import wiremock.com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.web.servlet.function.RequestPredicates.contentType;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GithubRestControllerTest {
    @MockBean
    private GitHubRepoFetcherService fetcher;
    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;

    }

//    @Test
//    public void testGetRepositories() {
//        String username = "testUser";
//        String authorization = "Bearer token";
//        String acceptHeader = "application/json";
//        String expectedJson = "/RestAssuredResponse.json";
//        ObjectMapper objectMapper = new ObjectMapper();
//
//
//        RestAssured.given()
//                .header("Authorization", authorization)
//                .header("Accept", acceptHeader)
//                .when()
//                .get("/fetcher/api/repos/{username}", username)
//                .then()
//                .statusCode(200)
//                .header(contentType(MediaType.APPLICATION_JSON))
//                .body("block()", instanceOf(Repository.class));
//    }
}