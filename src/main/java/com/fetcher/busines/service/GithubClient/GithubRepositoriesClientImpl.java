package com.fetcher.busines.service.GithubClient;


import com.fetcher.busines.service.dao.GithubFetcherDao;
import com.fetcher.exception.NotFoundException;
import com.fetcher.model.BranchInfo;
import com.fetcher.model.Repository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GithubRepositoriesClientImpl implements GithubFetcherDao {
    private final WebClient webClient;

    @Override
    public Mono<List<Repository>> findRepositoryInfoByUsername(String username) {
        String userRepositories = String.format("/users/%s/repos", username);

        Flux<Repository> repositoryFlux = webClient.get()
                .uri(userRepositories)
                .retrieve()
                .bodyToFlux(Repository.class);

        Flux<Tuple2<Repository, List<BranchInfo>>> repositoryWithBranchesFlux = repositoryFlux
                .flatMap(repository -> findBranches(repository.getOwner().login(), repository.getName())
                        .map(branches -> Tuples.of(repository, branches)));

        return repositoryWithBranchesFlux
                .collectList()
                .map(repositoryWithBranchesList ->
                        repositoryWithBranchesList.stream()
                                .peek(tuple -> tuple.getT1().setBranches(tuple.getT2()))
                                .map(Tuple2::getT1)
                                .collect(Collectors.toList())
                );

    }

    private Mono<List<BranchInfo>> findBranches(String username, String repoName) {
        String RepositoriesInfo = String.format("/repos/%s/%s/branches", username, repoName);
        return webClient.get()
                .uri(RepositoriesInfo)
                .retrieve()
                .bodyToFlux(BranchInfo.class)
                .collectList();
    }
}
