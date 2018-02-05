package com.sumit.githubrepobrowser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.common.base.Strings;
import com.sumit.githubrepobrowser.adapter.RepositoryAdapter;
import com.sumit.githubrepobrowser.api.GitHubClient;
import com.sumit.githubrepobrowser.application.MyApplication;
import com.sumit.githubrepobrowser.dao.OwnerDAO;
import com.sumit.githubrepobrowser.model.repolistresult.GitHubRepo;
import com.sumit.githubrepobrowser.model.repolistresult.Owner;
import com.tapadoo.alerter.Alerter;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RepoSearchActivity extends AppCompatActivity {

    private static final String TAG = RepoSearchActivity.class.getCanonicalName();
    private static final int ALERTER_DURATION = 8000;

    private Context context;
    private CoordinatorLayout coordinatorLayout;
    private SearchView searchView;
    private MenuItem menuItemSearch;
    private ImageView imageViewGitHubAvatar;
    private FloatingActionButton fabSearch;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private LinearLayout linearLayoutPlaceholder;
    private LottieAnimationView lottieAnimationView;

    private Box<GitHubRepo> gitHubRepoBox;
    private Box<Owner> ownerBox;

    private Disposable disposable;

    private RecyclerView recyclerView;
    private RepositoryAdapter repositoryAdapter;

    private String profileUrl;
    private boolean isDataFromApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_search);

        context = this;

        initViews();
        customizeViews();
        setupEventHandlers();
        initObjectBox();

    }

    @SuppressLint("NewApi")
    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        coordinatorLayout = findViewById(R.id.coordinator_layout);
        fabSearch = findViewById(R.id.fab);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageViewGitHubAvatar = findViewById(R.id.image_github_avatar);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = findViewById(R.id.app_bar_layout);

        linearLayoutPlaceholder = findViewById(R.id.layout_placeholder);
        lottieAnimationView = findViewById(R.id.lottie_animation_view);
    }

    private void customizeViews() {
        /*Glide.with(context)
                .load(R.drawable.ic_github)
                .apply(new RequestOptions().centerInside())
                .into(imageViewGitHubAvatar);*/

        // Disable scroll, enable it after repo results are fetched
        appBarLayout.setExpanded(false);
        toggleAppBarLayoutScroll(false);
    }

    private void setupEventHandlers() {

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Strings.isNullOrEmpty(searchView.getQuery().toString())) {
                    searchView.clearFocus();
                    updateGitHubList(searchView.getQuery().toString());
                }
            }
        });

        imageViewGitHubAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Strings.isNullOrEmpty(profileUrl)) {
                    Util.launchChromeCustomTab(context, profileUrl);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        menuItemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) menuItemSearch.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!Strings.isNullOrEmpty(searchView.getQuery().toString())) {
                    updateGitHubList(searchView.getQuery().toString());
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_about) {
            // Show about app info
            showAboutMessage();
        } else if (id == R.id.action_clear_db) {

            if (gitHubRepoBox != null && ownerBox != null) {

                // Remove all data from GitHub and Owner Box

                gitHubRepoBox.removeAll();
                ownerBox.removeAll();

                // Show success message

                Alerter.create(this)
                        .setTitle(getString(R.string.success))
                        .setText(getString(R.string.msg_db_cleared))
                        .setBackgroundColorRes(R.color.md_green_400)
                        .show();
            }
        }else if(id == R.id.action_view_source){
            Util.launchChromeCustomTab(context, "https://github.com/sumitsahoo/GitHubRepoBrowser");
        }

        return super.onOptionsItemSelected(item);
    }

    private void initObjectBox() {
        BoxStore boxStore = ((MyApplication) getApplication()).getBoxStore();

        // Below boxes will be use to do CRUID operation
        gitHubRepoBox = boxStore.boxFor(GitHubRepo.class);
        ownerBox = boxStore.boxFor(Owner.class);
    }

    private void refreshAdapter(List<GitHubRepo> gitHubRepos) {

        if (repositoryAdapter == null) {
            repositoryAdapter = new RepositoryAdapter();
            recyclerView.setAdapter(repositoryAdapter);
        }

        repositoryAdapter.feedRepoList(gitHubRepos);
    }

    private void showRepoOwnerDetails(Owner owner) {

        if (!Strings.isNullOrEmpty(owner.avatarUrl)) {
            Glide.with(context)
                    .load(owner.avatarUrl)
                    .apply(new RequestOptions().centerCrop()
                            .placeholder(R.drawable.ic_github))
                    .into(imageViewGitHubAvatar);

            appBarLayout.setExpanded(true, true);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_github)
                    .apply(new RequestOptions().centerInside())
                    .into(imageViewGitHubAvatar);
        }

        // User can navigate to GitHub repo owner profile by clicking on avatar image
        profileUrl = owner.htmlUrl;

        collapsingToolbarLayout.setTitle(owner.login);
    }

    private void updateGitHubList(String userName) {

        togglePlaceholderLayout(false);
        toggleLottieAnimation(true);

        disposable = fetchGitHubRepoList(userName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<GitHubRepo>>() {
                    @Override
                    public void accept(List<GitHubRepo> gitHubRepos) throws Exception {
                        if (gitHubRepos != null && gitHubRepos.size() > 0) {

                            // Close search view
                            searchView.setIconified(true);
                            searchView.clearFocus();
                            menuItemSearch.collapseActionView();

                            if (isDataFromApi) {
                                // Save in DB for offline search
                                saveResponse(gitHubRepos);
                            }

                            // Data either from DB or API
                            refreshAdapter(gitHubRepos);

                            // Taking first record as owner detail will remain same for all his repos
                            showRepoOwnerDetails(gitHubRepos.get(0).ownerToOne.getTarget());

                            toggleLottieAnimation(false);

                        } else {
                            // List is empty

                            showErrorMessage(getString(R.string.msg_other_error));
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        // Mostly network error

                        if(throwable.toString().contains(getString(R.string.exception_unknown_host))){
                            showErrorMessage(getString(R.string.msg_network_error));
                        }else {
                            showErrorMessage(getString(R.string.msg_other_error));
                        }

                    }
                });
    }


    private Observable<List<GitHubRepo>> fetchGitHubRepoList(String userName) {

        // Check DB for existing data

        List<GitHubRepo> gitHubRepos = OwnerDAO.getRepoListByUserName(ownerBox, userName);

        if (gitHubRepos == null) {
            isDataFromApi = true;
            // Fetch data from GitHub API
            return GitHubClient.getInstance()
                    .getRepoList(userName);
        } else {
            isDataFromApi = false;
            // Return DB result
            return Observable.just(gitHubRepos);
        }

    }

    private void saveResponse(List<GitHubRepo> gitHubRepos) {

        dispose();

        toggleLottieAnimation(true);
        disposable = saveRepoListInDb(gitHubRepos)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isDbOperationSuccess) throws Exception {
                        // Records inserted
                        Log.e(TAG, "DB Operation" + isDbOperationSuccess);
                        toggleLottieAnimation(false);
                    }
                });
    }

    private Observable<Boolean> saveRepoListInDb(List<GitHubRepo> gitHubRepos) {
        for (GitHubRepo gitHubRepo : gitHubRepos) {

            // Owner record needs to be inserted first so that it can be referred from RepoBox
            ownerBox.put(gitHubRepo.owner);

            // Set target ToOne (Gson can't save to ToOne fields so need to copy from dummy Gson field)
            if (gitHubRepo.owner != null)
                gitHubRepo.ownerToOne.setTarget(gitHubRepo.owner);
            if (gitHubRepo.license != null)
                gitHubRepo.licenseToOne.setTarget(gitHubRepo.license);

        }

        // Insert records
        gitHubRepoBox.put(gitHubRepos);

        return Observable.just(true);

    }

    private void showAboutMessage() {

        // Show About App & Developer Info

        Alerter.create(this)
                .setTitle(getString(R.string.app_name))
                .setText(getString(R.string.msg_app_info))
                .setBackgroundColorRes(R.color.colorPrimary)
                .setDuration(ALERTER_DURATION)
                .show();
    }

    private void showErrorMessage(String errorMessage) {

        // Network or any other error
        // Reset views and show alert message

        toggleLottieAnimation(false);
        togglePlaceholderLayout(true);
        refreshAdapter(null);
        collapsingToolbarLayout.setTitle(getString(R.string.app_name));

        Alerter.create(this)
                .setTitle(getString(R.string.error))
                .setText(errorMessage)
                .setBackgroundColorRes(R.color.md_red_400)
                .show();
    }

    private void togglePlaceholderLayout(boolean isShow) {

        // Show/Hide placeholder images

        if (isShow) {
            appBarLayout.setExpanded(false, true);
            linearLayoutPlaceholder.setVisibility(View.VISIBLE);
            toggleAppBarLayoutScroll(false);
        } else {
            linearLayoutPlaceholder.setVisibility(View.GONE);
            toggleAppBarLayoutScroll(true);
        }
    }

    private void toggleAppBarLayoutScroll(final boolean isEnable) {

        // Enable/Disable AppBarLayout Drag to reveal avatar image
        // Enable drag only when valid response is fetched from API and repo owner info is available

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();

        if (behavior == null) {
            params.setBehavior(new AppBarLayout.Behavior());
            behavior = (AppBarLayout.Behavior) params.getBehavior();
        }

        behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return isEnable;
            }
        });

    }


    private void toggleLottieAnimation(boolean isShowAnimation) {

        // Show/Hide Lottie loader animation

        if (isShowAnimation) {

            appBarLayout.setExpanded(false);
            collapsingToolbarLayout.setTitle(getString(R.string.app_name));

            lottieAnimationView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            if (lottieAnimationView.isAnimating()) {
                lottieAnimationView.clearAnimation();
            }
            lottieAnimationView.setAnimation("loader.json");
            lottieAnimationView.loop(true);
            lottieAnimationView.playAnimation();
        } else {

            lottieAnimationView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            if (lottieAnimationView.isAnimating())
                lottieAnimationView.pauseAnimation();
            lottieAnimationView.clearAnimation();
        }
    }

    private void dispose() {

        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        dispose();
        toggleLottieAnimation(false);
    }

}
