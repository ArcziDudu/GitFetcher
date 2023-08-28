package com.fetcher.busines.service.dao;


import com.fetcher.model.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GithubFetcherDao {
    Mono<List<Repository>> findRepositoryInfoByUsername(String username);
}
