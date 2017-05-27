package com.sakurafish.pockettoushituryou.view.helper;

import android.databinding.BindingAdapter;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

public class DataBindingHelper {

    /**
     * item_foodlist.xmlの食品名と糖質量のテキストカラーを設定
     *
     * @param view
     * @param colorResId
     */
    @BindingAdapter("foodTextColor")
    public static void setFoodTextColor(TextView view, @ColorRes int colorResId) {
        view.setTextColor(ContextCompat.getColor(view.getContext(), colorResId));
    }

    @BindingAdapter("webViewUrl")
    public static void loadUrl(WebView webView, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        webView.loadUrl(url);
    }

    @BindingAdapter("webViewClient")
    public static void setWebViewClient(WebView webView, WebViewClient client) {
        webView.setWebViewClient(client);
    }
}
