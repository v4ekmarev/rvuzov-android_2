/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.raspisaniyevuzov.app.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amplitude.api.Amplitude;
import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.api.Api;
import com.raspisaniyevuzov.app.api.messages.user.SignInMessage;
import com.raspisaniyevuzov.app.event.IncorrectCredentialsEvent;
import com.raspisaniyevuzov.app.event.ProfileResultReceivedEvent;
import com.raspisaniyevuzov.app.event.ServiceUnavailableEvent;
import com.raspisaniyevuzov.app.event.UserAlreadyExistEvent;
import com.raspisaniyevuzov.app.ui.BaseActivity;
import com.raspisaniyevuzov.app.ui.BaseFragment;
import com.raspisaniyevuzov.app.ui.MainActivity;
import com.raspisaniyevuzov.app.util.AnalyticsUtil;
import com.raspisaniyevuzov.app.util.TextUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * Created by SAPOZHKOV on 24.09.2015.
 */
public class SignInFragment extends BaseFragment {

    private Button signIn;
    private TextView tvError;
    private MaterialEditText edtEmail;
    private MaterialEditText edtPassword;
    private boolean mSignInInProgress = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(
                R.layout.fragment_sign_in, container, false);

        edtPassword = (MaterialEditText) view.findViewById(R.id.edtPassword);
        edtPassword.setMetHintTextColor(getResources().getColor(R.color.blue));
        edtEmail = (MaterialEditText) view.findViewById(R.id.edtEmail);
        edtEmail.setMetHintTextColor(getResources().getColor(R.color.blue));

        tvError = (TextView) view.findViewById(R.id.tvError);

        view.findViewById(R.id.btnRestorePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RestorePasswordActivity.class));
            }
        });

        signIn = (Button) view.findViewById(R.id.btnSignIn);

        signIn.setOnClickListener(new View.OnClickListener() {
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
                    Api.sendMessage(new SignInMessage(email, password));
                    signIn.setEnabled(false);
                    signIn.setText(getString(R.string.loading_msg2));
                    signIn.setEnabled(false);
                    edtEmail.setEnabled(false);
                    edtPassword.setEnabled(false);
                    mSignInInProgress = true;
                    ((BaseActivity) getActivity()).hideKeyboard2(getActivity());
                }
            }
        });

        Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_IN_FORM_OPEN.type);

        return view;
    }

    public void onEventMainThread(ProfileResultReceivedEvent event) {
        if (mSignInInProgress) {
            mSignInInProgress = false;
            if (event.success) {
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_IN_SUCCESSFUL.type);
                ((MainActivity) getActivity()).login();
            } else {
                Amplitude.getInstance().logEvent(AnalyticsUtil.AmplitudeEventType.SIGN_IN_FAULT.type);
            }
        }
    }

    public void onEventMainThread(IncorrectCredentialsEvent event) {
        signIn.setEnabled(true);
        edtEmail.setEnabled(true);
        edtPassword.setEnabled(true);
        signIn.setText(getString(R.string.sign_in));
        tvError.setText(getString(R.string.incorrect_credentials));
        tvError.setVisibility(View.VISIBLE);
    }

    public void onEventMainThread(ServiceUnavailableEvent event) {
        signIn.setEnabled(true);
        edtEmail.setEnabled(true);
        edtPassword.setEnabled(true);
        signIn.setText(getString(R.string.sign_in));
        tvError.setText(getString(R.string.service_unavailable));
        tvError.setVisibility(View.VISIBLE);
    }

}
