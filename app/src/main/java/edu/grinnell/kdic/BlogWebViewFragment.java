package edu.grinnell.kdic;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import static edu.grinnell.kdic.Constants.BLOG_URL;


public class BlogWebViewFragment extends Fragment {

    public static final String TAG = "BlogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = View.inflate(getContext(), R.layout.fragment_blog, null);
        WebView webView = (WebView) view.findViewById(R.id.webview);
        TextView tvError = (TextView) view.findViewById(R.id.tv_error);

        if (NetworkState.isOnline(getContext())) {
            tvError.setVisibility(View.GONE);
            webView.loadUrl(BLOG_URL);
            webView.getSettings().setJavaScriptEnabled(true);
        } else {
            webView.setVisibility(View.GONE);
        }

        return view;
    }
}
