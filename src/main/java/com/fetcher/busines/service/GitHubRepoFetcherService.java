package com.fetcher.busines.service;


import com.fetcher.busines.service.dao.GithubFetcherDao;
import com.fetcher.exception.badHeaderException;
import com.fetcher.model.Repository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@AllArgsConstructor
@Log4j2
public class GitHubRepoFetcherService {
    private final GithubFetcherDao githubFetcherDao;

    public Mono<List<Repository>> fetchRepositoriesInfo(String username, String acceptHeader, String authorization) {
        if (acceptHeader.equals("application/xml")) {
            throw new badHeaderException("'Accept: application/xml' is unsupported header");
        }
        Mono<List<Repository>> repositoryInfoByUsername = githubFetcherDao.findRepositoryInfoByUsername(username, authorization);
        return repositoryInfoByUsername.flatMap(repositoryList -> {
            int repositoryCount = repositoryList.size();
            log.info("Found [{}] repositories for [{}] user in GitHub repositories", repositoryCount, username);
            return repositoryInfoByUsername;
        });
    }


}
