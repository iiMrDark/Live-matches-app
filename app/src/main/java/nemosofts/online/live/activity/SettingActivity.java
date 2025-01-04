package nemosofts.online.live.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ColorUtils;
import androidx.nemosofts.theme.ThemeEngine;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Objects;

import nemosofts.online.live.BuildConfig;
import nemosofts.online.live.R;
import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import nemosofts.online.live.utils.IfSupported;
import nemosofts.online.live.view.NSoftsProgressDialog;

public class SettingActivity extends AppCompatActivity {

    private ThemeEngine themeEngine;
    private TextView tvCacheSize;
    private TextView tvClassic;
    private TextView tvDarkGrey;
    private TextView tvDark;
    private TextView tvDarkBlue;
    private ImageView ivDarkMode;
    private NSoftsProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IfSupported.isRTL(this);
        IfSupported.isScreenshot(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        themeEngine = new ThemeEngine(this);

        progressDialog = new NSoftsProgressDialog(SettingActivity.this);

        tvClassic = findViewById(R.id.tv_classic);
        tvDarkGrey = findViewById(R.id.tv_dark_grey);
        tvDark = findViewById(R.id.tv_dark);
        tvDarkBlue = findViewById(R.id.tv_dark_blue);
        ivDarkMode = findViewById(R.id.iv_dark_mode);
        tvCacheSize = findViewById(R.id.tv_cachesize);

        try {
            ObjectAnimator fadeAltAnim = ObjectAnimator.ofFloat(ivDarkMode, View.ALPHA, 0, 1);
            fadeAltAnim.setDuration(1500);
            fadeAltAnim.start();
        } catch (Exception e) {
            Log.e("SettingActivity", "Error object animator", e);
        }

        initializeCache();
        getThemeData();
        setupButton();
    }

    private void setupButton() {
        final String TAG_URL = "web_url";
        final String TAG_TITLE = "page_title";

        findViewById(R.id.ll_cache).setOnClickListener(v -> cacheRemove());
        findViewById(R.id.ll_notifications).setOnClickListener(v -> notification());
        findViewById(R.id.ll_about).setOnClickListener(v -> startActivity(new Intent(SettingActivity.this, AboutUsActivity.class)));
        findViewById(R.id.ll_privacy).setOnClickListener(v ->  {
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra(TAG_URL, BuildConfig.BASE_URL+"data.php?privacy_policy");
            intent.putExtra(TAG_TITLE, getResources().getString(R.string.privacy_policy));
            ActivityCompat.startActivity(SettingActivity.this, intent, null);
        });
        findViewById(R.id.ll_terms).setOnClickListener(v ->  {
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra(TAG_URL, BuildConfig.BASE_URL+"data.php?terms");
            intent.putExtra(TAG_TITLE, getResources().getString(R.string.terms_and_conditions));
            ActivityCompat.startActivity(SettingActivity.this, intent, null);
        });
        findViewById(R.id.ll_privacy_data).setOnClickListener(v ->  {
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra(TAG_URL, BuildConfig.BASE_URL+"account_delete_request.php");
            intent.putExtra(TAG_TITLE, getResources().getString(R.string.deletion_policy));
            ActivityCompat.startActivity(SettingActivity.this, intent, null);
        });

        tvClassic.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 0){
                setThemeMode(false, 0);
            }
        });
        tvDarkGrey.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 2){
                setThemeMode(true, 2);
            }
        });
        tvDarkBlue.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 3){
                setThemeMode(true, 3);
            }
        });
        tvDark.setOnClickListener(view -> {
            if (themeEngine.getThemePage() != 1){
                setThemeMode(true, 1);
            }
        });
    }

    private void cacheRemove() {
        new AsyncTaskExecutor<String, String, String>() {
            @Override
            protected void onPreExecute() {
                progressDialog.show();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(String strings) {
                try {
                    FileUtils.deleteQuietly(getCacheDir());
                    FileUtils.deleteQuietly(getExternalCacheDir());
                    return "1";
                } catch (Exception e) {
                    return "0";
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            protected void onPostExecute(String s) {
                if (isFinishing()){
                    return;
                }
                progressDialog.dismiss();
                Toast.makeText(SettingActivity.this, getString(R.string.cache_cleared), Toast.LENGTH_SHORT).show();
                tvCacheSize.setText("0 MB");
            }
        }.execute();
    }

    private void notification() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
        } else {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(android.net.Uri.parse("package:" + getPackageName()));
        }
        startActivity(intent);
    }

    private void setThemeMode(Boolean isChecked, int isTheme) {
        themeEngine.setThemeMode(isChecked);
        themeEngine.setThemePage(isTheme);
        Callback.setRecreate(true);
        recreate();
    }

    private void initializeCache() {
        long size = 0;
        size += getDirSize(this.getCacheDir());
        size += getDirSize(this.getExternalCacheDir());
        tvCacheSize.setText(ApplicationUtil.readableFileSize(size));
    }

    private long getDirSize(File dir) {
        long size = 0;
        try {
            for (File file : Objects.requireNonNull(dir.listFiles())) {
                if (file != null && file.isDirectory()) {
                    size += getDirSize(file);
                } else if (file != null && file.isFile()) {
                    size += file.length();
                }
            }
        } catch (Exception e) {
            return size;
        }
        return size;
    }

    private void getThemeData() {
        int theme = themeEngine.getThemePage();
        if (theme == 0){
            tvClassic.setBackgroundResource(R.drawable.btn_accent);
            tvDarkGrey.setBackgroundResource(R.drawable.btn_border_bg);
            tvDarkBlue.setBackgroundResource(R.drawable.btn_border_bg);
            tvDark.setBackgroundResource(R.drawable.btn_border_bg);

            tvClassic.setTextColor(ColorUtils.colorWhite(this));
            tvDarkGrey.setTextColor(ColorUtils.colorTitle(this));
            tvDarkBlue.setTextColor(ColorUtils.colorTitle(this));
            tvDark.setTextColor(ColorUtils.colorTitle(this));

            ivDarkMode.setImageResource(R.drawable.classic);

        } else if (theme == 1){
            tvClassic.setBackgroundResource(R.drawable.btn_border_bg);
            tvDarkGrey.setBackgroundResource(R.drawable.btn_border_bg);
            tvDarkBlue.setBackgroundResource(R.drawable.btn_border_bg);
            tvDark.setBackgroundResource(R.drawable.btn_accent);

            tvClassic.setTextColor(ColorUtils.colorTitle(this));
            tvDarkGrey.setTextColor(ColorUtils.colorTitle(this));
            tvDarkBlue.setTextColor(ColorUtils.colorTitle(this));
            tvDark.setTextColor(ColorUtils.colorWhite(this));

            ivDarkMode.setImageResource(R.drawable.dark);

        } else if (theme == 2){
            tvClassic.setBackgroundResource(R.drawable.btn_border_bg);
            tvDarkGrey.setBackgroundResource(R.drawable.btn_accent);
            tvDarkBlue.setBackgroundResource(R.drawable.btn_border_bg);
            tvDark.setBackgroundResource(R.drawable.btn_border_bg);

            tvClassic.setTextColor(ColorUtils.colorTitle(this));
            tvDarkGrey.setTextColor(ColorUtils.colorWhite(this));
            tvDarkBlue.setTextColor(ColorUtils.colorTitle(this));
            tvDark.setTextColor(ColorUtils.colorTitle(this));

            ivDarkMode.setImageResource(R.drawable.dark_grey);
        } else if (theme == 3){
            tvClassic.setBackgroundResource(R.drawable.btn_border_bg);
            tvDarkGrey.setBackgroundResource(R.drawable.btn_border_bg);
            tvDarkBlue.setBackgroundResource(R.drawable.btn_accent);
            tvDark.setBackgroundResource(R.drawable.btn_border_bg);

            tvClassic.setTextColor(ColorUtils.colorTitle(this));
            tvDarkGrey.setTextColor(ColorUtils.colorTitle(this));
            tvDarkBlue.setTextColor(ColorUtils.colorWhite(this));
            tvDark.setTextColor(ColorUtils.colorTitle(this));

            ivDarkMode.setImageResource(R.drawable.dark_blue);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    @Override
    public int setContentViewID() {
        return R.layout.activity_setting;
    }
}