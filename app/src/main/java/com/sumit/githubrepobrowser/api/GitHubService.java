package com.sumit.githubrepobrowser.api;

import com.sumit.githubrepobrowser.model.repolistresult.GitHubRepo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by sahoos16 on 2/1/2018.
 */

public interface GitHubService {
    @GET("users/{user}/repos")
    Observable<List<GitHubRepo>> listRepos(@Path("user") String userName);
}