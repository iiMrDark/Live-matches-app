package nemosofts.online.live.utils.advertising;

import android.annotation.SuppressLint;
import android.content.Context;

import com.wortise.ads.rewarded.RewardedAd;

import nemosofts.online.live.callback.Callback;

public class RewardAdWortise {

    @SuppressLint("StaticFieldLeak")
    private static RewardedAd mRewardedAd;
    private final Context ctx;

    public RewardAdWortise(Context ctx) {
        this.ctx = ctx;
    }

    public void createAd() {
        RewardAdWortise.setAd(new RewardedAd((ctx), Callback.getWortiseRewardAdID()));
        mRewardedAd.loadAd();
    }

    public RewardedAd getAd() {
        return mRewardedAd;
    }

    public static void setAd(RewardedAd mRewardAd) {
        mRewardedAd = mRewardAd;
    }
}