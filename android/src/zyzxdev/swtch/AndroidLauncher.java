package zyzxdev.swtch;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import zyzxdev.swtch.nativeInterface.AdManager;

/**
 * Created by aaron on 7/6/2017.
 */

public class AndroidLauncher extends AndroidApplication {

    private static String APP_ID = "ca-app-pub-9302553825003253~3377317324";
    private static String INTERSTITIAL_ID = "ca-app-pub-9302553825003253/6330783725";
    private static String BANNER_ID = "ca-app-pub-9302553825003253/4854050526";

    private AdView adView;
    private View gameView;

    public InterstitialAd interstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MobileAds.initialize(this,  APP_ID);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(INTERSTITIAL_ID);
        interstitialAd.loadAd(buildAdRequest());
        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdClosed(){
                interstitialAd.loadAd(buildAdRequest());
            }
        });

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.useImmersiveMode = true;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(params);

        AdView admobView = createAdView();
        View gameView = createGameView(cfg);
        layout.addView(gameView);
        layout.addView(admobView);

        setContentView(layout);
        startAdvertising(admobView);
    }

    @SuppressLint("ResourceType")
    private AdView createAdView() {
        adView = new AdView(this);
        adView.setAdSize(AdSize.SMART_BANNER);
        adView.setAdUnitId(BANNER_ID);
        adView.setId(1234);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        adView.setLayoutParams(params);
        adView.setBackgroundColor(Color.parseColor("#222222"));
        return adView;
    }

    private View createGameView(AndroidApplicationConfiguration cfg) {
        gameView = initializeForView(new SWITCH(new AndroidAdManager()), cfg);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, 0, 1);
        gameView.setLayoutParams(params);
        return gameView;
    }

    private void startAdvertising(AdView adView) {
        adView.loadAd(buildAdRequest());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
    }

    @Override
    public void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (adView != null) adView.destroy();
        super.onDestroy();
    }


    private AdRequest buildAdRequest(){
        return new AdRequest.Builder()
                .addTestDevice("881EFDDE91EA2CB393A656D948A86ED6")
                .addKeyword("game")
                .addKeyword("puzzle")
                .build();
    }

    class AndroidAdManager implements AdManager {
        public void showInterstitial() {
            Handler mainHandler = new Handler(getMainLooper());

            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if(interstitialAd.isLoaded())
                        interstitialAd.show();
                }
            };

            mainHandler.post(runnable);
        }
    }
}
