package nemosofts.online.live.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.media3.common.util.UnstableApi;
import androidx.nemosofts.AppCompatActivity;
import androidx.nemosofts.theme.ThemeEngine;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import nemosofts.online.live.R;
import nemosofts.online.live.adapter.AdapterSimilar;
import nemosofts.online.live.adapter.AdapterSimilarGrid;
import nemosofts.online.live.callback.Method;
import nemosofts.online.live.cast.Casty;
import nemosofts.online.live.cast.MediaData;
import nemosofts.online.live.dialog.FeedBackDialog;
import nemosofts.online.live.dialog.ReviewDialog;
import nemosofts.online.live.dialog.TextSizeDialog;
import nemosofts.online.live.dialog.Toasty;
import nemosofts.online.live.executor.LoadLiveID;
import nemosofts.online.live.executor.LoadStatus;
import nemosofts.online.live.fragment.player.ChromecastScreenFragment;
import nemosofts.online.live.fragment.player.EmbeddedHLSFragment;
import nemosofts.online.live.fragment.player.EmbeddedImageFragment;
import nemosofts.online.live.fragment.player.EmbeddedYoutubeFragment;
import nemosofts.online.live.fragment.player.ExoPlayerFragment;
import nemosofts.online.live.fragment.player.ExternalImageFragment;
import nemosofts.online.live.fragment.player.WebsiteImageFragment;
import nemosofts.online.live.interfaces.LiveIDListener;
import nemosofts.online.live.interfaces.SuccessListener;
import nemosofts.online.live.item.ItemData;
import nemosofts.online.live.item.ItemLiveTv;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.Events;
import nemosofts.online.live.utils.GlobalBus;
import nemosofts.online.live.utils.IfSupported;
import nemosofts.online.live.utils.helper.DBHelper;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.helper.SPHelper;

public class VideoDetailsActivity extends AppCompatActivity {

    private static final String TAG = "VideoDetailsActivity";
    private String postID;
    private Toolbar toolbar;
    private Helper helper;
    private SPHelper spHelper;
    private ItemLiveTv itemLive;
    private String errorMsg = "";
    private FragmentManager fragmentManager;
    private int playerHeight;
    private FrameLayout frameLayout;
    boolean isFullScreen = false;
    private NestedScrollView mNestedScrollView;
    private ProgressBar mProgressBar;
    private RelativeLayout lytParent;
    private Casty casty;
    private int mOriginalSystemUiVisibility;
    private TextView title;
    private TextView postViews;
    private TextView avgRate;
    private RatingBar ratingBar;
    private ImageView fav;
    private WebView mWebView;
    private RecyclerView rvSimilar;
    private ArrayList<ItemData> arrayListPost;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GlobalBus.getBus().register(this);

        IfSupported.isRTL(this);
        IfSupported.isScreenshot(this);
        IfSupported.keepScreenOn(this);

        toolbar = findViewById(R.id.toolbar);

        setTitle("");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        try {
            casty = Casty.create(this).withMiniController();
        } catch (Exception e) {
            Log.e(TAG, "Error casty create", e);
        }

        Intent intent = getIntent();
        postID = intent.getStringExtra("post_id");

        helper = new Helper(this);
        spHelper = new SPHelper(this);

        fragmentManager = getSupportFragmentManager();

        arrayListPost = new ArrayList<>();

        mProgressBar = findViewById(R.id.pb);
        lytParent = findViewById(R.id.lytParent);
        mNestedScrollView = findViewById(R.id.nestedScrollView);

        title = findViewById(R.id.tv_details_title);
        ratingBar = findViewById(R.id.rb_video);
        postViews = findViewById(R.id.tv_views);
        avgRate = findViewById(R.id.tv_avg_rate);
        fav = findViewById(R.id.iv_fav);
        rvSimilar = findViewById(R.id.rv_similar);

        mWebView = findViewById(R.id.webView_det);
        mWebView.setBackgroundColor(Color.TRANSPARENT);
        mWebView.getSettings().setJavaScriptEnabled(true);

        frameLayout = findViewById(R.id.playerSection);
        int columnWidth = ApplicationUtil.getScreenWidth(VideoDetailsActivity.this);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(columnWidth, columnWidth / 2));
        playerHeight = frameLayout.getLayoutParams().height;

        getDetails();

        fav.setOnClickListener(v -> loadFav());
        findViewById(R.id.iv_sta).setOnClickListener(v -> showRateDialog());

        similarView();

        LinearLayout adView = findViewById(R.id.ll_adView);
        helper.showBannerAd(adView, "");

        LinearLayout adView2 = findViewById(R.id.ll_adView_2);
        helper.showBannerAd(adView2, "");

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleOnBack();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void similarView() {
        ImageView similar = findViewById(R.id.iv_similar);
        similar.setImageResource(Boolean.TRUE.equals(spHelper.getGridSimilar())
                ? R.drawable.ic_round_list
                : R.drawable.ic_grid_view);
        similar.setOnClickListener(v -> {
            spHelper.setGridSimilar(!spHelper.getGridSimilar());
            similar.setImageResource(Boolean.TRUE.equals(spHelper.getGridSimilar())
                    ? R.drawable.ic_round_list
                    : R.drawable.ic_grid_view);
            setSimilarAdapter();
        });
    }

    private void getDetails() {
        if (!helper.isNetworkAvailable()) {
            errorMsg = getString(R.string.err_internet_not_connected);
            setEmpty();
            return;
        }
        LoadLiveID loadBank = new LoadLiveID(new LiveIDListener() {
            @Override
            public void onStart() {
                mProgressBar.setVisibility(View.VISIBLE);
                lytParent.setVisibility(View.GONE);
            }

            @Override
            public void onEnd(String success, ArrayList<ItemLiveTv> arrayListLive,
                              ArrayList<ItemData> arrayListRelated) {
                if (!success.equals("1")) {
                    errorMsg = getString(R.string.err_server_not_connected);
                    setEmpty();
                    return;
                }
                if (arrayListLive.isEmpty()) {
                    errorMsg = getString(R.string.err_no_data_found);
                    setEmpty();
                    return;
                }

                itemLive = arrayListLive.get(0);
                if (!arrayListRelated.isEmpty()) {
                    if (!arrayListPost.isEmpty()) {
                        arrayListPost.clear();
                    }
                    arrayListPost.addAll(arrayListRelated);
                }

                if (itemLive != null) {
                    displayData();
                } else {
                    errorMsg = getString(R.string.err_no_data_found);
                    setEmpty();
                }
            }
        }, helper.getAPIRequest(Method.METHOD_LIVE_ID, 0, postID, "", "",
                "", new SPHelper(this).getUserId(), "", "",
                "", "", "", "", "", null));
        loadBank.execute();
    }

    private void displayData() {
        title.setText(itemLive.getLiveTitle());
        ratingBar.setRating(Integer.parseInt(itemLive.getAverageRating()));
        avgRate.setText(itemLive.getAverageRating());
        postViews.setText(getString(R.string.count, ApplicationUtil.format(Integer.parseInt(itemLive.getTotalViews()))));
        changeFav(itemLive.getIsFav());

        addRecent();
        setDescription();
        initPlayer();
        setSimilarAdapter();
        setEmpty();
    }

    private void addRecent() {
        try {
            DBHelper dbHelper = new DBHelper(VideoDetailsActivity.this);
            if (!itemLive.getIsPremium()) {
                dbHelper.addToRecent(itemLive);
            } else {
                if (spHelper.getIsSubscribed()) {
                    dbHelper.addToRecent(itemLive);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error addToRecent", e);
        }
    }

    private void setSimilarAdapter() {
        if (!arrayListPost.isEmpty()) {
            findViewById(R.id.ll_similar).setVisibility(View.VISIBLE);
            if (Boolean.TRUE.equals(spHelper.getGridSimilar())) {
                GridLayoutManager gridFresh = new GridLayoutManager(this, 2);
                gridFresh.setSpanCount(2);
                rvSimilar.setLayoutManager(gridFresh);
                rvSimilar.setItemAnimator(new DefaultItemAnimator());
                AdapterSimilarGrid adapterPostHome = new AdapterSimilarGrid(this, arrayListPost, (itemData, position) -> {
                    postID = arrayListPost.get(position).getId();
                    mNestedScrollView.scrollTo(0, 0);
                    getDetails();
                });
                rvSimilar.setAdapter(adapterPostHome);
            } else {
                AdapterSimilar adapter = new AdapterSimilar(this, arrayListPost, (itemPost, position) -> {
                    postID = arrayListPost.get(position).getId();
                    mNestedScrollView.scrollTo(0, 0);
                    getDetails();
                });
                LinearLayoutManager llm = new LinearLayoutManager(VideoDetailsActivity.this);
                rvSimilar.setLayoutManager(llm);
                rvSimilar.setItemAnimator(new DefaultItemAnimator());
                rvSimilar.setAdapter(adapter);
            }
        } else {
            findViewById(R.id.ll_similar).setVisibility(View.GONE);
        }
    }

    private void initPlayer() {
        if (itemLive.getLiveURL().isEmpty()) {
            EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(itemLive.getLiveURL(), itemLive.getImage(), false);
            fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
        } else {
            intPlayerView();
        }

        if (casty == null) {
            return;
        }
        casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {
                // this method is empty
            }

            @Override
            public void onDisconnected() {
                intPlayerView();
            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void intPlayerView() {
        switch (itemLive.getLiveType()) {
            case "hls", "rtmp", "dash", "mp4" -> {
                ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(
                        itemLive.getLiveURL(), itemLive.getLiveTitle(), itemLive.getUserAgentName(), itemLive.isUserAgent()
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, exoPlayerFragment).commitAllowingStateLoss();
            }
            case "embedded" -> {
                EmbeddedImageFragment embeddedImageFragment = EmbeddedImageFragment.newInstance(
                        itemLive.getLiveURL(), itemLive.getImage(), true
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedImageFragment).commitAllowingStateLoss();
            }
            case "webview" -> {
                WebsiteImageFragment websiteImageFragment = WebsiteImageFragment.newInstance(
                        itemLive.getLiveURL(), itemLive.getImage(), true
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, websiteImageFragment).commitAllowingStateLoss();
            }
            case "youtube", "youtube_live" -> {
                String videoId2 = ApplicationUtil.getVideoId(itemLive.getLiveURL());
                EmbeddedYoutubeFragment embeddedYoutubePlayerActivity = EmbeddedYoutubeFragment.newInstance(videoId2, true);
                fragmentManager.beginTransaction().replace(R.id.playerSection, embeddedYoutubePlayerActivity).commitAllowingStateLoss();
            }
            case "browser" -> {
                ExternalImageFragment externalImageFragment = ExternalImageFragment.newInstance(itemLive.getLiveURL(),
                        itemLive.getImage(), true, "browser"
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, externalImageFragment).commitAllowingStateLoss();
            }
            case "external" -> {
                ExternalImageFragment externalFragment = ExternalImageFragment.newInstance(itemLive.getLiveURL(),
                        itemLive.getImage(), true, "player"
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, externalFragment).commitAllowingStateLoss();
            }
            case "player" -> {
                EmbeddedHLSFragment hlsExternalFragment = EmbeddedHLSFragment.newInstance(
                        itemLive.getLiveURL(), itemLive.getImage(), itemLive.getPlayerType(),
                        itemLive.getLiveTitle(), itemLive.getUserAgentName(), itemLive.isUserAgent()
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, hlsExternalFragment).commitAllowingStateLoss();
            }
            default -> {
                WebsiteImageFragment websiteImageFragment = WebsiteImageFragment.newInstance(
                        itemLive.getLiveURL(), itemLive.getImage(), true
                );
                fragmentManager.beginTransaction().replace(R.id.playerSection, websiteImageFragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (casty != null) {
            casty.addMediaRouteMenuItem(menu);
        }
        getMenuInflater().inflate(R.menu.menu_details, menu);
        menu.findItem(R.id.menu_cast_play).setVisible(casty != null && casty.isConnected());
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.menu_cast_play) {
            playViaCast();
        } else if (id == R.id.menu_fields) {
            if (itemLive != null) {
                setTextSize();
            }
        } else if (id == R.id.menu_feedback) {
            if (itemLive != null) {
                new FeedBackDialog(this).showDialog(itemLive.getId(), itemLive.getLiveTitle());
            }
        } else if (id == R.id.menu_share) {
            if (itemLive != null) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_the_app));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, itemLive.getLiveTitle()
                        + "\n\nvia "
                        + getResources().getString(R.string.app_name)
                        + " - http://play.google.com/store/apps/details?id="
                        + getPackageName()
                );
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_the_app)));
            }
        } else {
            return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    private void setTextSize() {
        new TextSizeDialog(this, new SuccessListener() {
            @Override
            public void onStart() {
                // this method is empty
            }

            @Override
            public void onEnd(String success, String registerSuccess, String message) {
                setDescription();
            }
        }).showDialog();
    }

    private void playViaCast() {
        if (itemLive.getLiveType().equals("hls") || itemLive.getLiveType().equals("rtmp")
                || itemLive.getLiveType().equals("dash") || itemLive.getLiveType().equals("mp4")) {
            casty.getPlayer().loadMediaAndPlay(createSampleMediaData(itemLive.getLiveURL(), itemLive.getLiveTitle(), itemLive.getImage()));
            ChromecastScreenFragment chromecast = new ChromecastScreenFragment();
            fragmentManager.beginTransaction().replace(R.id.playerSection, chromecast).commitAllowingStateLoss();
        } else {
            Toast.makeText(VideoDetailsActivity.this, getResources().getString(R.string.cast_youtube), Toast.LENGTH_SHORT).show();
        }
    }

    private MediaData createSampleMediaData(@NonNull String videoUrl, String videoTitle, String videoImage) {
        if (videoUrl.endsWith(".mp4")) {
            return new MediaData.Builder(videoUrl)
                    .setStreamType(MediaData.STREAM_TYPE_BUFFERED)
                    .setContentType(getType(videoUrl))
                    .setMediaType(MediaData.MEDIA_TYPE_MOVIE)
                    .setTitle(videoTitle)
                    .setSubtitle(getString(R.string.app_name))
                    .addPhotoUrl(videoImage)
                    .build();
        } else {
            return new MediaData.Builder(videoUrl)
                    .setStreamType(MediaData.STREAM_TYPE_BUFFERED)
                    .setContentType(getType(videoUrl))
                    .setMediaType(MediaData.MEDIA_TYPE_TV_SHOW)
                    .setTitle(videoTitle)
                    .setSubtitle(getString(R.string.app_name))
                    .addPhotoUrl(videoImage)
                    .build();
        }
    }

    @NonNull
    private String getType(@NonNull String videoUrl) {
        if (videoUrl.endsWith(".mp4")) {
            return "videos/mp4";
        } else if (videoUrl.endsWith(".m3u8")) {
            return "application/x-mpegurl";
        } else {
            return "application/x-mpegurl";
        }
    }

    private void setEmpty() {
        if (itemLive != null) {
            mProgressBar.setVisibility(View.GONE);
            lytParent.setVisibility(View.VISIBLE);
        } else {
            if (!errorMsg.isEmpty()) {
                Toast.makeText(VideoDetailsActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
            mProgressBar.setVisibility(View.GONE);
            lytParent.setVisibility(View.GONE);
        }
    }

    @Override
    public int setContentViewID() {
        return R.layout.activity_video_details;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getFullScreen(@NonNull Events.FullScreen fullScreen) {
        isFullScreen = fullScreen.isFullScreen();
        if (fullScreen.isFullScreen()) {
            gotoFullScreen();
        } else {
            gotoPortraitScreen();
        }
    }

    private void gotoPortraitScreen() {
        mNestedScrollView.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, playerHeight));
        getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
    }

    private void gotoFullScreen() {
        mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
        mNestedScrollView.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        frameLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().getDecorView().setSystemUiVisibility(3846);
    }

    private void handleOnBack() {
        if (isFullScreen) {
            Events.FullScreen fullScreen = new Events.FullScreen();
            fullScreen.setFullScreen(false);
            GlobalBus.getBus().post(fullScreen);
        } else {
            finish();
        }
    }

    private void loadFav() {
        if (!helper.isNetworkAvailable()) {
            Toasty.makeText(VideoDetailsActivity.this, getString(R.string.err_internet_not_connected), Toasty.ERROR);
            return;
        }
        LoadStatus loadFav = new LoadStatus(new SuccessListener() {
            @Override
            public void onStart() {
                changeFav(!itemLive.getIsFav());
            }

            @Override
            public void onEnd(String success, String favSuccess, String message) {
                if (success.equals("1")) {
                    itemLive.setIsFav(message.equals("Added to Favourite"));
                    changeFav(itemLive.getIsFav());
                    Toasty.makeText(VideoDetailsActivity.this, message, Toasty.SUCCESS);
                } else {
                    Toasty.makeText(VideoDetailsActivity.this, getString(R.string.err_server_not_connected), Toasty.ERROR);
                }
            }
        }, helper.getAPIRequest(Method.METHOD_DO_FAV, 0, itemLive.getId(), "", "",
                "", spHelper.getUserId(), "", "", "", "",
                "", "", "", null));
        loadFav.execute();
    }

    private void showRateDialog() {
        if (!helper.isNetworkAvailable()) {
            Toasty.makeText(VideoDetailsActivity.this, getString(R.string.err_internet_not_connected), Toasty.ERROR);
            return;
        }
        ReviewDialog reviewDialog = new ReviewDialog(this, new ReviewDialog.RatingDialogListener() {
            @Override
            public void onShow() {
                // this method is empty
            }

            @Override
            public void onGetRating(String rating, String message) {
                itemLive.setUserRating(String.valueOf(rating));
                itemLive.setUserMessage(message);
            }

            @Override
            public void onDismiss(String success, String rateSuccess, String message, int rating,
                                  String userRating, String userMessage) {
                if (!success.equals("1")) {
                    Toast.makeText(VideoDetailsActivity.this, getString(R.string.err_server_not_connected), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (rateSuccess.equals("1")) {
                    try {
                        itemLive.setAverageRating(String.valueOf(rating));
                        itemLive.setTotalRate(String.valueOf(Integer.parseInt(itemLive.getTotalRate() + 1)));
                        itemLive.setUserRating(String.valueOf(userRating));
                        itemLive.setUserMessage(String.valueOf(userMessage));
                        ratingBar.setRating(rating);
                    } catch (Exception e) {
                        Log.e(TAG, "Error rate", e);
                    }
                }
                Toasty.makeText(VideoDetailsActivity.this, message, Toasty.SUCCESS);
            }
        });
        reviewDialog.showDialog(itemLive.getId(), itemLive.getUserRating(), itemLive.getUserMessage());
    }

    public void changeFav(Boolean isFav) {
        if (Boolean.TRUE.equals(isFav)) {
            fav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_turned_in));
        } else {
            fav.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_turned_in_not));
        }
    }

    private void setDescription() {
        String htmlText = itemLive.getLiveDescription();
        String htmlString = getHtmlString(htmlText);
        mWebView.loadDataWithBaseURL("", htmlString, "text/html", "utf-8", null);
    }

    @NonNull
    private String getHtmlString(String htmlText) {
        String textSize = getTextSize();
        String myCustomStyleString;
        if (Boolean.TRUE.equals(new ThemeEngine(this).getIsThemeMode())) {
            myCustomStyleString = "<style channelType=\"text/css\">body,* {color:#DBDBDB; font-family: MyFont;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>"
                    + "<style type=\"text/css\">" + textSize + "</style>";
        } else {
            myCustomStyleString = "<style channelType=\"text/css\">body,* {color:#161616; font-family: MyFont; text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>"
                    + "<style type=\"text/css\">" + textSize + "</style>";
        }

        String htmlString;
        if (Boolean.FALSE.equals(spHelper.getIsRTL())) {
            htmlString = myCustomStyleString + "<div>" + htmlText + "</div>";
        } else {
            htmlString = "<html dir=\"rtl\" lang=\"\"><body>" + myCustomStyleString + "<div>" + htmlText + "</div>" + "</body></html>";
        }
        return htmlString;
    }

    @NonNull
    private String getTextSize() {
        String textSize;
        int textData = spHelper.getTextSize();
        if (0 == textData) {
            textSize = "body{font-size:12px;}";
        } else if (1 == textData) {
            textSize = "body{font-size:14px;}";
        } else if (2 == textData) {
            textSize = "body{font-size:16px;}";
        } else if (3 == textData) {
            textSize = "body{font-size:17px;}";
        } else if (4 == textData) {
            textSize = "body{font-size:20px;}";
        } else if (5 == textData) {
            textSize = "body{font-size:25px;}";
        } else {
            textSize = "body{font-size:14px;}";
        }
        return textSize;
    }
}