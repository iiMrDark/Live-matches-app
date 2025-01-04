package nemosofts.online.live.utils.advertising;

import android.annotation.SuppressLint;
import android.content.Context;

import com.wortise.ads.interstitial.InterstitialAd;

import nemosofts.online.live.callback.Callback;


public class AdManagerInterWortise {

    @SuppressLint("StaticFieldLeak")
    private static InterstitialAd interAd;
    private final Context ctx;

    public AdManagerInterWortise(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        AdManagerInterWortise.setAd(new InterstitialAd((ctx), Callback.getWortiseInterstitialAdID()));
        interAd.loadAd();
    }

    public InterstitialAd getAd() {
        return interAd;
    }

    public static void setAd(InterstitialAd interstitialAd) {
        interAd = interstitialAd;
    }
}