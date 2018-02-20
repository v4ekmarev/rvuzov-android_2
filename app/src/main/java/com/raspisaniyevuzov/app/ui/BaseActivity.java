package com.raspisaniyevuzov.app.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.raspisaniyevuzov.app.ListMyScheduleActivity;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.RVuzovApp;
import com.raspisaniyevuzov.app.event.GroupNotFoundEvent;
import com.raspisaniyevuzov.app.event.ServiceUnavailableEvent;
import com.raspisaniyevuzov.app.event.UserAlreadyExistEvent;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.LogUtil;

import de.greenrobot.event.EventBus;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class BaseActivity extends AppCompatActivity {

    // Primary toolbar and drawer toggle
    private Toolbar mActionBarToolbar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    public void hideKeyboard2(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //check if no view has focus:
        View v = getCurrentFocus();
        if (v == null)
            return;
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public Toolbar getActionBarToolbar() {
        if (mActionBarToolbar == null) {
            mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
            if (mActionBarToolbar != null) {
                try {
                    setSupportActionBar(mActionBarToolbar);
                } catch (Exception e) {
                    LogUtil.e(BaseActivity.class.getSimpleName(), e.getMessage());
                }
            }
        }
        return mActionBarToolbar;
    }

    public void showProgress(String msg) {
        if (msg == null) msg = getString(R.string.loading_msg3);

        if (!isFinishing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void hideProgress() {
        if (progressDialog != null && !isFinishing() && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    protected Toolbar initToolbarForActivity(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        toolbar.setTitle(title);
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            LogUtil.e(BaseActivity.class.getSimpleName(), e.getMessage());
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return toolbar;
    }

    public void startActivityWithParam(Intent intent) {
        finish();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RVuzovApp.setInForeground(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        RVuzovApp.setInForeground(true);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(final GroupNotFoundEvent event) {
        CustomDialog.showMessageWithTitle(this, null, getString(R.string.group_not_found), null, new CustomDialog.CustomCallback() {
            @Override
            public void proceed() {
                DbUtil.clearDb();
                startActivityWithParam(new Intent(BaseActivity.this, ListMyScheduleActivity.class));
            }
        });
    }

}

