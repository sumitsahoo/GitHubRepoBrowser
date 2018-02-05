package com.sumit.githubrepobrowser.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.sumit.githubrepobrowser.R;
import com.sumit.githubrepobrowser.Util;
import com.sumit.githubrepobrowser.model.repolistresult.GitHubRepo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by sahoos16 on 2/2/2018.
 */

public class RepositoryAdapter extends RecyclerView.Adapter<RepositoryAdapter.RepositoryViewHolder> {

    private List<GitHubRepo> gitHubRepos;
    private Context context;
    private SimpleDateFormat fromFormat, toFormat;

    public void feedRepoList(List<GitHubRepo> gitHubRepos) {
        this.gitHubRepos = gitHubRepos;
        notifyDataSetChanged();

        fromFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        toFormat = new SimpleDateFormat("dd MMM yyyy", Locale.US);

    }

    @Override
    public RepositoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.row_github_repo, parent, false);
        return new RepositoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RepositoryViewHolder holder, int position) {

        final GitHubRepo gitHubRepo = gitHubRepos.get(position);

        if (!Strings.isNullOrEmpty(gitHubRepo.language)) {
            holder.textViewLanguage.setText(gitHubRepo.language);
            holder.textViewLanguage.setVisibility(View.VISIBLE);
        } else {
            holder.textViewLanguage.setVisibility(View.GONE);
        }

        holder.textViewRepoName.setText(gitHubRepo.name);

        if (!Strings.isNullOrEmpty(gitHubRepo.description)) {
            // A repo might not have description, so if description is present then make the TextView visible
            holder.textViewRepoDescription.setVisibility(View.VISIBLE);
            holder.textViewRepoDescription.setText(gitHubRepo.description);
        } else {
            holder.textViewRepoDescription.setVisibility(View.GONE);
        }

        // Date format from API : "2016-05-15T16:33:23Z"

        String lastUpdated = Splitter.on("T").split(gitHubRepo.updatedAt).iterator().next();

        try {
            Date lastUpdateDate = fromFormat.parse(lastUpdated);
            holder.textViewRepoUpdateDate.setText(context.getString(R.string.updated_on) + toFormat.format(lastUpdateDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Chrome Custom Tab

                if (!Strings.isNullOrEmpty(gitHubRepo.htmlUrl)) {
                    Util.launchChromeCustomTab(context, gitHubRepo.htmlUrl);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (gitHubRepos != null)
            return gitHubRepos.size();
        return 0;
    }

    class RepositoryViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView textViewLanguage;
        TextView textViewRepoName;
        TextView textViewRepoDescription;
        TextView textViewRepoUpdateDate;
        ImageView imageViewRepoDetailBrowser;

        RepositoryViewHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_view);
            textViewLanguage = itemView.findViewById(R.id.text_language);
            textViewRepoName = itemView.findViewById(R.id.text_repo_name);
            textViewRepoDescription = itemView.findViewById(R.id.text_repo_description);
            textViewRepoUpdateDate = itemView.findViewById(R.id.text_repo_update_date);
            imageViewRepoDetailBrowser = itemView.findViewById(R.id.image_browse_repo);
        }
    }
}
