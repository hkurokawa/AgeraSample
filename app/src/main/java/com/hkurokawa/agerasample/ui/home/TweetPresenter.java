package com.hkurokawa.agerasample.ui.home;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.google.android.agera.Result;
import com.google.android.agera.rvadapter.RepositoryPresenter;
import com.hkurokawa.agerasample.R;
import com.hkurokawa.agerasample.databinding.ListItemTweetBinding;

import java.util.List;

class TweetPresenter extends RepositoryPresenter<Result<List<Tweet>>> {
    @Override
    public int getItemCount(@NonNull Result<List<Tweet>> data) {
        if (data.succeeded()) {
            return data.get().size();
        }
        return 0;
    }

    @Override
    public int getLayoutResId(@NonNull Result<List<Tweet>> data, int index) {
        return R.layout.list_item_tweet;
    }

    @Override
    public void bind(@NonNull Result<List<Tweet>> data, int index, @NonNull RecyclerView.ViewHolder holder) {
        if (data.isAbsent() || data.failed()) {
            return;
        }
        final Tweet tweet = data.get().get(index);
        final ListItemTweetBinding binding = DataBindingUtil.bind(holder.itemView);
        binding.userName.setText(tweet.username);
        binding.tweet.setText(tweet.text);
    }
}
