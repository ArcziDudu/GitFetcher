package com.fetcher.model;

import lombok.Data;

import java.util.List;

@Data
public class Repository {
    String name;
    String html_url;
    String description;
    boolean fork;
    Owner owner;
    List<BranchInfo> branches;

}
