package com.raspisaniyevuzov.app.api;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.raspisaniyevuzov.app.event.ErrorEvent;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.greenrobot.event.EventBus;

public class ApiServerHttpResponseHandler extends JsonHttpResponseHandler {

    public ApiServerHttpResponseHandler() {
        super();
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, org.json.JSONArray response) {
        super.onSuccess(statusCode, headers, response);
        Api.receiveMessages(response);
        Api.syncInProgress = false;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        super.onSuccess(statusCode, headers, response);
        Api.syncInProgress = false;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String responseString) {
        super.onSuccess(statusCode, headers, responseString);
        Api.syncInProgress = false;
    }

    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
        super.onFailure(statusCode, headers, e, errorResponse);
        onErrorReceived(statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
        super.onFailure(statusCode, headers, responseString, throwable);
        onErrorReceived(statusCode);
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
        super.onFailure(statusCode, headers, throwable, errorResponse);
        Api.syncInProgress = false;
        Api.receiveMessages(errorResponse);
    }

    private void onErrorReceived(int statusCode) {
        Api.syncInProgress = false;
        ResponseCodesHelper.process(statusCode);
        EventBus.getDefault().post(new ErrorEvent());
    }

}
