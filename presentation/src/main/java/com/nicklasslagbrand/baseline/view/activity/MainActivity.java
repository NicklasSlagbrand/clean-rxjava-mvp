package com.nicklasslagbrand.baseline.view.activity;

import android.os.Bundle;
import android.widget.Button;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.nicklasslagbrand.baseline.presentation.R;

/**
 * Main application screen. This is the app entry point.
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.btn_load_data) Button btnLoadData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    /**
     * Goes to the user list screen.
     */
    @OnClick(R.id.btn_load_data)
    void navigateToUserList() {
        this.navigator.navigateToUserList(this);
    }
}
