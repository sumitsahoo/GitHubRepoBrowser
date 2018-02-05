package com.sumit.githubrepobrowser.dao;

import com.sumit.githubrepobrowser.model.repolistresult.GitHubRepo;
import com.sumit.githubrepobrowser.model.repolistresult.Owner;
import com.sumit.githubrepobrowser.model.repolistresult.Owner_;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

/**
 * Created by sahoos16 on 2/1/2018.
 */

public class OwnerDAO {

    public static List<GitHubRepo> getRepoListByUserName(Box<Owner> ownerBox, String username){

        QueryBuilder<Owner> ownerQueryBuilder = ownerBox.query();
        ownerQueryBuilder.contains(Owner_.login, username);

        Owner owner = ownerQueryBuilder.build().findFirst();

        if(owner != null) {

            // Retrieval is super easy because of @Backlink
            // Owner can have multiple Repos, hence 1:N relation i.e. ToMany
            // A Repo can have one Owner, hence 1:1 relation i.e. ToOne
            // Read more at : https://piercezaifman.com/objectbox-android-database/

            List<GitHubRepo> gitHubRepos = owner.gitHubRepos;

            if(gitHubRepos != null && gitHubRepos.size() > 0)
                return gitHubRepos;
        }

        return null;
    }
}
