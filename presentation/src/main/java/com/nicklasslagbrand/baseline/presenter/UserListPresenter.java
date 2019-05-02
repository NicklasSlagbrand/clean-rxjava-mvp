/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nicklasslagbrand.baseline.presenter;

import android.support.annotation.NonNull;
import com.nicklasslagbrand.baseline.data.exception.ErrorMessageFactory;
import com.nicklasslagbrand.baseline.di.PerActivity;
import com.nicklasslagbrand.baseline.domain.User;
import com.nicklasslagbrand.baseline.domain.exception.DefaultErrorBundle;
import com.nicklasslagbrand.baseline.domain.exception.ErrorBundle;
import com.nicklasslagbrand.baseline.domain.interactor.DefaultObserver;
import com.nicklasslagbrand.baseline.domain.interactor.GetUserList;
import com.nicklasslagbrand.baseline.mapper.UserModelDataMapper;
import com.nicklasslagbrand.baseline.model.UserModel;
import com.nicklasslagbrand.baseline.view.UserListView;
import java.util.Collection;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
@PerActivity
public class UserListPresenter implements Presenter {

    private UserListView view;

    private final GetUserList getUserListUseCase;
    private final UserModelDataMapper userModelDataMapper;

    @Inject
    public UserListPresenter(GetUserList getUserListUserCase, UserModelDataMapper userModelDataMapper) {
        this.getUserListUseCase = getUserListUserCase;
        this.userModelDataMapper = userModelDataMapper;
    }

    public void setView(@NonNull UserListView view) {
        this.view = view;
    }

    @Override
    public void resume() {}

    @Override
    public void pause()  {}

    @Override
    public void destroy() {
        getUserListUseCase.dispose();
        view = null;
    }

    /**
     * Initializes the presenter by start retrieving the user list.
     */
    public void initialize() {
        this.loadUserList();
    }

    /**
     * Loads all users.
     */
    private void loadUserList() {
        this.hideViewRetry();
        this.showViewLoading();
        getUserList();
    }

    public void onUserClicked(UserModel userModel) {
        view.viewUser(userModel);
    }

    private void showViewLoading() {
        view.showLoading();
    }

    void hideViewLoading() {
        view.hideLoading();
    }

    void showViewRetry() {
        view.showRetry();
    }

    private void hideViewRetry() {
        view.hideRetry();
    }

    void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(view.context(), errorBundle.getException());
        view.showError(errorMessage);
    }

    void showUsersCollectionInView(Collection<User> usersCollection) {
        final Collection<UserModel> userModelsCollection = userModelDataMapper.transform(usersCollection);
        view.renderUserList(userModelsCollection);
    }

    private void getUserList() {
        getUserListUseCase.execute(new UserListObserver(), null);
    }

    private final class UserListObserver extends DefaultObserver<List<User>> {

        @Override
        public void onComplete() {
            hideViewLoading();
        }

        @Override
        public void onError(Throwable e) {
            hideViewLoading();
            showErrorMessage(new DefaultErrorBundle((Exception) e));
            showViewRetry();
        }

        @Override
        public void onNext(List<User> users) {
            Timber.d("Successfully loaded user list");

            showUsersCollectionInView(users);
        }
    }
}
