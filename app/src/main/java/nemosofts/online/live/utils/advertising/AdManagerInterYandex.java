package nemosofts.online.live.utils.advertising;

import android.content.Context;

import androidx.annotation.NonNull;

import com.yandex.mobile.ads.common.AdRequestConfiguration;
import com.yandex.mobile.ads.common.AdRequestError;
import com.yandex.mobile.ads.interstitial.InterstitialAd;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener;
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader;

import nemosofts.online.live.callback.Callback;

public class AdManagerInterYandex {

    private static InterstitialAd mInterstitialAd = null;
    private final Context ctx;

    public AdManagerInterYandex(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        AdRequestConfiguration adRequestConfiguration = new AdRequestConfiguration.Builder(Callback.getYandexInterstitialAdID()).build();

        InterstitialAdLoader mInterstitialAdLoader = new InterstitialAdLoader(ctx);
        mInterstitialAdLoader.setAdLoadListener(new InterstitialAdLoadListener() {
            @Override
            public void onAdLoaded(@NonNull final InterstitialAd interstitialAd) {
                AdManagerInterYandex.setAd(interstitialAd);
            }

            @Override
            public void onAdFailedToLoad(@NonNull final AdRequestError adRequestError) {
                // Ad failed to load
            }
        });
        mInterstitialAdLoader.loadAd(adRequestConfiguration);
    }

    public InterstitialAd getAd() {
        return mInterstitialAd ;
    }

    public static void setAd(InterstitialAd yandexInter) {
        mInterstitialAd = yandexInter;
    }
}