package com.raspisaniyevuzov.app.ui.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.ListMyScheduleActivity;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.service.PopupInfoMessage;
import com.raspisaniyevuzov.app.api.messages.user.RestorePasswordMessage;
import com.raspisaniyevuzov.app.event.DataNotFoundEvent;
import com.raspisaniyevuzov.app.event.IncorrectCredentialsEvent;
import com.raspisaniyevuzov.app.event.ShowPopupInfoEvent;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.widget.CustomDialog;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.DbUtil;
import com.raspisaniyevuzov.app.util.PrefUtil;
import com.raspisaniyevuzov.app.util.TextUtil;

/**
 * Created by SAPOZHKOV on 28.10.2015.
 */
public class RestorePasswordActivity extends BaseActivity {

    private TextView tvError;
    private Button btnRestorePassword;
    private EditText edtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        initToolbarForActivity(getString(R.string.activity_restore_password_title));

        tvError = (TextView) findViewById(R.id.tvError);
        edtEmail = (EditText) findViewById(R.id.edtEmail);

        btnRestorePassword = (Button) findViewById(R.id.btnRestorePassword);
        btnRestorePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();

                if (!email.isEmpty()) {
                    if (TextUtil.isValid(email)) {
                        tvError.setVisibility(View.GONE);
                        Api.sendMessage(new RestorePasswordMessage(email));
                        btnRestorePassword.setEnabled(false);
                        edtEmail.setEnabled(false);
                        btnRestorePassword.setText(getString(R.string.sending_msg));

                        Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.RESTORE_PASSWORD.type);
                    } else edtEmail.setError(getString(R.string.invalid_format_warning));
                } else {
                    edtEmail.setError(getString(R.string.empty_field_warning));
                }
            }
        });
    }

    public void onEventMainThread(DataNotFoundEvent event) {
        btnRestorePassword.setEnabled(true);
        edtEmail.setEnabled(true);
        btnRestorePassword.setText(getString(R.string.send));
        tvError.setText(getString(R.string.email_not_found));
        tvError.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(final ShowPopupInfoEvent event) {
        finish();
    }

}
