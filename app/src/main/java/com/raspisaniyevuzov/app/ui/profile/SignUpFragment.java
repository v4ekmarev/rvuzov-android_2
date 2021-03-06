package com.raspisaniyevuzov.app.ui.profile;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.user.SignUpMessage;
import com.raspisaniyevuzov.app.event.ProfileResultReceivedEvent;
import com.raspisaniyevuzov.app.event.ServiceUnavailableEvent;
import com.raspisaniyevuzov.app.event.UserAlreadyExistEvent;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.MainActivity;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.DeviceUtil;
import com.raspisaniyevuzov.app.util.Settings;
import com.raspisaniyevuzov.app.util.TextUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class SignUpFragment extends BaseFragment {

    private Button signUp;
    private TextView tvError;
    private MaterialEditText edtEmail;
    private MaterialEditText edtPassword;
    private boolean mSignUpInProgress = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(
                R.layout.fragment_sign_up, container, false);

        edtPassword = (MaterialEditText) view.findViewById(R.id.edtPassword);
        edtPassword.setMetHintTextColor(getResources().getColor(R.color.blue));
        edtEmail = (MaterialEditText) view.findViewById(R.id.edtEmail);
        edtEmail.setMetHintTextColor(getResources().getColor(R.color.blue));

        tvError = (TextView) view.findViewById(R.id.tvError);

        edtEmail.setText(DeviceUtil.getPossibleDeviceEmail(getActivity()));

        TextView btnTermsOfUse = (TextView) view.findViewById(R.id.btnTermsOfUse);
        SpannableString content = new SpannableString(getString(R.string.terms_of_use2));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        btnTermsOfUse.setText(content);

        signUp = (Button) view.findViewById(R.id.btnSignUp);

        view.findViewById(R.id.btnTermsOfUse).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.addCategory(Intent.CATEGORY_BROWSABLE);
                    i.setData(Uri.parse(Settings.TERMS_OF_USE_URL));
                    startActivity(i);
                } catch (ActivityNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasError = false;
                String password = edtPassword.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    edtEmail.setError(getString(R.string.empty_field_warning));
                    hasError = true;
                } else {
                    if (!TextUtil.isValid(email)) {
                        edtEmail.setError(getString(R.string.invalid_format_warning));
                        hasError = true;
                    }
                }
                if (password.isEmpty()) {
                    edtPassword.setError(getString(R.string.empty_field_warning));
                    hasError = true;
                }

                if (!hasError) {
                    ((BaseActivity) getActivity()).hideKeyboard2(getActivity());
                    Api.sendMessage(new SignUpMessage(email, password));
                    signUp.setText(getString(R.string.loading_msg2));
                    signUp.setEnabled(false);
                    edtEmail.setEnabled(false);
                    edtPassword.setEnabled(false);
                    mSignUpInProgress = true;
                }
            }
        });

        return view;
    }

    public void onEventMainThread(ProfileResultReceivedEvent event) {
        if (mSignUpInProgress) {
            mSignUpInProgress = false;
            if (event.success) {
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_UP_SUCCESSFUL.type);
                ((MainActivity) getActivity()).login();
            } else {
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_UP_FAULT.type);
            }
        }
    }

    public void onEventMainThread(UserAlreadyExistEvent event) {
        signUp.setEnabled(true);
        edtEmail.setEnabled(true);
        edtPassword.setEnabled(true);
        signUp.setText(getString(R.string.sign_up));
        tvError.setText(getString(R.string.user_already_exist));
        tvError.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(ServiceUnavailableEvent event) {
        signUp.setEnabled(true);
        edtEmail.setEnabled(true);
        edtPassword.setEnabled(true);
        signUp.setText(getString(R.string.sign_up));
        tvError.setText(getString(R.string.service_unavailable));
        tvError.setVisibility(View.VISIBLE);
    }

}
