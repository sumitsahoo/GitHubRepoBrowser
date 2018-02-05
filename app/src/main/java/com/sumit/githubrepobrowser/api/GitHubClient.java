package com.sumit.githubrepobrowser.api;

import com.sumit.githubrepobrowser.model.repolistresult.GitHubRepo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by sahoos16 on 2/1/2018.
 */

public class GitHubClient {

    private static final String GITHUB_API_BASE_URL = "https://api.github.com";

    private static GitHubClient instance;
    private GitHubService gitHubService;

    private GitHubClient(){
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_API_BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        gitHubService = retrofit.create(GitHubService.class);

    }

    public static GitHubClient getInstance(){
        if(instance == null){
            instance = new GitHubClient();
        }

        return instance;
    }

    public Observable<List<GitHubRepo>> getRepoList(String userName){
        return gitHubService.listRepos(userName);
    }
}
