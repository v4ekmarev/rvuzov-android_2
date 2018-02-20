package com.raspisaniyevuzov.app.ui.task;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.dao.TaskDao;
import com.raspisaniyevuzov.app.db.manager.TaskManager;
import com.raspisaniyevuzov.app.db.model.File;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;
import com.yandex.metrica.YandexMetrica;

import java.util.Calendar;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class ViewTaskActivity extends BaseTaskActivity {

    private static final int EDIT_TASK_REQUEST_CODE = 100;
    private TextView mTvText, mTvDate, mTvSubject;
    private String mTaskId;
    private View textDivider;
    private MenuItem mOpenMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_task);

        Toolbar toolbar = initToolbarForActivity("");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClose();
            }
        });

        mTvSubject = (TextView) findViewById(R.id.tvSubject);
        mTvText = (TextView) findViewById(R.id.tvText);
        textDivider = findViewById(R.id.textDivider);
        mTvDate = (TextView) findViewById(R.id.tvDate);
        mImagesContainer = (LinearLayout) findViewById(R.id.llImagesContainer);

        mTaskId = getIntent().getStringExtra(EditTaskActivity.EXTRA_TASK_ID);
        if (mTaskId == null) finish();
        else {
            mCurrentTask = (Task) TaskDao.getInstance().get(mTaskId, Task.class);
            if (mCurrentTask == null) finish();
        }
    }

    private void updateTaskInfo() {
        mCurrentTask = (Task) TaskDao.getInstance().get(mTaskId, Task.class);
        if (mCurrentTask != null) {
            mTvSubject.setText(mCurrentTask.getSubject() != null ? mCurrentTask.getSubject().getName() : "");

            if (mCurrentTask.getText() != null && !mCurrentTask.getText().isEmpty()) {
                mTvText.setVisibility(View.VISIBLE);
                textDivider.setVisibility(View.VISIBLE);
                mTvText.setText(mCurrentTask.getText());
            } else {
                mTvText.setVisibility(View.GONE);
                textDivider.setVisibility(View.GONE);
            }

            if (mCurrentTask.getDateEnd() != null && mCurrentTask.getDateEnd().getTime() > 0) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mCurrentTask.getDateEnd().getTime());
                mTvDate.setText(TimeUtil.convertDateToString(calendar.getTime()));
                findViewById(R.id.dateDivider).setVisibility(View.VISIBLE);
                findViewById(R.id.dateContainer).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.dateDivider).setVisibility(View.GONE);
                findViewById(R.id.dateContainer).setVisibility(View.GONE);
            }
            // add photos
            if (!mCurrentTask.getImages().isEmpty()) {
                mImagesContainer.removeAllViews();
                mImages = new RealmList<>();
                findViewById(R.id.photoDivider).setVisibility(View.VISIBLE);
                findViewById(R.id.hsvImages).setVisibility(View.VISIBLE);
                for (com.raspisaniyevuzov.app.db.model.File image : mCurrentTask.getImages()) {
                    addImage(Uri.parse(image.getName()), image);
                }
            } else {
                findViewById(R.id.photoDivider).setVisibility(View.GONE);
                findViewById(R.id.hsvImages).setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClose() {
        if (mOpenPhotoViewer && mPhotoViewerFragment != null) {
            findViewById(R.id.photoContainer).setVisibility(View.GONE);
            mOpenPhotoViewer = false;
        } else
            finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_task_actions, menu);

        menu.findItem(R.id.menu_edit).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                                     @Override
                                                                     public boolean onMenuItemClick(MenuItem item) {
                                                                         Intent intent = new Intent(ViewTaskActivity.this, EditTaskActivity.class);
                                                                         intent.putExtra(EditTaskActivity.EXTRA_TASK_ID, mCurrentTask.getId());
                                                                         startActivityForResult(intent, EDIT_TASK_REQUEST_CODE);
                                                                         return false;
                                                                     }
                                                                 }
        );

        mOpenMenuItem = menu.findItem(R.id.menu_reopen);

        if (mCurrentTask != null && mCurrentTask.isComplete()) {
            mOpenMenuItem.setIcon(R.drawable.ic_undo_white_24dp);
            mOpenMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                         @Override
                                                         public boolean onMenuItemClick(MenuItem item) {
                                                             TaskManager.open(mCurrentTask);
                                                             showMessage(getString(R.string.task_reopened));
                                                             invalidateOptionsMenu();
                                                             return false;
                                                         }
                                                     }
            );

        } else {
            mOpenMenuItem.setIcon(R.drawable.ic_done_white_24dp);
            mOpenMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                                                         @Override
                                                         public boolean onMenuItemClick(MenuItem item) {
                                                             TaskManager.complete(mCurrentTask);
                                                             showMessage(getString(R.string.task_completed));
                                                             invalidateOptionsMenu();
                                                             return false;
                                                         }
                                                     }
            );
        }

        return true;
    }

    @Override
    public void showMessage(String text) {
        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.flMain), text, Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(getResources().getColor(R.color.textLabelColor));
        snackbar.setActionTextColor(getResources().getColor(android.R.color.white));
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTaskInfo();
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.TASK_SCREEN.type);
    }

    @Override
    public List<File> getFileList() {
        return mImages;
    }

    @Override
    public void deletePhoto(int position) {
        // no implementation
    }

    @Override
    protected void onCloseActivity() {
        finish();
    }

    @Override
    protected void checkSaveButton() {
        // no implementation
    }

    @Override
    protected boolean isDeletePhotoEnable() {
        return false;
    }

}
