package com.raspisaniyevuzov.app.ui.auth;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.schedule.AddScheduleRequestMessage;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.auth.search.BaseSearchFragment;
import com.raspisaniyevuzov.app.util.DeviceUtil;
import com.raspisaniyevuzov.app.util.TextUtil;

/**
 * Created by SAPOZHKOV on 28.10.2015.
 */
public class AddNewDataActivity extends BaseActivity {

    private CompoundButton.OnCheckedChangeListener employeeChangeListener, studentChangeListener;
    private MaterialEditText edtName, edtEmail, mEdtUniversityName;
    private String mSelectedUserType = null;
    private boolean mRequestSent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_data);

        mEdtUniversityName = (MaterialEditText) findViewById(R.id.edtUniversityName);

        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtEmail = (MaterialEditText) findViewById(R.id.edtEmail);

        edtEmail.setText(DeviceUtil.getPossibleDeviceEmail(this));

        mEdtUniversityName.setText(getIntent().getStringExtra(BaseSearchFragment.EXTRA_UNIVERSITY));
        edtName.setFloatingLabelText(getString(R.string.user_name).toUpperCase());
        mEdtUniversityName.setFloatingLabelText(getString(R.string.university).toUpperCase());

        Toolbar toolbar = initToolbarForActivity(getString(R.string.activity_add_new_university_title));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClose();
            }
        });

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasError = false;
                if (mEdtUniversityName.getText().toString().length() == 0) {
                    mEdtUniversityName.setError(getString(R.string.empty_field_warning));
                    hasError = true;
                }
                if (edtName.getText().toString().length() == 0) {
                    edtName.setError(getString(R.string.empty_field_warning));
                    hasError = true;
                }
                if (edtEmail.getText().toString().length() == 0) {
                    edtEmail.setError(getString(R.string.empty_field_warning));
                    hasError = true;
                } else if (!TextUtil.isValid(edtEmail.getText().toString())) {
                    edtEmail.setError(getString(R.string.invalid_format_warning));
                    hasError = true;
                }
                if (!hasError) {
                    findViewById(R.id.rlDataSent).setVisibility(View.VISIBLE);
                    findViewById(R.id.rlData).setVisibility(View.INVISIBLE);
                    Api.sendMessage(new AddScheduleRequestMessage(edtName.getText().toString(), edtEmail.getText().toString(), mSelectedUserType, mEdtUniversityName.getText().toString()));
                    Toast.makeText(AddNewDataActivity.this, getString(R.string.new_schedule_request), Toast.LENGTH_SHORT).show();

                    mRequestSent = true;
                    Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SEND_FORM_SUCCESSFUL.type);
                }
            }
        });

        findViewById(R.id.btnContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               onClose();
            }
        });

        final RadioButton studentType = (RadioButton) findViewById(R.id.studentType);
        final RadioButton employeeType = (RadioButton) findViewById(R.id.employeeType);

        studentChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                employeeType.setOnCheckedChangeListener(null);
                employeeType.setChecked(!isChecked);
                employeeType.setOnCheckedChangeListener(employeeChangeListener);
                mSelectedUserType = "student";
            }
        };

        employeeChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                studentType.setOnCheckedChangeListener(null);
                studentType.setChecked(!isChecked);
                studentType.setOnCheckedChangeListener(studentChangeListener);
                mSelectedUserType = "employee";
            }
        };

        studentType.setOnCheckedChangeListener(studentChangeListener);
        employeeType.setOnCheckedChangeListener(employeeChangeListener);

        studentType.setChecked(true);

        Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SEND_FORM_OPEN.type);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    @Override
    public void onBackPressed() {
        onClose();
    }

    public void onClose() {
        if (!mRequestSent)
            Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SEND_FORM_FAULT.type);
        finish();
    }

}
