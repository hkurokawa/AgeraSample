package com.hkurokawa.agerasample.ui.home;

import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.android.agera.Repositories;
import com.google.android.agera.Repository;
import com.google.android.agera.Result;
import com.google.android.agera.Updatable;
import com.google.android.agera.rvadapter.RepositoryAdapter;
import com.hkurokawa.agerasample.R;
import com.hkurokawa.agerasample.databinding.ActivityMainBinding;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements Updatable {
    private SwipeRefreshLayout swipeRefreshLayout;
    private Repository<Result<List<Tweet>>> tweetsRepository;
    private ExecutorService networkExecutor;
    private RepositoryAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        // OnRefresh observable
        final OnRefreshObservable refreshObservable = new OnRefreshObservable();
        swipeRefreshLayout = binding.refreshLayout;
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
        swipeRefreshLayout.setOnRefreshListener(refreshObservable);

        networkExecutor = Executors.newSingleThreadExecutor();

        // Tweets supplier and repository
        final TweetsSupplier tweetsSupplier = new TweetsSupplier();
        tweetsRepository = Repositories
                .repositoryWithInitialValue(Result.<List<Tweet>>absent())
                .observe(refreshObservable)
                .onUpdatesPerLoop()
                .goTo(networkExecutor)
                .thenGetFrom(tweetsSupplier)
                .compile();

        listAdapter = RepositoryAdapter.repositoryAdapter()
                .add(tweetsRepository, new TweetPresenter())
                .build();
        final RecyclerView list = binding.list;
        list.setAdapter(listAdapter);
        list.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.startObserving();
        tweetsRepository.addUpdatable(this);

        // Start loading when resumed
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                update();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        listAdapter.stopObserving();
        tweetsRepository.removeUpdatable(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkExecutor.shutdown();
    }

    @Override
    public void update() {
        if (tweetsRepository.get().isAbsent()) {
            swipeRefreshLayout.setRefreshing(true);
        } else if (tweetsRepository.get().failed()) {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
