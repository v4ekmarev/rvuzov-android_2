package com.raspisaniyevuzov.app.ui.task;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.db.dao.FileDao;
import com.raspisaniyevuzov.app.db.dao.SubjectDao;
import com.raspisaniyevuzov.app.db.dao.TaskDao;
import com.raspisaniyevuzov.app.db.model.Subject;
import com.raspisaniyevuzov.app.db.model.Task;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.ui.widget.ImageChooserDialog;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.FileUtil;
import com.raspisaniyevuzov.app.util.TimeUtil;
import com.yandex.metrica.YandexMetrica;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class EditTaskActivity extends BaseTaskActivity {

    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_SUBJECT_ID = "extra_subject_id";
    private TextView tvSubject;
    private Uri mImageCaptureUri;
    private EditText mEdtText;
    private TextView tvDateEnd, tvDateEndTitle, tvSubjectTitle;
    private DatePickerDialog mDatePickerDialog;
    private Calendar mDateEndCalendar = Calendar.getInstance();
    private long mSelectedDateEnd = 0;
    private FloatingActionsMenu multipleActions;
    private FloatingActionButton addImageMenu;
    private ImageView ivDateIcon;
    private FrameLayout flClearDateEnd;
    private boolean mSearchOpened = false;
    private boolean mSaveEnable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);

        mImagesContainer = (LinearLayout) findViewById(R.id.llImagesContainer);
        multipleActions = (FloatingActionsMenu) findViewById(R.id.multiple_actions);

        addImageMenu = (FloatingActionButton) findViewById(R.id.addImageMenu);

        ivDateIcon = (ImageView) findViewById(R.id.ivDateIcon);
        flClearDateEnd = (FrameLayout) findViewById(R.id.flClearDateEnd);
        tvSubject = (TextView) findViewById(R.id.tvSubject);
        mEdtText = (EditText) findViewById(R.id.edtText);
        tvDateEnd = (TextView) findViewById(R.id.tvDateEnd);
        tvDateEndTitle = (TextView) findViewById(R.id.tvDateEndTitle);
        tvSubjectTitle = (TextView) findViewById(R.id.tvSubjectTitle);

        mEdtText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                isKeyboardShown(mEdtText.getRootView());
            }
        });

        String title;

        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        if (taskId != null) {
            title = getString(R.string.activity_edit_task_title);
            mCurrentTask = (Task) TaskDao.getInstance().get(taskId, Task.class);
            if (mCurrentTask != null) {
                if (mCurrentTask.getDateEnd() != null && mCurrentTask.getDateEnd().getTime() > 0)
                    setDateEnd(mCurrentTask.getDateEnd());
                onSubjectSelect(mCurrentTask.getSubject(), false);
                mEdtText.setText(mCurrentTask.getText());
                if (!mCurrentTask.getImages().isEmpty())
                    for (com.raspisaniyevuzov.app.db.model.File image : mCurrentTask.getImages())
                        addImage(Uri.parse(image.getName()), image);
            }
        } else title = getString(R.string.activity_add_task_title);

        Toolbar toolbar = initToolbarForActivity(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClose();
            }
        });
        toolbar.setBackgroundColor(getResources().getColor(R.color.accentColor));

        // set Subject if it has passed
        String subjectId = getIntent().getStringExtra(EXTRA_SUBJECT_ID);
        if (subjectId != null) {
            mSelectedSubject = (Subject) SubjectDao.getInstance().get(subjectId, Subject.class);
            if (mSelectedSubject != null)
                onSubjectSelect(mSelectedSubject, false);
        }

        findViewById(R.id.flSubject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchOpened = true;
                getSupportFragmentManager().beginTransaction().replace(R.id.searchContainer, new SearchSubjectFragment()).addToBackStack("searchContainer").commit();
                hideFab();
            }
        });

        FloatingActionButton photoFromGallery = (FloatingActionButton) findViewById(R.id.photoFromGallery);

        photoFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleActions.collapse();
                addImageMenu.setIcon(R.drawable.ic_add_to_photos_white_24dp);
                // pick from file
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, getString(R.string.select_photo_hint)), ImageChooserDialog.PICK_FROM_FILE);
            }
        });

        FloatingActionButton photoFromCamera = (FloatingActionButton) findViewById(R.id.photoFromCamera);

        addImageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleActions.setVisibility(View.VISIBLE);
                if (multipleActions.isExpanded()) {
                    multipleActions.collapse();
                    addImageMenu.setIcon(R.drawable.ic_add_to_photos_white_24dp);
                } else {
                    multipleActions.expand();
                    addImageMenu.setIcon(R.drawable.ic_clear_white_24dp);
                }
            }
        });

        photoFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multipleActions.collapse();
                addImageMenu.setIcon(R.drawable.ic_add_to_photos_white_24dp);
                // pick from camera
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mImageCaptureUri = Uri.fromFile(new File(FileUtil.getNewFileName()));
                intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                try {
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, ImageChooserDialog.PICK_FROM_CAMERA);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        findViewById(R.id.flDateEnd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateEndDialog();
            }
        });

        findViewById(R.id.flDateEnd).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    showDateEndDialog();
            }
        });

        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (TimeUtil.getDayLeft(Calendar.getInstance().getTimeInMillis(), newDate.getTimeInMillis() + 24 * TimeUtil.HOUR) > 0) {
                    setDateEnd(newDate.getTime());
                    mHasChanges = true;
                } else {
                    showMessage(getString(R.string.set_edit_task_date_end_warning));
                }
            }

        }, mDateEndCalendar.get(Calendar.YEAR), mDateEndCalendar.get(Calendar.MONTH), mDateEndCalendar.get(Calendar.DAY_OF_MONTH));

        mEdtText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkSaveButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                mHasChanges = true;
            }
        });

        checkSaveButton();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void showMessage(String text) {
        Snackbar
                .make(findViewById(R.id.root), text, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onCloseActivity() {
        if (!mSaveEnable && mHasChanges) {
            CustomDialog.showMessageWithTitle(this, null, getString(R.string.edit_task_activity_confirm_close_msg), null, null, new CustomDialog.CustomCallback() {
                @Override
                public void proceed() {
                    finish();
                }
            }, null);
        } else {
            finish();
        }
    }

    private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
    /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
    /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        if (isKeyboardShown) hideFab();
        else if (!mSearchOpened)
            showFab();

        return isKeyboardShown;
    }

    private void showFab() {
        multipleActions.animate().scaleX(1).scaleY(1).setDuration(200).setStartDelay(500).start();
        addImageMenu.animate().scaleX(1).scaleY(1).setDuration(200).setStartDelay(500).start();
    }

    private void hideFab() {
        multipleActions.animate().scaleX(0).scaleY(0).setDuration(200).setStartDelay(0).start();
        addImageMenu.animate().scaleX(0).scaleY(0).setDuration(200).setStartDelay(0).start();
    }

    private void setDateEnd(Date newDate) {
        mSelectedDateEnd = newDate.getTime();
        mDateEndCalendar.setTimeInMillis(newDate.getTime());
        tvDateEndTitle.setVisibility(View.VISIBLE);
        tvDateEnd.setText(TimeUtil.convertDateToString(newDate));
        ivDateIcon.setImageResource(R.drawable.ic_clear_white_24dp);
        flClearDateEnd.setClickable(true);

        flClearDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDateEnd.setText("");
                mDateEndCalendar.setTimeInMillis(0);
                mSelectedDateEnd = 0;
                tvDateEndTitle.setVisibility(View.INVISIBLE);
                flClearDateEnd.setClickable(false);
                ivDateIcon.setImageResource(R.drawable.ic_query_builder_grey600_24dp);
                mHasChanges = true;
            }
        });
    }

    private void showDateEndDialog() {
        mDatePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ImageChooserDialog.PICK_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    mHasChanges = true;
                    addImage(mImageCaptureUri, true);
                }
                break;
            case ImageChooserDialog.PICK_FROM_FILE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    mHasChanges = true;
                    mImageCaptureUri = data.getData();
                    if (mImageCaptureUri == null)
                        return;
                    Uri mSaveImageUri = Uri.fromFile(new File(FileUtil.getNewFileName()));
                    try {
                        FileUtil.copyInputToOutputStream(getContentResolver().openInputStream(mImageCaptureUri), getContentResolver().openOutputStream(mSaveImageUri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    addImage(mSaveImageUri, false);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // save changes
        if (mHasChanges && mSaveEnable) {
            if (mCurrentTask == null) {
                mCurrentTask = new Task();
                mCurrentTask.setId(DbUtil.getNewUid());
                mCurrentTask.setSubject(mSelectedSubject);
                mCurrentTask.setDateAdd(Calendar.getInstance().getTime());
                if (mSelectedDateEnd > 0)
                    mCurrentTask.setDateEnd(new Date(mSelectedDateEnd));
                mCurrentTask.setComplete(false);
                mCurrentTask.setImages(mImages);
                mCurrentTask.setText(mEdtText.getText().toString().trim());
                TaskDao.getInstance().save(mCurrentTask);
            } else {
                RealmList<com.raspisaniyevuzov.app.db.model.File> files = new RealmList<>();
                for (com.raspisaniyevuzov.app.db.model.File image : mImages)
                    files.add((com.raspisaniyevuzov.app.db.model.File) FileDao.getInstance().update(image));
                TaskDao.getInstance().updateTask(mCurrentTask, mEdtText.getText().toString().trim(), mSelectedSubject, files, mCurrentTask.isComplete(), new Date(mSelectedDateEnd));
            }
            // delete images
            if (!mImagesToDelete.isEmpty())
                for (String uri : mImagesToDelete)
                    deletePhotoFromDisk(uri);
        }
    }

    public void onSubjectSelect(Subject subject, boolean changed) {
        tvSubject.setText(subject.getName());
        tvSubjectTitle.setVisibility(View.VISIBLE);
        mSelectedSubject = subject;
        findViewById(R.id.ivSubjectIcon).setVisibility(View.GONE);
        checkSaveButton();
        if (changed)
            mHasChanges = true;
    }

    public void checkSaveButton() {
        mSaveEnable = (mSelectedSubject != null && (!mEdtText.getText().toString().trim().isEmpty() || !mImages.isEmpty()));
    }

    @Override
    protected boolean isDeletePhotoEnable() {
        return true;
    }

    public void onSearchClose() {
        mSearchOpened = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        YandexMetrica.reportEvent(AnalyticsUtil.YandexMetricaEventType.EDIT_TASK_SCREEN.type);
    }

}
