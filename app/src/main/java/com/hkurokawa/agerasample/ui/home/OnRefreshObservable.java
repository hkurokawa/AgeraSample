package com.hkurokawa.agerasample.ui.home;

import android.support.v4.widget.SwipeRefreshLayout;

import com.google.android.agera.BaseObservable;

class OnRefreshObservable extends BaseObservable implements SwipeRefreshLayout.OnRefreshListener {
    @Override
    public void onRefresh() {
        dispatchUpdate();
    }
}
