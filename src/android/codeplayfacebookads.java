package cordova.plugin.codeplay.facebookads.free;



import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.facebook.ads.AudienceNetworkAds;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.facebook.ads.*;


/**
 * This class echoes a string called from JavaScript.
 */
public class codeplayfacebookads extends CordovaPlugin {


    private AdView adView;
    private InterstitialAd interstitialAd;


    private ViewGroup parentView;
    static boolean isFirstTime=true;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        JSONObject opts = args.optJSONObject(0);


        Context testParameter = (cordova.getActivity()).getBaseContext();
        if(isFirstTime)
            AudienceNetworkAds.initialize(testParameter);

        isFirstTime=false;



        if (action.equals("showBannerAds")) {

            String adBannerID = opts.optString("bannerid");
            String isTesting = opts.optString("isTesting");

            //Banner size set here getBannerAdSize(BANNER SIZE)
            adView = new AdView(testParameter, adBannerID, getBannerAdSize(""));

            if(Boolean.parseBoolean(isTesting)) {
                SharedPreferences adPrefs = cordova.getActivity().getSharedPreferences("FBAdPrefs", 0);
                String deviceIdHash = adPrefs.getString("deviceIdHash", (String) null);
                AdSettings.addTestDevice(deviceIdHash);
            }




            facebookBannerAdsShow(callbackContext);

            //String message = args.getString(0);
            //this.coolMethod(message, callbackContext);
            return true;
        }


        if (action.equals("hideBannerAds")) {

            cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (adView != null) {
                        ((ViewGroup)adView.getParent()).removeView(adView);
                        adView=null;
                    }
                    callbackContext.success("Banner Ad hide");

                    //PluginResult result = new PluginResult(PluginResult.Status.OK, "");
                    //callbackContext.sendPluginResult(result);
                }
            });

            return true;

        }


        if (action.equals("showInterstitialAds")) {

            String interstitialid = opts.optString("interstitialid");
            String isTesting = opts.optString("isTesting");
            interstitialAd = new InterstitialAd(testParameter, interstitialid);

            if(Boolean.parseBoolean(isTesting)) {
                SharedPreferences adPrefs = cordova.getActivity().getSharedPreferences("FBAdPrefs", 0);
                String deviceIdHash = adPrefs.getString("deviceIdHash", (String) null);
                AdSettings.addTestDevice(deviceIdHash);
            }


            facebookInterstitialAdsShow(callbackContext);

            //String message = args.getString(0);
            //this.coolMethod(message, callbackContext);
            return true;
        }

        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }




    private void facebookBannerAdsShow(CallbackContext callbackContext)
    {
        //Context testParameter = (cordova.getActivity()).getBaseContext();



        cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                View view = webView.getView();
                ViewGroup wvParentView = (ViewGroup) view.getParent();
                if (parentView == null) {
                    parentView = new LinearLayout(webView.getContext());
                }


                if (wvParentView != null && wvParentView != parentView) {
                    ViewGroup rootView = (ViewGroup)(view.getParent());
                    wvParentView.removeView(view);
                    ((LinearLayout) parentView).setOrientation(LinearLayout.VERTICAL);
                    parentView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                    view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F));
                    parentView.addView(view);
                    rootView.addView(parentView);
                }

                parentView.addView(adView);
                parentView.bringToFront();
                parentView.requestLayout();
                parentView.requestFocus();




            }
        });



        adView.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                //adError.getErrorMessage(),
                  //return adError.getErrorMessage();
                callbackContext.error(adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Ad loaded callback
                callbackContext.success("Banner Ad Loaded");
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                callbackContext.success("Banner Ad clicked");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                callbackContext.success("Ad impression logged");
            }
        });

        // Request an ad
        adView.loadAd();
    }


    private void facebookInterstitialAdsShow(CallbackContext callbackContext)
    {
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                callbackContext.success("Interstitial ad displayed.");
                //Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                callbackContext.success("Interstitial ad dismissed");
                //Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                callbackContext.error("Interstitial ad failed to load: " + adError.getErrorMessage());
                //Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                callbackContext.success("Interstitial ad is loaded and ready to be displayed!");
                //Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                callbackContext.success("Interstitial ad clicked!");
                //Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                callbackContext.success("Interstitial ad impression logged!");
                //Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();
    }








    protected AdSize getBannerAdSize(String str) {
        AdSize sz;
        if("BANNER".equals(str)) {
            sz = AdSize.BANNER_HEIGHT_50;
            // other size not supported by facebook audience network: FULL_BANNER, MEDIUM_RECTANGLE, LEADERBOARD, SKYSCRAPER
            //} else if ("SMART_BANNER".equals(str)) {
        } else {
            sz = isTablet() ? AdSize.BANNER_HEIGHT_90 : AdSize.BANNER_HEIGHT_50;
        }

        return sz;
    }

    public boolean isTablet() {
        Configuration conf = cordova.getActivity().getResources().getConfiguration();
        boolean xlarge = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((conf.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }


}
