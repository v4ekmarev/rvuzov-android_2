package com.raspisaniyevuzov.app.api.messages.ads;

import android.util.Base64;

import com.raspisaniyevuzov.app.api.messages.BaseMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class InAppPurchaseCompleteMessage extends BaseMessage {

    private final String purchase_id;
    private final String platform;
    private final String token;
    private final String signature;
    private final String originalJson;

    public InAppPurchaseCompleteMessage(String purchase_id, String platform, String token, String signature, String originalJson) {
        super();
        this.purchase_id = purchase_id;
        this.platform = platform;
        this.token = token;
        this.signature = signature;
        this.originalJson = originalJson;
    }

    @Override
    public JSONObject encodeToJSON() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("purchase_id", purchase_id);
        jsonObject.put("platform", platform);
        // for verifing purchase
        jsonObject.put("signature", Base64.encodeToString(signature.getBytes(), Base64.DEFAULT));
        jsonObject.put("original_json", Base64.encodeToString(originalJson.getBytes(), Base64.DEFAULT));
        // additional
        jsonObject.put("token", token);

        return jsonObject;
    }

}
