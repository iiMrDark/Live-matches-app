package nemosofts.online.live.utils.advertising;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAppOpenAd;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;

import java.util.Date;

import nemosofts.online.live.activity.LauncherActivity;
import nemosofts.online.live.activity.MainActivity;
import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.interfaces.OnShowAdCompleteListener;


public class AppOpenAdManager {

    private AppOpenAd appOpenAd = null;
    private com.wortise.ads.appopen.AppOpenAd mAppOpenAdWortise = null;
    private MaxAppOpenAd maxAppOpenAd;
    StartAppAd startAppAd;
    private boolean isLoadingAd = false;
    private long loadTime = 0;
    Context context;

    private Boolean isShowingAd = false;
    public Boolean getIsShowingAd() {
        return isShowingAd;
    }

    public AppOpenAdManager(Context context) {
        this.context = context;
    }

    public void loadAd(Context context) {
        // Do not load ad if there is an unused ad or one is already loading.
        if (Boolean.TRUE.equals(Callback.getIsAppOpenAdShown())) {
            return;
        }
        if (isLoadingAd || isAdAvailable()) {
            return;
        }

        isLoadingAd = true;

        switch (Callback.getAdNetwork()) {
            case Callback.AD_TYPE_ADMOB:
            case Callback.AD_TYPE_META:
                AdRequest request = new AdRequest.Builder().build();
                AppOpenAd.load(context, Callback.getAdmobOpenAdID(), request, new AppOpenAd.AppOpenAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AppOpenAd ad) {
                        appOpenAd = ad;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        isLoadingAd = false;
                    }
                });
                break;
            case Callback.AD_TYPE_WORTISE:
                mAppOpenAdWortise = new com.wortise.ads.appopen.AppOpenAd(context, Callback.getWortiseOpenAdID());
                mAppOpenAdWortise.setListener(new com.wortise.ads.appopen.AppOpenAd.Listener() {
                    @Override
                    public void onAppOpenClicked(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                        // this method is empty
                    }

                    @Override
                    public void onAppOpenDismissed(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                        // this method is empty
                    }

                    @Override
                    public void onAppOpenFailedToLoad(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd, @NonNull com.wortise.ads.AdError adError) {
                        isLoadingAd = false;
                    }

                    @Override
                    public void onAppOpenFailedToShow(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd, @NonNull com.wortise.ads.AdError adError) {
                        // this method is empty
                    }

                    @Override
                    public void onAppOpenImpression(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                        // this method is empty
                    }

                    @Override
                    public void onAppOpenLoaded(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                        mAppOpenAdWortise = appOpenAd;
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAppOpenShown(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                        // this method is empty
                    }
                });
                mAppOpenAdWortise.loadAd();
                break;
            case Callback.AD_TYPE_APPLOVIN:
                maxAppOpenAd = new MaxAppOpenAd(Callback.getApplovinOpenAdID(), context);
                maxAppOpenAd.setListener(new MaxAdListener() {
                    @Override
                    public void onAdLoaded(@NonNull MaxAd maxAd) {
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onAdDisplayed(@NonNull MaxAd maxAd) {
                        // this method is empty
                    }

                    @Override
                    public void onAdHidden(@NonNull MaxAd maxAd) {
                        // this method is empty
                    }

                    @Override
                    public void onAdClicked(@NonNull MaxAd maxAd) {
                        // this method is empty
                    }

                    @Override
                    public void onAdLoadFailed(@NonNull String s, @NonNull MaxError maxError) {
                        isLoadingAd = false;
                    }

                    @Override
                    public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                        // this method is empty
                    }
                });
                maxAppOpenAd.loadAd();
                break;
            case Callback.AD_TYPE_STARTAPP:
                startAppAd = new StartAppAd(context);
                startAppAd.loadAd(new AdEventListener() {
                    @Override
                    public void onReceiveAd(@NonNull Ad ad) {
                        isLoadingAd = false;
                        loadTime = (new Date()).getTime();
                    }

                    @Override
                    public void onFailedToReceiveAd(@Nullable Ad ad) {
                        isLoadingAd = false;
                    }
                });
                break;
            default:
                break;
        }
    }

    private boolean wasLoadTimeLessThanNHoursAgo() {
        long dateDifference = (new Date()).getTime() - loadTime;
        long numMilliSecondsPerHour = 3600000;
        return (dateDifference < (numMilliSecondsPerHour * 4));
    }

    public boolean isAdAvailable() {
        return switch (Callback.getAdNetwork()) {
            case Callback.AD_TYPE_ADMOB -> appOpenAd != null && wasLoadTimeLessThanNHoursAgo();
            case Callback.AD_TYPE_WORTISE -> mAppOpenAdWortise != null && mAppOpenAdWortise.isAvailable() && wasLoadTimeLessThanNHoursAgo();
            case Callback.AD_TYPE_APPLOVIN -> maxAppOpenAd != null && maxAppOpenAd.isReady();
            default -> false;
        };
    }

    public void showAdIfAvailable(@NonNull final Activity activity) {
        showAdIfAvailable(activity, () -> {
            // Empty because the user will go back to the activity that shows the ad.
        });
    }

    public void showAdIfAvailable(@NonNull final Activity activity,
                                  @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
        if (Boolean.TRUE.equals(isShowingAd)) {
            return;
        }

        if (!isAdAvailable()) {
            onShowAdCompleteListener.onShowAdComplete();
            checkAndReloadAd(activity);
            return;
        }

        switch (Callback.getAdNetwork()) {
            case Callback.AD_TYPE_ADMOB:
                handleAdMobAd(activity, onShowAdCompleteListener);
                break;
            case Callback.AD_TYPE_WORTISE:
                handleWortiseAd(activity, onShowAdCompleteListener);
                break;
            case Callback.AD_TYPE_APPLOVIN:
                handleAppLovinAd(activity, onShowAdCompleteListener);
                break;
            case Callback.AD_TYPE_STARTAPP:
                handleStartAppAd(activity, onShowAdCompleteListener);
                break;
            default:
                break;
        }
    }

    private void handleStartAppAd(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
        startAppAd.showAd(new AdDisplayListener() {
            @Override
            public void adHidden(Ad ad) {
                startAppAd = null;
                isShowingAd = false;

                if (activity instanceof LauncherActivity) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void adDisplayed(Ad ad) {
                Callback.setIsAppOpenAdShown(true);
                isShowingAd = true;
            }

            @Override
            public void adClicked(Ad ad) {
                // document why this method is empty
            }

            @Override
            public void adNotDisplayed(Ad ad) {
                startAppAd = null;
                isShowingAd = false;

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }
        });
        isShowingAd = true;
    }

    private void handleAppLovinAd(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
        maxAppOpenAd.setListener(new MaxAdListener() {
            @Override
            public void onAdHidden(@NonNull MaxAd maxAd) {
                maxAppOpenAd = null;
                isShowingAd = false;

                if (activity instanceof LauncherActivity) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void onAdClicked(@NonNull MaxAd maxAd) {
                // document why this method is empty
            }

            @Override
            public void onAdLoadFailed(@NonNull String string, @NonNull MaxError maxError) {
                // document why this method is empty
            }

            @Override
            public void onAdDisplayFailed(@NonNull MaxAd maxAd, @NonNull MaxError maxError) {
                maxAppOpenAd = null;
                isShowingAd = false;

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void onAdLoaded(@NonNull MaxAd maxAd) {
                // document why this method is empty
            }

            @Override
            public void onAdDisplayed(@NonNull MaxAd maxAd) {
                Callback.setIsAppOpenAdShown(true);
                isShowingAd = true;
            }
        });
        isShowingAd = true;
        maxAppOpenAd.showAd();
    }

    private void handleWortiseAd(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
        mAppOpenAdWortise.setListener(new com.wortise.ads.appopen.AppOpenAd.Listener() {
            @Override
            public void onAppOpenClicked(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                // document why this method is empty
            }

            @Override
            public void onAppOpenDismissed(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                mAppOpenAdWortise = null;
                isShowingAd = false;

                if (activity instanceof LauncherActivity) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void onAppOpenFailedToLoad(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd, @NonNull com.wortise.ads.AdError adError) {
                // document why this method is empty
            }

            @Override
            public void onAppOpenFailedToShow(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd, @NonNull com.wortise.ads.AdError adError) {
                mAppOpenAdWortise = null;
                isShowingAd = false;

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void onAppOpenImpression(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                // document why this method is empty
            }

            @Override
            public void onAppOpenLoaded(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                // document why this method is empty
            }

            @Override
            public void onAppOpenShown(@NonNull com.wortise.ads.appopen.AppOpenAd appOpenAd) {
                Callback.setIsAppOpenAdShown(true);
                isShowingAd = true;
            }
        });
        isShowingAd = true;
        mAppOpenAdWortise.showAd(activity);
    }

    private void handleAdMobAd(Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
        appOpenAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                // Set the reference to null so isAdAvailable() returns false.
                appOpenAd = null;
                isShowingAd = false;

                if (activity instanceof LauncherActivity) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                    activity.finish();
                }

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                appOpenAd = null;
                isShowingAd = false;

                onShowAdCompleteListener.onShowAdComplete();
                if (new GDPRChecker(context).canLoadAdOpenAds()) {
                    loadAd(activity);
                }
            }

            @Override
            public void onAdShowedFullScreenContent() {
                Callback.setIsAppOpenAdShown(true);
                isShowingAd = true;
            }
        });
        isShowingAd = true;
        appOpenAd.show(activity);
    }

    private void checkAndReloadAd(Activity activity) {
        if (new GDPRChecker(context).canLoadAdOpenAds()) {
            loadAd(activity);
        }
    }
}