/**
 * Copyright (C) 2014 android10.org. All rights reserved.
 *
 * @author Fernando Cejas (the android10 coder)
 */
package com.nicklasslagbrand.baseline.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.nicklasslagbrand.baseline.di.components.UserComponent;
import com.nicklasslagbrand.baseline.model.UserModel;
import com.nicklasslagbrand.baseline.presentation.R;
import com.nicklasslagbrand.baseline.presenter.UserListPresenter;
import com.nicklasslagbrand.baseline.view.UserListView;
import com.nicklasslagbrand.baseline.view.adapter.UsersAdapter;
import com.nicklasslagbrand.baseline.view.adapter.UsersLayoutManager;
import java.util.Collection;
import javax.inject.Inject;

/**
 * Fragment that shows a list of Users.
 */
public class UserListFragment extends BaseFragment implements UserListView {

    @Inject UserListPresenter presenter;
    @Inject UsersAdapter adapter;

    @Bind(R.id.rv_users) RecyclerView rvUsers;
    @Bind(R.id.rl_progress) RelativeLayout rlProgress;
    @Bind(R.id.rl_retry) RelativeLayout rlRetry;
    @Bind(R.id.bt_retry) Button btnRetry;

    private UserListListener userListListener;

    public UserListFragment() {
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof UserListListener) {
            userListListener = (UserListListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getComponent(UserComponent.class)
            .inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View fragmentView = inflater.inflate(R.layout.fragment_user_list, container, false);
        ButterKnife.bind(this, fragmentView);
        setupRecyclerView();
        return fragmentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.setView(this);
        if (savedInstanceState == null) {
            loadUserList();
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
        rvUsers.setAdapter(null);
        ButterKnife.unbind(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.destroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        userListListener = null;
    }

    @Override
    public void showLoading() {
        rlProgress.setVisibility(View.VISIBLE);
        getActivity()
            .setProgressBarIndeterminateVisibility(true);
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
    public void renderUserList(Collection<UserModel> userModelCollection) {
        if (userModelCollection != null) {
            adapter.setUsersCollection(userModelCollection);
        }
    }

    @Override
    public void viewUser(UserModel userModel) {
        if (userListListener != null) {
            userListListener.onUserClicked(userModel);
        }
    }

    @Override
    public void showError(String message) {
        showToastMessage(message);
    }

    @Override
    public Context context() {
        return getActivity().getApplicationContext();
    }

    private void setupRecyclerView() {
        adapter.setOnItemClickListener(onItemClickListener);
        rvUsers.setLayoutManager(new UsersLayoutManager(context()));
        rvUsers.setAdapter(adapter);
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        presenter.initialize();
    }

    @OnClick(R.id.bt_retry)
    void onButtonRetryClick() {
        loadUserList();
    }

    private UsersAdapter.OnItemClickListener onItemClickListener = new UsersAdapter.OnItemClickListener() {
        @Override
        public void onUserItemClicked(UserModel userModel) {
            if (presenter != null && userModel != null) {
                presenter.onUserClicked(userModel);
            }
        }
    };

    /**
     * Interface for listening user list events.
     */
    public interface UserListListener {
        void onUserClicked(final UserModel userModel);
    }

}
