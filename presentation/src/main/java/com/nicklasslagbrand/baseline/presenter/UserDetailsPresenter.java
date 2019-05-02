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
import com.nicklasslagbrand.baseline.domain.interactor.GetUserDetails;
import com.nicklasslagbrand.baseline.domain.interactor.GetUserDetails.Params;
import com.nicklasslagbrand.baseline.mapper.UserModelDataMapper;
import com.nicklasslagbrand.baseline.model.UserModel;
import com.nicklasslagbrand.baseline.view.UserDetailsView;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * {@link Presenter} that controls communication between views and models of the presentation
 * layer.
 */
@PerActivity
public class UserDetailsPresenter implements Presenter {

    private UserDetailsView view;

    private final GetUserDetails getUserDetailsUseCase;
    private final UserModelDataMapper userModelDataMapper;

    @Inject
    public UserDetailsPresenter(GetUserDetails getUserDetailsUseCase, UserModelDataMapper userModelDataMapper) {
        this.getUserDetailsUseCase = getUserDetailsUseCase;
        this.userModelDataMapper = userModelDataMapper;
    }

    public void setView(@NonNull UserDetailsView view) {
        this.view = view;
    }

    @Override
    public void resume() {}

    @Override
    public void pause()  {}

    @Override
    public void destroy() {
        getUserDetailsUseCase.dispose();
        view = null;
    }

    /**
     * Initializes the presenter by showing/hiding proper views
     * and retrieving user details.
     */
    public void initialize(int userId) {
        this.hideViewRetry();
        this.showViewLoading();
        getUserDetails(userId);
    }

    private void getUserDetails(int userId) {
        getUserDetailsUseCase.execute(new UserDetailsObserver(), Params.forUser(userId));
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

    void hideViewRetry() {
        view.hideRetry();
    }

    void showErrorMessage(ErrorBundle errorBundle) {
        String errorMessage = ErrorMessageFactory.create(view.context(), errorBundle.getException());
        view.showError(errorMessage);
    }

    void showUserDetailsInView(User user) {
        final UserModel userModel = this.userModelDataMapper.transform(user);
        view.renderUser(userModel);
    }

    private final class UserDetailsObserver extends DefaultObserver<User> {

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
        public void onNext(User user) {
            Timber.d("Successfully loaded user details");

            showUserDetailsInView(user);
        }
    }
}
