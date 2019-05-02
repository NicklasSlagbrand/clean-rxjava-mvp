/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.nicklasslagbrand.baseline.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.fernandocejas.arrow.checks.Preconditions;
import com.nicklasslagbrand.baseline.di.components.UserComponent;
import com.nicklasslagbrand.baseline.model.UserModel;
import com.nicklasslagbrand.baseline.presentation.R;
import com.nicklasslagbrand.baseline.presenter.UserDetailsPresenter;
import com.nicklasslagbrand.baseline.view.UserDetailsView;
import com.nicklasslagbrand.baseline.view.component.AutoLoadImageView;
import javax.inject.Inject;

/**
 * Fragment that shows details of a certain user.
 */
public class UserDetailsFragment extends BaseFragment implements UserDetailsView {
    private static final String PARAM_USER_ID = "param_user_id";

    @Inject UserDetailsPresenter presenter;

    @Bind(R.id.iv_cover) AutoLoadImageView ivCover;
    @Bind(R.id.tv_fullname) TextView tvFullname;
    @Bind(R.id.tv_email) TextView tvEmail;
    @Bind(R.id.tv_followers) TextView tvFollowers;
    @Bind(R.id.tv_description) TextView tvDescription;
    @Bind(R.id.rl_progress) RelativeLayout rlProgress;
    @Bind(R.id.rl_retry) RelativeLayout rlRetry;
    @Bind(R.id.bt_retry) Button bt_retry;

    public static UserDetailsFragment forUser(int userId) {
        final UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
        final Bundle arguments = new Bundle();
        arguments.putInt(PARAM_USER_ID, userId);
        userDetailsFragment.setArguments(arguments);

        return userDetailsFragment;
    }

    public UserDetailsFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent(UserComponent.class)
            .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_user_details, container, false);
        ButterKnife.bind(this, fragmentView);

        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setView(this);
        if (savedInstanceState == null) {
            loadUserDetails();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        presenter.pause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        presenter.destroy();
    }

    @Override
    public void renderUser(UserModel user) {
        if (user != null) {
            ivCover.setImageUrl(user.getCoverUrl());
            tvFullname.setText(user.getFullName());
            tvEmail.setText(user.getEmail());
            tvFollowers.setText(String.valueOf(user.getFollowers()));
            tvDescription.setText(user.getDescription());
        }
    }

    @Override
    public void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
        getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        rlProgress.setVisibility(View.GONE);
        getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void showRetry() {
        rlRetry.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        rlRetry.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        showToastMessage(message);
    }

    @Override
    public Context context() {
        return getActivity().getApplicationContext();
    }

    /**
     * Load user details.
     */
    private void loadUserDetails() {
        if (presenter != null) {
            presenter.initialize(currentUserId());
        }
    }

    /**
     * Get current user id from fragments arguments.
     */
    private int currentUserId() {
        final Bundle arguments = getArguments();
        Preconditions.checkNotNull(arguments, "Fragment arguments cannot be null");
        return arguments.getInt(PARAM_USER_ID);
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserDetails();
    }
}
