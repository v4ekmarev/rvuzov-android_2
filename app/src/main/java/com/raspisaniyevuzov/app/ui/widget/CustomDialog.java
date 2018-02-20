package com.raspisaniyevuzov.app.ui.widget;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.raspisaniyevuzov.app.R;

/**
 * Created by SAPOZHKOV on 09.10.2015.
 */
public class CustomDialog {

    public interface CustomCallback {
        void proceed();
    }

    public static MaterialDialog getMaterialDialogForList(Context context, View dialogView, String title) {
        return new MaterialDialog.Builder(context)
                .title(title)
                .customView(dialogView, false)
                .build();
    }

    public static void showMessageWithTitleShort(Context context, String title, String msg, final CustomCallback positiveCallback) {
        showMessageWithTitle(context, title, msg, null, null, positiveCallback, null);
    }

    public static void showMessageWithTitle(Context context, String title, String msg, String positiveBtnTxt, String negativeBtnTxt, final CustomCallback positiveCallback, final CustomCallback negativeCallback) {
        if (positiveBtnTxt == null)
            positiveBtnTxt = context.getString(R.string.positive_button_text).toUpperCase();
        if (negativeBtnTxt == null)
            negativeBtnTxt = context.getString(R.string.negative_button_text).toUpperCase();
        new MaterialDialog.Builder(context)
                .title(title)
                .content(msg)
                .positiveText(positiveBtnTxt)
                .negativeText(negativeBtnTxt)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                    if (positiveCallback != null)
                                        positiveCallback.proceed();
                                }
                            }
                ).onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                materialDialog.dismiss();
                if (negativeCallback != null)
                    negativeCallback.proceed();
            }
        }).build()
                .show();
    }

    public static void showMessageWithTitle(Context context, String title, String msg, String positiveBtnTxt, final CustomCallback positiveCallback) {
        if (positiveBtnTxt == null)
            positiveBtnTxt = context.getString(R.string.positive_button_text).toUpperCase();
        new MaterialDialog.Builder(context)
                .title(title)
                .content(msg)
                .positiveText(positiveBtnTxt)
                .negativeText(null)
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(MaterialDialog materialDialog, DialogAction dialogAction) {
                                    materialDialog.dismiss();
                                    if (positiveCallback != null)
                                        positiveCallback.proceed();
                                }
                            }
                ).build()
                .show();
    }

}
