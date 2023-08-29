package com.fetcher.busines.service.GithubClient;

import com.fetcher.busines.service.dao.GithubFetcherDao;
import com.fetcher.model.BranchInfo;
import com.fetcher.model.Repository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class GithubRepositoriesClientImpl implements GithubFetcherDao {
    private final WebClient webClient;

    @Override
    public Mono<List<Repository>> findRepositoryInfoByUsername(String username, String authorization) {
        String userRepositories = String.format("/users/%s/repos", username);

        return webClient.get()
                .uri(userRepositories)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .bodyToFlux(Repository.class)
                .flatMap(repository -> findBranches(repository.getOwner().login(), repository.getName(), authorization)
                        .map(branches -> {
                            repository.setBranches(branches);
                            return repository;
                        }))
                .collectList();
    }

    private Mono<List<BranchInfo>> findBranches(String username, String repoName, String authorization) {
        String repositoriesInfo = String.format("/repos/%s/%s/branches", username, repoName);
        return webClient.get()
                .uri(repositoriesInfo)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .bodyToFlux(BranchInfo.class)
                .collectList();
    }


}
