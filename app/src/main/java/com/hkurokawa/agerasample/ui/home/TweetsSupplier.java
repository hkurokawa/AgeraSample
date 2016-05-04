package com.hkurokawa.agerasample.ui.home;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.agera.Result;
import com.google.android.agera.Supplier;
import com.google.android.agera.net.HttpFunctions;
import com.google.android.agera.net.HttpRequests;
import com.google.android.agera.net.HttpResponse;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.GuestCallback;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okio.Buffer;

class TweetsSupplier implements Supplier<Result<List<Tweet>>> {
    private static final String TAG = "TweetsSupplier";
//    private static final String BASE_URL = "https://api.twitter.com/1.1/statuses/home_timeline.json";
//    private final JsonAdapter<List<Tweet>> tweetJsonAdapter;

    public TweetsSupplier() {
//        tweetJsonAdapter = new Moshi.Builder().build().adapter(Types.newParameterizedType(List.class, Tweet.class));
    }

    @NonNull
    @Override
    public Result<List<Tweet>> get() {
        Twitter.getInstance().core.logInGuest(new Callback<AppSession>() {
            @Override
            public void success(com.twitter.sdk.android.core.Result<AppSession> result) {
                final AppSession session = result.data;
                final TwitterApiClient client = Twitter.getApiClient(session);
                client.getSearchService().tweets("#potatotips", null, null, null, null, 50, null, null, null, true, new GuestCallback<>(new Callback<Search>() {
                    @Override
                    public void success(com.twitter.sdk.android.core.Result<Search> result) {
                        final Search s = result.data;
                        for (com.twitter.sdk.android.core.models.Tweet tw : s.tweets) {
                            Log.d(TAG, tw.text);
                        }
                    }

                    @Override
                    public void failure(TwitterException exception) {

                    }
                }));
            }

            @Override
            public void failure(TwitterException exception) {

            }
        });
        final List<Tweet> ret = new ArrayList<>();
        ret.add(new Tweet("hydrakecat", "Hello, world!"));
        ret.add(new Tweet("hydrakecat", "Good bye, world!"));
        return Result.success(ret);
//        final String url = BASE_URL + "";
//        final HttpResponse response = HttpFunctions.httpFunction().apply(HttpRequests.httpGetRequest(url).compile()).get();
//        final Buffer buffer = new Buffer();
//        buffer.read(response.getBody());
//        try {
//            final List<Tweet> list = tweetJsonAdapter.fromJson(buffer);
//            return Result.success(list);
//        } catch (IOException e) {
//            Log.e(TAG, "Failed to parse the response.", e);
//            return Result.failure(e);
//        }
    }
}
