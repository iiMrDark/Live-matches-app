package nemosofts.online.live.utils.helper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.nemosofts.view.SmoothCheckBox;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdk;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.ads.mediation.facebook.FacebookMediationAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.LevelPlayInterstitialListener;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.banner.BannerAd;
import com.wortise.ads.interstitial.InterstitialAd;
import com.wortise.ads.rewarded.RewardedAd;
import com.wortise.ads.rewarded.models.Reward;
import com.yandex.mobile.ads.banner.BannerAdView;
import com.yandex.mobile.ads.common.AdError;
import com.yandex.mobile.ads.common.ImpressionData;
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;

import nemosofts.online.live.R;
import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.callback.Method;
import nemosofts.online.live.interfaces.InterAdListener;
import nemosofts.online.live.interfaces.RewardAdListener;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.advertising.AdManagerInterAdmob;
import nemosofts.online.live.utils.advertising.AdManagerInterApplovin;
import nemosofts.online.live.utils.advertising.AdManagerInterStartApp;
import nemosofts.online.live.utils.advertising.AdManagerInterUnity;
import nemosofts.online.live.utils.advertising.AdManagerInterWortise;
import nemosofts.online.live.utils.advertising.AdManagerInterYandex;
import nemosofts.online.live.utils.advertising.GDPRChecker;
import nemosofts.online.live.utils.advertising.RewardAdAdmob;
import nemosofts.online.live.utils.advertising.RewardAdApplovin;
import nemosofts.online.live.utils.advertising.RewardAdStartApp;
import nemosofts.online.live.utils.advertising.RewardAdUnity;
import nemosofts.online.live.utils.advertising.RewardAdWortise;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Helper {

    private final Context ctx;
    private InterAdListener interAdListener;
    boolean isRewarded = false;

    public Helper(Context ctx) {
        this.ctx = ctx;
    }

    public Helper(Context ctx, InterAdListener interAdListener) {
        this.ctx = ctx;
        this.interAdListener = interAdListener;
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            Network network = cm.getActiveNetwork();
            if (network != null) {
                NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
                return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            }
        }
        return false;
    }

    public RequestBody getAPIRequest(String helper_name, int page, String itemID, String catID,
                                     String searchText, String reportMessage, String userID,
                                     String name, String email, String mobile, String gender,
                                     String password, String authID, String loginType, File file) {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(gson);
        jsObj.addProperty("helper_name", helper_name);
        jsObj.addProperty("application_id", ctx.getPackageName());

        if (Method.METHOD_APP_DETAILS.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_LOGIN.equals(helper_name)){
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_password", password);
            jsObj.addProperty("auth_id", authID);
            jsObj.addProperty("type", loginType);
        } else if (Method.METHOD_REGISTER.equals(helper_name)){
            jsObj.addProperty("user_name", name);
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_phone", mobile);
            jsObj.addProperty("user_gender", gender);
            jsObj.addProperty("user_password", password);
            jsObj.addProperty("auth_id", authID);
            jsObj.addProperty("type", loginType);
        } else if (Method.METHOD_PROFILE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_ACCOUNT_DELETE.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_EDIT_PROFILE.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("user_name", name);
            jsObj.addProperty("user_email", email);
            jsObj.addProperty("user_phone", mobile);
            jsObj.addProperty("user_password", password);
        } else if (Method.METHOD_USER_IMAGES_UPDATE.equals(helper_name)){
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("type", loginType);
        } else if (Method.METHOD_FORGOT_PASSWORD.equals(helper_name)){
            jsObj.addProperty("user_email", email);
        } else if (Method.METHOD_NOTIFICATION.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_REMOVE_NOTIFICATION.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_REPORT.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("report_title", searchText);
            jsObj.addProperty("report_msg", reportMessage);
        } else if (Method.METHOD_GET_RATINGS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("device_id", userID);
        } else if (Method.METHOD_RATINGS.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("device_id", userID);
            jsObj.addProperty("rate", authID);
            jsObj.addProperty("message", reportMessage);
        }

        else if (Method.METHOD_HOME.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            if(!itemID.equals("")) {
                jsObj.addProperty("post_ids", itemID);
            }
        } else if (Method.METHOD_HOME_DETAILS.equals(helper_name)) {
            jsObj.addProperty("id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_LATEST.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Method.METHOD_MOST_VIEWED.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Method.METHOD_CAT.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("search_text", searchText);
            jsObj.addProperty("search_type", loginType);
        } else if (Method.METHOD_LIVE_RECENT.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("post_ids", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_LIVE_ID.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_CAT_ID.equals(helper_name)){
            jsObj.addProperty("cat_id", catID);
            jsObj.addProperty("page", String.valueOf(page));
        } else if (Method.METHOD_POST_BY_FAV.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_POST_BY_BANNER.equals(helper_name)){
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("post_id", itemID);
        } else if (Method.METHOD_DO_FAV.equals(helper_name)) {
            jsObj.addProperty("post_id", itemID);
            jsObj.addProperty("user_id", userID);
        } else if (Method.METHOD_EVENT.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
        }

        else if (Method.METHOD_SEARCH_LIVE.equals(helper_name)) {
            jsObj.addProperty("page", String.valueOf(page));
            jsObj.addProperty("search_text", searchText);
        } else if (Method.METHOD_SEARCH.equals(helper_name)) {
            jsObj.addProperty("search_text", searchText);
        }

        else if (Method.METHOD_SUGGESTION.equals(helper_name)) {
            jsObj.addProperty("user_id", userID);
            jsObj.addProperty("suggest_title", searchText);
            jsObj.addProperty("suggest_message", reportMessage);
        }

        else if (Method.TRANSACTION_URL.equals(helper_name)){
            jsObj.addProperty("planId", itemID);
            jsObj.addProperty("planName", catID);
            jsObj.addProperty("planPrice", searchText);
            jsObj.addProperty("planDuration", reportMessage);
            jsObj.addProperty("planCurrencyCode", name);
            jsObj.addProperty("user_id", userID);
        }

        switch (helper_name) {
            case Method.METHOD_REGISTER, Method.METHOD_SUGGESTION, Method.METHOD_USER_IMAGES_UPDATE -> {
                final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");
                MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
                if (file != null) {
                    builder.addFormDataPart("image_data", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file));
                }
                return builder.addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString())).build();
            }
            default -> {
                return new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("data", ApplicationUtil.toBase64(jsObj.toString()))
                        .build();
            }
        }
    }

    public void initializeAds() {
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_ADMOB) || Callback.getAdNetwork().equals(Callback.AD_TYPE_META)) {
            MobileAds.initialize(ctx, initializationStatus -> {
            });
        }
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_STARTAPP)) {
            StartAppSDK.init(ctx, Callback.getStartappAppID(), false);
            StartAppAd.disableSplash();
            StartAppSDK.setUserConsent(ctx, "pas", System.currentTimeMillis(), new GDPRChecker(ctx).canLoadAd());
        }
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_APPLOVIN) && (!AppLovinSdk.getInstance(ctx).isInitialized())) {
            AppLovinSdk.initializeSdk(ctx);
            AppLovinSdk.getInstance(ctx).setMediationProvider("max");
            AppLovinSdk.getInstance(ctx).getSettings()
                    .setTestDeviceAdvertisingIds(Arrays.asList("656822d9-18de-4120-994e-44d4245a4d63", "249d75a2-1ef2-8ff9-8885-c50384843a66"));
        }
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_IRONSOURCE)) {
            IronSource.init(ctx, Callback.getIronsourceAppKey(), () -> {
            });
        }
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_UNITY)) {
            UnityAds.initialize(ctx, Callback.getUnityGameID(), true,
                    new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        // document why this method is empty
                    }

                    @Override
                    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error,
                                                       String message) {
                        // document why this method is empty
                    }
            });
        }
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_YANDEX)) {
            com.yandex.mobile.ads.common.MobileAds.initialize(ctx, () -> {
            });
        }
        if (Callback.getAdNetwork().equals(Callback.AD_TYPE_WORTISE) && !WortiseSdk.isInitialized()) {
            WortiseSdk.initialize(ctx, Callback.getWortiseAppID());
        }
    }

    public Object showBannerAd(LinearLayout linearLayout, String page) {
        if (isBannerAd(page)){
            switch (Callback.getAdNetwork()) {
                case Callback.AD_TYPE_ADMOB:
                case Callback.AD_TYPE_META:
                    Bundle extras = new Bundle();
                    AdView adViewAdmob = new AdView(ctx);
                    AdRequest adRequest;
                    if (Callback.getAdNetwork().equals(Callback.AD_TYPE_ADMOB)) {
                        adRequest = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .build();
                    } else {
                        adRequest = new AdRequest.Builder()
                                .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                                .addNetworkExtrasBundle(FacebookMediationAdapter.class, extras)
                                .build();
                    }
                    adViewAdmob.setAdUnitId(Callback.getAdmobBannerAdID());
                    adViewAdmob.setAdSize(AdSize.BANNER);
                    linearLayout.addView(adViewAdmob);
                    adViewAdmob.loadAd(adRequest);
                    return adViewAdmob;
                case Callback.AD_TYPE_WORTISE:
                    BannerAd mBannerAd = new BannerAd(ctx);
                    mBannerAd.setAdSize(com.wortise.ads.AdSize.HEIGHT_50);
                    mBannerAd.setAdUnitId(Callback.getWortiseBannerAdID());
                    linearLayout.addView(mBannerAd);
                    mBannerAd.loadAd();
                    return mBannerAd;
                case Callback.AD_TYPE_STARTAPP:
                    Banner startAppBanner = new Banner(ctx);
                    startAppBanner.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.
                            LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    linearLayout.addView(startAppBanner);
                    startAppBanner.loadAd();
                    return startAppBanner;
                case Callback.AD_TYPE_UNITY:
                    BannerView bannerView = new BannerView((Activity) ctx,
                            Callback.getUnityBannerAdID(), new UnityBannerSize(320, 50));
                    linearLayout.addView(bannerView);
                    bannerView.load();
                    return bannerView;
                case Callback.AD_TYPE_APPLOVIN:
                    MaxAdView adView = new MaxAdView(Callback.getApplovinBannerAdID(), ctx);
                    int width = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx = ctx.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    adView.setLayoutParams(new FrameLayout.LayoutParams(width, heightPx));
                    linearLayout.addView(adView);
                    adView.loadAd();
                    return adView;
                case Callback.AD_TYPE_IRONSOURCE:
                    IronSourceBannerLayout iBannerAd  = IronSource.createBanner((Activity) ctx, ISBannerSize.BANNER);
                    linearLayout.addView(iBannerAd);
                    IronSource.loadBanner(iBannerAd);
                    return iBannerAd;
                case Callback.AD_TYPE_YANDEX:
                    BannerAdView yBannerAd = new BannerAdView(ctx);
                    int width2 = ViewGroup.LayoutParams.MATCH_PARENT;
                    int heightPx2 = ctx.getResources().getDimensionPixelSize(R.dimen.banner_height);
                    yBannerAd.setLayoutParams(new FrameLayout.LayoutParams(width2, heightPx2));
                    yBannerAd.setAdUnitId(Callback.getYandexBannerAdID());
                    com.yandex.mobile.ads.common.AdRequest yadRequest = new com.yandex.mobile.ads.common.AdRequest.Builder().build();
                    linearLayout.addView(yBannerAd);
                    yBannerAd.loadAd(yadRequest);
                    return yBannerAd;
                default:
                    return null;
            }
        } else {
            return null;
        }
    }

    public void showInterAd(final int pos, final String type) {
        if (isInterAd()){
            switch (Callback.getAdNetwork()) {
                case Callback.AD_TYPE_ADMOB:
                case Callback.AD_TYPE_META:
                    final AdManagerInterAdmob adManagerInterAdmob = new AdManagerInterAdmob(ctx);
                    if (adManagerInterAdmob.getAd() != null) {
                        adManagerInterAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                AdManagerInterAdmob.setAd(null);
                                adManagerInterAdmob.createAd();
                                interAdListener.onClick(pos, type);
                                super.onAdDismissedFullScreenContent();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                AdManagerInterAdmob.setAd(null);
                                adManagerInterAdmob.createAd();
                                interAdListener.onClick(pos, type);
                                super.onAdFailedToShowFullScreenContent(adError);
                            }
                        });
                        adManagerInterAdmob.getAd().show((Activity) ctx);
                    } else {
                        AdManagerInterAdmob.setAd(null);
                        adManagerInterAdmob.createAd();
                        interAdListener.onClick(pos, type);
                    }
                    break;
                case Callback.AD_TYPE_STARTAPP:
                    final AdManagerInterStartApp adManagerInterStartApp = new AdManagerInterStartApp(ctx);
                    if (adManagerInterStartApp.getAd() != null && adManagerInterStartApp.getAd().isReady()) {
                        adManagerInterStartApp.getAd().showAd(new AdDisplayListener() {
                            @Override
                            public void adHidden(Ad ad) {
                                AdManagerInterStartApp.setAd(null);
                                adManagerInterStartApp.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void adDisplayed(Ad ad) {
                                // this method is empty
                            }

                            @Override
                            public void adClicked(Ad ad) {
                                // this method is empty
                            }

                            @Override
                            public void adNotDisplayed(Ad ad) {
                                AdManagerInterStartApp.setAd(null);
                                adManagerInterStartApp.createAd();
                                interAdListener.onClick(pos, type);
                            }
                        });
                    } else {
                        AdManagerInterStartApp.setAd(null);
                        adManagerInterStartApp.createAd();
                        interAdListener.onClick(pos, type);
                    }
                    break;
                case Callback.AD_TYPE_UNITY:
                    final AdManagerInterUnity adManagerInterUnity = new AdManagerInterUnity();
                    if (AdManagerInterUnity.getAd()) {
                        UnityAds.show((Activity) ctx, Callback.getUnityInterstitialAdID(), new IUnityAdsShowListener() {
                            @Override
                            public void onUnityAdsShowFailure(String placementId,
                                                              UnityAds.UnityAdsShowError error,
                                                              String message) {
                                AdManagerInterUnity.setAd();
                                adManagerInterUnity.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onUnityAdsShowStart(String placementId) {
                                // this method is empty
                            }

                            @Override
                            public void onUnityAdsShowClick(String placementId) {
                                // this method is empty
                            }

                            @Override
                            public void onUnityAdsShowComplete(String placementId,
                                                               UnityAds.UnityAdsShowCompletionState state) {
                                AdManagerInterUnity.setAd();
                                adManagerInterUnity.createAd();
                                interAdListener.onClick(pos, type);
                            }
                        });
                    } else {
                        AdManagerInterUnity.setAd();
                        adManagerInterUnity.createAd();
                        interAdListener.onClick(pos, type);
                    }
                    break;
                case Callback.AD_TYPE_APPLOVIN:
                    final AdManagerInterApplovin adManagerInterApplovin = new AdManagerInterApplovin(ctx);
                    if (adManagerInterApplovin.getAd() != null && adManagerInterApplovin.getAd().isReady()) {
                        adManagerInterApplovin.getAd().setListener(new MaxAdListener() {
                            @Override
                            public void onAdLoaded(@NonNull MaxAd ad) {
                                // this method is empty
                            }

                            @Override
                            public void onAdDisplayed(@NonNull MaxAd ad) {
                                // this method is empty
                            }

                            @Override
                            public void onAdHidden(@NonNull MaxAd ad) {
                                AdManagerInterApplovin.setAd(null);
                                adManagerInterApplovin.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onAdClicked(@NonNull MaxAd ad) {
                                // this method is empty
                            }

                            @Override
                            public void onAdLoadFailed(@NonNull String adUnitId, @NonNull MaxError error) {
                                AdManagerInterApplovin.setAd(null);
                                adManagerInterApplovin.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onAdDisplayFailed(@NonNull MaxAd ad, @NonNull MaxError error) {
                                AdManagerInterApplovin.setAd(null);
                                adManagerInterApplovin.createAd();
                                interAdListener.onClick(pos, type);
                            }
                        });
                        adManagerInterApplovin.getAd().showAd();
                    } else {
                        AdManagerInterStartApp.setAd(null);
                        adManagerInterApplovin.createAd();
                        interAdListener.onClick(pos, type);
                    }
                    break;
                case Callback.AD_TYPE_IRONSOURCE:
                    if (IronSource.isInterstitialReady()) {
                        IronSource.setLevelPlayInterstitialListener(new LevelPlayInterstitialListener() {
                            @Override
                            public void onAdReady(AdInfo adInfo) {
                                // this method is empty
                            }

                            @Override
                            public void onAdLoadFailed(IronSourceError ironSourceError) {
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onAdOpened(AdInfo adInfo) {
                                // this method is empty
                            }

                            @Override
                            public void onAdShowSucceeded(AdInfo adInfo) {
                                // this method is empty
                            }

                            @Override
                            public void onAdShowFailed(IronSourceError ironSourceError, AdInfo adInfo) {
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onAdClicked(AdInfo adInfo) {
                                // this method is empty
                            }

                            @Override
                            public void onAdClosed(AdInfo adInfo) {
                                interAdListener.onClick(pos, type);
                            }
                        });
                        IronSource.showInterstitial();
                    } else {
                        interAdListener.onClick(pos, type);
                    }
                    IronSource.init(ctx, Callback.getIronsourceAppKey(), IronSource.AD_UNIT.INTERSTITIAL);
                    IronSource.loadInterstitial();
                    break;
                case Callback.AD_TYPE_YANDEX:
                    final AdManagerInterYandex adManagerInterYandex = new AdManagerInterYandex(ctx);
                    if (adManagerInterYandex.getAd() != null) {
                        adManagerInterYandex.getAd().setAdEventListener(new InterstitialAdEventListener() {
                            @Override
                            public void onAdShown() {
                                // this method is empty
                            }

                            @Override
                            public void onAdFailedToShow(@NonNull AdError adError) {
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onAdDismissed() {
                                AdManagerInterYandex.setAd(null);
                                adManagerInterYandex.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onAdClicked() {
                                // this method is empty
                            }

                            @Override
                            public void onAdImpression(@Nullable ImpressionData impressionData) {
                                // this method is empty
                            }
                        });
                        adManagerInterYandex.getAd().show((Activity) ctx);
                    } else {
                        AdManagerInterYandex.setAd(null);
                        adManagerInterYandex.createAd();
                        interAdListener.onClick(pos, type);
                    }
                    break;
                case Callback.AD_TYPE_WORTISE:
                    final AdManagerInterWortise adManagerInterWortise = new AdManagerInterWortise(ctx);
                    if (adManagerInterWortise.getAd() != null && adManagerInterWortise.getAd().isAvailable()) {
                        adManagerInterWortise.getAd().setListener(new InterstitialAd.Listener() {

                            @Override
                            public void onInterstitialFailedToLoad(@NonNull InterstitialAd interstitialAd,
                                                                   @NonNull com.wortise.ads.AdError adError) {
                                AdManagerInterWortise.setAd(null);
                                adManagerInterWortise.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onInterstitialShown(@NonNull InterstitialAd interstitialAd) {
                                // this method is empty
                            }

                            @Override
                            public void onInterstitialLoaded(@NonNull InterstitialAd interstitialAd) {
                                // this method is empty
                            }

                            @Override
                            public void onInterstitialImpression(@NonNull InterstitialAd interstitialAd) {
                                // this method is empty
                            }

                            @Override
                            public void onInterstitialFailedToShow(@NonNull InterstitialAd interstitialAd,
                                                                   @NonNull com.wortise.ads.AdError adError) {
                                AdManagerInterWortise.setAd(null);
                                adManagerInterWortise.createAd();
                                interAdListener.onClick(pos, type);
                            }


                            @Override
                            public void onInterstitialDismissed(@NonNull InterstitialAd interstitialAd) {
                                AdManagerInterWortise.setAd(null);
                                adManagerInterWortise.createAd();
                                interAdListener.onClick(pos, type);
                            }

                            @Override
                            public void onInterstitialClicked(@NonNull InterstitialAd interstitialAd) {
                                // this method is empty
                            }
                        });
                        adManagerInterWortise.getAd().showAd();
                    } else {
                        AdManagerInterWortise.setAd(null);
                        adManagerInterWortise.createAd();
                        interAdListener.onClick(pos, type);
                    }
                    break;
                default : interAdListener.onClick(pos, type);
                    break;
            }
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public void showRewardAds(int pos, RewardAdListener rewardAdListener) {
        if (Boolean.TRUE.equals(Callback.getIsRewardAd())
                && Boolean.TRUE.equals(Callback.getIsAdsStatus())
                && Boolean.TRUE.equals(!new SPHelper(ctx).getIsSubscribed())) {
            if (Boolean.TRUE.equals(new SPHelper(ctx).getIsRewardAdWarned())) {
                loadRewardAds(rewardAdListener, pos);
            } else {
                openRewardVideoAdAlert(rewardAdListener, pos);
            }
        } else {
            rewardAdListener.onClick(false, pos);
        }
    }


    public void loadRewardAds(RewardAdListener rewardAdListener, int pos) {
        if (new GDPRChecker(ctx).canLoadAd()) {
            switch (Callback.getAdNetwork()) {
                case Callback.AD_TYPE_ADMOB :
                    final RewardAdAdmob rewardAdAdmob = new RewardAdAdmob(ctx);
                    if (rewardAdAdmob.getAd() != null) {
                        rewardAdAdmob.getAd().setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                RewardAdAdmob.setAd(null);
                                rewardAdAdmob.createAd();
                                if (isRewarded) {
                                    rewardAdListener.onClick(true, pos);
                                }
                                super.onAdDismissedFullScreenContent();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                                RewardAdAdmob.setAd(null);
                                rewardAdAdmob.createAd();
                                rewardAdListener.onClick(false, pos);
                                super.onAdFailedToShowFullScreenContent(adError);
                            }
                        });
                        rewardAdAdmob.getAd().show((Activity) ctx, rewardItem -> isRewarded = true);
                    } else {
                        RewardAdAdmob.setAd(null);
                        rewardAdAdmob.createAd();
                        rewardAdListener.onClick(false, pos);
                    }
                    break;
                case Callback.AD_TYPE_WORTISE :
                    final RewardAdWortise rewardAdWortise = new RewardAdWortise(ctx);
                    if (rewardAdWortise.getAd() != null && rewardAdWortise.getAd().isAvailable()) {
                        rewardAdWortise.getAd().setListener(new RewardedAd.Listener() {

                            @Override
                            public void onRewardedShown(@NonNull RewardedAd rewardedAd) {
                                // this method is empty
                            }

                            @Override
                            public void onRewardedLoaded(@NonNull RewardedAd rewardedAd) {
                                // this method is empty
                            }

                            @Override
                            public void onRewardedImpression(@NonNull RewardedAd rewardedAd) {
                                // this method is empty
                            }

                            @Override
                            public void onRewardedFailedToShow(@NonNull RewardedAd rewardedAd,
                                                               @NonNull com.wortise.ads.AdError adError) {
                                RewardAdWortise.setAd(null);
                                rewardAdWortise.createAd();
                                rewardAdListener.onClick(false, pos);
                            }

                            @Override
                            public void onRewardedFailedToLoad(@NonNull RewardedAd rewardedAd,
                                                               @NonNull com.wortise.ads.AdError adError) {
                                RewardAdWortise.setAd(null);
                                rewardAdWortise.createAd();
                                rewardAdListener.onClick(false, pos);
                            }

                            @Override
                            public void onRewardedDismissed(@NonNull RewardedAd rewardedAd) {
                                RewardAdWortise.setAd(null);
                                rewardAdWortise.createAd();
                                if (isRewarded) {
                                    rewardAdListener.onClick(true, pos);
                                }
                            }

                            @Override
                            public void onRewardedCompleted(@NonNull RewardedAd rewardedAd,
                                                            @NonNull Reward reward) {
                                isRewarded = true;
                            }

                            @Override
                            public void onRewardedClicked(@NonNull RewardedAd rewardedAd) {
                                // this method is empty
                            }
                        });
                        rewardAdWortise.getAd().showAd();
                    } else {
                        RewardAdWortise.setAd(null);
                        rewardAdWortise.createAd();
                        rewardAdListener.onClick(false, pos);
                    }
                    break;
                case Callback.AD_TYPE_STARTAPP :
                    final RewardAdStartApp rewardAdStartApp = new RewardAdStartApp(ctx);
                    if (rewardAdStartApp.getAd() != null && rewardAdStartApp.getAd().isReady()) {
                        rewardAdStartApp.getAd().showAd(new AdDisplayListener() {
                            @Override
                            public void adHidden(Ad ad) {
                                RewardAdStartApp.setAd(null);
                                rewardAdStartApp.createAd();
                                if (isRewarded) {
                                    rewardAdListener.onClick(true, pos);
                                }
                            }

                            @Override
                            public void adDisplayed(Ad ad) {
                                // this method is empty
                            }

                            @Override
                            public void adClicked(Ad ad) {
                                // this method is empty
                            }

                            @Override
                            public void adNotDisplayed(Ad ad) {
                                RewardAdStartApp.setAd(null);
                                rewardAdStartApp.createAd();
                                rewardAdListener.onClick(false, pos);
                            }
                        });
                        rewardAdStartApp.getAd().setVideoListener(() -> isRewarded = true);
                    } else {
                        RewardAdStartApp.setAd(null);
                        rewardAdStartApp.createAd();
                        rewardAdListener.onClick(false, pos);
                    }
                    break;
                case Callback.AD_TYPE_APPLOVIN :
                    final RewardAdApplovin rewardAdApplovin = new RewardAdApplovin(ctx);
                    if (RewardAdApplovin.getAd() != null && RewardAdApplovin.getAd().isReady()) {
                        RewardAdApplovin.getAd().setListener(new MaxRewardedAdListener() {


                            @Override
                            public void onAdLoaded(@NonNull MaxAd maxAd) {
                                // this method is empty
                            }

                            @Override
                            public void onAdDisplayed(@NonNull MaxAd maxAd) {
                                // this method is empty
                            }

                            @Override
                            public void onAdHidden(@NonNull MaxAd maxAd) {
                                RewardAdApplovin.setAd(null);
                                rewardAdApplovin.createAd();
                                if (isRewarded) {
                                    rewardAdListener.onClick(true, pos);
                                }
                            }

                            @Override
                            public void onAdClicked(@NonNull MaxAd maxAd) {
                                // this method is empty
                            }

                            @Override
                            public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                                RewardAdApplovin.setAd(null);
                                rewardAdApplovin.createAd();
                                rewardAdListener.onClick(false, pos);
                            }

                            @Override
                            public void onAdDisplayFailed(@NonNull MaxAd maxAd,
                                                          @NonNull MaxError maxError) {
                                RewardAdApplovin.setAd(null);
                                rewardAdApplovin.createAd();
                                rewardAdListener.onClick(false, pos);
                            }

                            @Override
                            public void onUserRewarded(@NonNull MaxAd maxAd,
                                                       @NonNull MaxReward maxReward) {
                                isRewarded = true;
                            }

                        });
                        RewardAdApplovin.getAd().showAd();
                    } else {
                        RewardAdApplovin.setAd(null);
                        rewardAdApplovin.createAd();
                        rewardAdListener.onClick(false, pos);
                    }
                    break;
                case Callback.AD_TYPE_UNITY :
                    final RewardAdUnity rewardAdUnity = new RewardAdUnity();
                    if (RewardAdUnity.isAdLoaded) {
                        UnityAds.show((Activity) ctx, Callback.getUnityRewardAdID(), new IUnityAdsShowListener() {
                            @Override
                            public void onUnityAdsShowFailure(String placementId,
                                                              UnityAds.UnityAdsShowError error,
                                                              String message) {
                                RewardAdUnity.setAd(false);
                                rewardAdUnity.createAd();
                                rewardAdListener.onClick(false, pos);
                            }

                            @Override
                            public void onUnityAdsShowStart(String placementId) {
                                // this method is empty
                            }

                            @Override
                            public void onUnityAdsShowClick(String placementId) {
                                // this method is empty
                            }

                            @Override
                            public void onUnityAdsShowComplete(String placementId,
                                                               UnityAds.UnityAdsShowCompletionState state) {
                                RewardAdUnity.setAd(false);
                                rewardAdUnity.createAd();
                                rewardAdListener.onClick(true, pos);
                            }
                        });
                    } else {
                        RewardAdUnity.setAd(false);
                        rewardAdUnity.createAd();
                        rewardAdListener.onClick(false, pos);
                    }
                    break;
                default:
                    break;
            }
        } else {
            rewardAdListener.onClick(false, pos);
        }
    }

    public void openRewardVideoAdAlert(RewardAdListener rewardAdListener, int pos) {
        @SuppressLint("InflateParams") View view = ((Activity) ctx).getLayoutInflater().inflate(R.layout.row_bottom_videoad, null);

        BottomSheetDialog dialog = new BottomSheetDialog(ctx);
        dialog.setContentView(view);
        dialog.show();

        SmoothCheckBox cb = dialog.findViewById(R.id.cb_videoad);

        dialog.findViewById(R.id.btn_bottom_logout).setOnClickListener(view1 -> {
            dialog.dismiss();
            new SPHelper(ctx).setIsRewardAdWarned(cb.isChecked());
            loadRewardAds(rewardAdListener, pos);
        });
        dialog.findViewById(R.id.btn_bottom_cancel).setOnClickListener(view1 -> dialog.dismiss());
        dialog.findViewById(R.id.ll_checkbox).setOnClickListener(view1 -> cb.setChecked(true, true));
    }

    private boolean isInterAd() {
        if (isNetworkAvailable() && Boolean.TRUE.equals(Callback.getIsInterAd())
                && Boolean.TRUE.equals(Callback.getIsAdsStatus())
                && new GDPRChecker(ctx).canLoadAd()
                && Boolean.TRUE.equals(!new SPHelper(ctx).getIsSubscribed())) {
            Callback.setAdCount(Callback.getAdCount() + 1);
            return Callback.getAdCount() % Callback.getInterstitialAdShow() == 0;
        } else {
            return false;
        }
    }

    private boolean isBannerAd(String page) {
        if (isNetworkAvailable() && Boolean.TRUE.equals(Callback.getIsAdsStatus())
                && new GDPRChecker(ctx).canLoadAd()
                && Boolean.TRUE.equals(!new SPHelper(ctx).getIsSubscribed())) {
            switch (page) {
                case Callback.PAGE_HOME:
                    return Callback.getIsBannerAdHome();
                case Callback.PAGE_POST_DETAILS:
                    return Callback.getIsBannerAdPostDetails();
                case Callback.PAGE_CAT_DETAILS:
                    return Callback.getIsBannerAdCatDetails();
                case Callback.PAGE_SEARCH:
                    return Callback.getIsBannerAdSearch();
                default:
                    return true;
            }
        } else {
            return false;
        }
    }

    public boolean canLoadNativeAds(Context context, String page) {
        if (Boolean.TRUE.equals(Callback.getIsAdsStatus())
                && new GDPRChecker(context).canLoadAd()
                && Boolean.TRUE.equals(!new SPHelper(ctx).getIsSubscribed())) {
            if (page.equals(Callback.PAGE_NATIVE_POST)){
                return Callback.getIsNativeAdPost();
            } else if (page.equals(Callback.PAGE_NATIVE_CAT)){
                return Callback.getIsNativeAdCat();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
