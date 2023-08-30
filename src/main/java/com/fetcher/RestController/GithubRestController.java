package com.fetcher.RestController;

import com.fetcher.busines.service.GitHubRepoFetcherService;
import com.fetcher.model.Repository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping(GithubRestController.API_GITHUB)
@AllArgsConstructor
public class GithubRestController {
    public static final String API_GITHUB = "/api";
    public static final String API_GITHUB_REPOS = "/repos/{username}";
    private final GitHubRepoFetcherService fetcher;

    @GetMapping(value = API_GITHUB_REPOS)
    public ResponseEntity<Mono<List<Repository>>> getRepositories(
            @PathVariable String username,
            @RequestHeader(value = "Authorization") String authorization,
            @RequestHeader(value = "Accept") String acceptHeader) {
        return ResponseEntity
                .ok(fetcher.fetchRepositoriesInfo(username, acceptHeader, authorization));
    }
}

