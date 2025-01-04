package nemosofts.online.live.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.nemosofts.AppCompatActivity;

import nemosofts.online.live.R;

public class EmbeddedPlayerActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent = getIntent();
        String streamUrl = intent.getStringExtra("streamUrl");

        webView = findViewById(R.id.video);
        progressBar = findViewById(R.id.load);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.setFocusableInTouchMode(false);
        webView.setFocusable(false);
        webView.getSettings().setDefaultTextEncodingName("UTF-8");
        webView.getSettings().setJavaScriptEnabled(true);

        String mimeType = "text/html";
        String encoding = "utf-8";

        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + streamUrl
                + "</body></html>";

        webView.setWebChromeClient(new WebChromeClient() {

            private View mCustomView;
            private CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            @Override
            public void onHideCustomView() {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            @Override
            public void onShowCustomView(View paramView, CustomViewCallback paramCustomViewCallback) {
                if (this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846);
            }
        });
        webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
    }

    @Override
    public int setContentViewID() {
        return R.layout.activity_embedded_player;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.loadUrl("");
        }
    }
}
