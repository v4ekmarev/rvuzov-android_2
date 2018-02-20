package com.raspisaniyevuzov.app.api;

import com.raspisaniyevuzov.app.event.DataNotFoundEvent;
import com.raspisaniyevuzov.app.event.IncorrectCredentialsEvent;
import com.raspisaniyevuzov.app.event.ServiceUnavailableEvent;
import com.raspisaniyevuzov.app.event.UserAlreadyExistEvent;
import com.raspisaniyevuzov.app.util.LogUtil;

import de.greenrobot.event.EventBus;

public class ResponseCodesHelper {

    private static final String TAG = ResponseCodesHelper.class.getSimpleName();

    public static void process(int errorCode) {
        switch (errorCode) {
            case 200: // ok
                break;
            case 400: // unknown data format
                LogUtil.e(TAG, "Error code = " + errorCode);
                break;
            case 401: // invalid token
                LogUtil.e(TAG, "Error code = " + errorCode);
//                EventBus.getDefault().post(new InvalidUserTokenEvent());
                break;
            case 404: // method not found
                LogUtil.e(TAG, "Error code = " + errorCode);
                EventBus.getDefault().post(new DataNotFoundEvent());
                break;
            case 500: // internal server error
                LogUtil.e(TAG, "Error code = " + errorCode);
                break;
            case 501: // unknown message type
                LogUtil.e(TAG, "Error code = " + errorCode);
                break;
            case 502: // bad gateway
                LogUtil.e(TAG, "Error code = " + errorCode);
                EventBus.getDefault().post(new ServiceUnavailableEvent());
                break;
            // 1000 - business logic
            case 1001: // user already exist
                LogUtil.e(TAG, "Error code = " + errorCode);
                EventBus.getDefault().post(new UserAlreadyExistEvent());
                break;
            case 1002: // email or password is incorrect
                LogUtil.e(TAG, "Error code = " + errorCode);
                EventBus.getDefault().post(new IncorrectCredentialsEvent());
                break;
            case 1003: // unknown profile error
                LogUtil.e(TAG, "Error code = " + errorCode);
                break;

            // TODO add other error codes here
            default:
                break;
        }
    }

}



