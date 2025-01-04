package nemosofts.online.live.executor;

import android.content.Context;

import androidx.nemosofts.Envato;

import org.json.JSONArray;
import org.json.JSONObject;

import nemosofts.online.live.callback.Callback;
import nemosofts.online.live.callback.Method;
import nemosofts.online.live.interfaces.AboutListener;
import nemosofts.online.live.item.ItemAbout;
import nemosofts.online.live.utils.ApplicationUtil;
import nemosofts.online.live.utils.AsyncTaskExecutor;
import nemosofts.online.live.utils.helper.Helper;
import nemosofts.online.live.utils.helper.SPHelper;

public class LoadAbout extends AsyncTaskExecutor<String, String, String> {

    private final Envato envato;
    private final Helper helper;
    private final SPHelper spHelper;
    private final AboutListener aboutListener;
    private String verifyStatus = "0";
    private String message = "";

    public LoadAbout(Context context, AboutListener aboutListener) {
        this.aboutListener = aboutListener;
        helper = new Helper(context);
        spHelper = new SPHelper(context);
        envato = new Envato(context);
    }

    @Override
    protected void onPreExecute() {
        aboutListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String strings) {
        try {
            String json = ApplicationUtil.responsePost(Callback.API_URL, helper.getAPIRequest(Method.METHOD_APP_DETAILS,
                    0, "", "", "", "", spHelper.getUserId(),
                    "", "", "", "","","","", null));
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray(Callback.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                if (!c.has(Callback.TAG_SUCCESS)) {

                    // App Details -----------------------------------------------------------------
                    String email = c.getString("app_email");
                    String author = c.getString("app_author");
                    String contact = c.getString("app_contact");
                    String website = c.getString("app_website");
                    String description = c.getString("app_description");
                    String developed = c.getString("app_developed_by");
                    String moreApps = "";
                    if (c.has("more_apps_url")){
                        moreApps = c.getString("more_apps_url");
                    }
                    Callback.setItemAbout(new ItemAbout(email, author, contact, website, description, developed, moreApps));

                    // Envato ----------------------------------------------------------------------
                    String apikey = c.getString("envato_api_key");
                    if (!apikey.isEmpty()){
                        envato.setEnvatoKEY(apikey);
                    }

                    // API Latest Limit ------------------------------------------------------------
                    Callback.setRecentLimit(Integer.parseInt(c.optString("api_latest_limit","10")));

                    // isSupported -----------------------------------------------------------------
                    Boolean isRtl = Boolean.parseBoolean(c.getString("isRTL"));
                    Boolean isMaintenance = Boolean.parseBoolean(c.getString("isMaintenance"));
                    Boolean isScreenshot = Boolean.parseBoolean(c.getString("isScreenshot"));
                    Boolean isApk = Boolean.parseBoolean(c.getString("isAPK"));
                    Boolean isVpn = Boolean.parseBoolean(c.getString("isVPN"));
                    Boolean isLogin = Boolean.parseBoolean(c.getString("isLogin"));
                    Boolean isGoogleLogin = Boolean.parseBoolean(c.getString("isGoogleLogin"));
                    spHelper.setIsSupported(isRtl, isMaintenance, isScreenshot, isApk, isVpn, isLogin, isGoogleLogin);

                    // AppUpdate -------------------------------------------------------------------
                    if (c.has("app_update_status")){
                        Boolean isAppUpdate = Boolean.parseBoolean(c.getString("app_update_status"));
                        Callback.setIsAppUpdate(isAppUpdate);
                        if(!c.getString("app_new_version").isEmpty()) {
                            int appNew = Integer.parseInt(c.getString("app_new_version"));
                            Callback.setAppNewVersion(appNew);
                        }
                        Callback.setAppUpdateDesc(c.getString("app_update_desc"));
                        Callback.setAppRedirectUrl(c.getString("app_redirect_url"));
                    }

                    // Ads Network -----------------------------------------------------------------
                    if (c.has("ad_status")){
                        Boolean isAds = Boolean.parseBoolean(c.getString("ad_status"));
                        Callback.setIsAdsStatus(isAds);
                        spHelper.setIsads(isAds);
                        if (Boolean.TRUE.equals(isAds)){
                            // PRIMARY ADS
                            String adNetwork = c.getString("ad_network");
                            Callback.setAdNetwork(adNetwork);
                            switch (adNetwork) {
                                case Callback.AD_TYPE_ADMOB:
                                    Callback.setAdmobPublisherID(c.getString("admob_publisher_id"));
                                    Callback.setAdmobBannerAdID(c.getString("admob_banner_unit_id"));
                                    Callback.setAdmobInterstitialAdID(c.getString("admob_interstitial_unit_id"));
                                    Callback.setAdmobNativeAdID(c.getString("admob_native_unit_id"));
                                    Callback.setAdmobOpenAdID(c.getString("admob_app_open_ad_unit_id"));
                                    Callback.setAdmobRewardAdID(c.getString("admob_reward_ad_unit_id"));
                                    break;
                                case Callback.AD_TYPE_STARTAPP:
                                    Callback.setStartappAppID(c.getString("startapp_app_id"));
                                    break;
                                case Callback.AD_TYPE_UNITY:
                                    Callback.setUnityGameID(c.getString("unity_game_id"));
                                    Callback.setUnityBannerAdID(c.getString("unity_banner_placement_id"));
                                    Callback.setUnityInterstitialAdID(c.getString("unity_interstitial_placement_id"));
                                    Callback.setUnityRewardAdID(c.getString("unity_reward_ad_unit_id"));
                                    break;
                                case Callback.AD_TYPE_APPLOVIN:
                                    Callback.setApplovinBannerAdID(c.getString("applovin_banner_ad_unit_id"));
                                    Callback.setApplovinInterstitialAdID(c.getString("applovin_interstitial_ad_unit_id"));
                                    Callback.setApplovinNativeAdID(c.getString("applovin_native_ad_manual_unit_id"));
                                    Callback.setApplovinOpenAdID(c.getString("applovin_app_open_ad_unit_id"));
                                    Callback.setApplovinRewardAdID(c.getString("applovin_reward_ad_unit_id"));
                                    break;
                                case Callback.AD_TYPE_IRONSOURCE:
                                    Callback.setIronsourceAppKey(c.getString("ironsource_app_key"));
                                    break;
                                case Callback.AD_TYPE_META:
                                    Callback.setAdmobBannerAdID(c.getString("mata_banner_ad_unit_id"));
                                    Callback.setAdmobInterstitialAdID(c.getString("mata_interstitial_ad_unit_id"));
                                    Callback.setAdmobNativeAdID(c.getString("mata_native_ad_manual_unit_id"));
                                    break;
                                case Callback.AD_TYPE_YANDEX:
                                    Callback.setYandexBannerAdID(c.getString("yandex_banner_ad_unit_id"));
                                    Callback.setYandexInterstitialAdID(c.getString("yandex_interstitial_ad_unit_id"));
                                    Callback.setYandexNativeAdID(c.getString("yandex_native_ad_manual_unit_id"));
                                    Callback.setYandexOpenAdID(c.getString("yandex_app_open_ad_unit_id"));
                                    break;
                                case Callback.AD_TYPE_WORTISE:
                                    Callback.setWortiseAppID(c.getString("wortise_app_id"));
                                    Callback.setWortiseBannerAdID(c.getString("wortise_banner_unit_id"));
                                    Callback.setWortiseInterstitialAdID(c.getString("wortise_interstitial_unit_id"));
                                    Callback.setWortiseNativeAdID(c.getString("wortise_native_unit_id"));
                                    Callback.setWortiseOpenAdID(c.getString("wortise_app_open_unit_id"));
                                    Callback.setWortiseRewardAdID(c.getString("wortise_reward_ad_unit_id"));
                                    break;
                                default:
                                    break;
                            }

                            // ADS PLACEMENT -------------------------------------------------------
                            Callback.setIsOpenAd(Boolean.parseBoolean(c.getString("app_open_ad_on_start")));
                            Callback.setIsBannerAdHome(Boolean.parseBoolean(c.getString("banner_home")));
                            Callback.setIsBannerAdPostDetails(Boolean.parseBoolean(c.getString("banner_post_details")));
                            Callback.setIsBannerAdCatDetails(Boolean.parseBoolean(c.getString("banner_category_details")));
                            Callback.setIsBannerAdSearch(Boolean.parseBoolean(c.getString("banner_search")));
                            Callback.setIsInterAd(Boolean.parseBoolean(c.getString("interstitial_post_list")));
                            Callback.setIsNativeAdPost(Boolean.parseBoolean(c.getString("native_ad_post_list")));
                            Callback.setIsNativeAdCat(Boolean.parseBoolean(c.getString("native_ad_category_list")));
                            Callback.setIsRewardAd(Boolean.parseBoolean(c.getString("reward_ad_on")));

                            // GLOBAL CONFIGURATION
                            Callback.setInterstitialAdShow(Integer.parseInt(c.optString("interstital_ad_click","5")));
                            Callback.setNativeAdShow(Integer.parseInt(c.optString("native_position","6")));
                            Callback.setRewardCredit(Integer.parseInt(c.optString("reward_credit","1")));
                        }
                    }

                    // PURCHASES -----------------------------------------------------------
                    if (c.has("isPurchases")){
                        Boolean isPurchases = Boolean.parseBoolean(c.getString("isPurchases"));
                        spHelper.setIsSubscribed(isPurchases);
                    }

                    // Player ----------------------------------------------------------------------
                    if(!c.getString("player_package_name").isEmpty()) {
                        String applicationID = c.getString("player_package_name");
                        spHelper.setHLSVideoPlayer(applicationID);
                    }

                } else {
                    verifyStatus = c.getString(Callback.TAG_SUCCESS);
                    message = c.getString(Callback.TAG_MSG);
                }
            }
            return "1";
        } catch (Exception ee) {
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        aboutListener.onEnd(s, verifyStatus, message);
    }
}