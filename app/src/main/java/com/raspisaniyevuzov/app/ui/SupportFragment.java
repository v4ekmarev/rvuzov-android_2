package com.raspisaniyevuzov.app.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.raspisaniyevuzov.app.R;
import com.raspisaniyevuzov.app.util.Settings;

/**
 * Created by SAPOZHKOV on 16.09.2015.
 */
public class SupportFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        Toolbar mToolbar = ((BaseActivity) getActivity()).getActionBarToolbar();
        mToolbar.setTitle(getString(R.string.fragment_support_title));
        mToolbar.setSubtitle(null);

        WebView supportPage = (WebView) view.findViewById(R.id.supportWebView);

        supportPage.getSettings().setJavaScriptEnabled(true);
        supportPage.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        supportPage.loadUrl(Settings.SUPPORT_URL);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

}
