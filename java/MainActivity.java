package archtectsproductions.floaty_boxxy_release;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.leaderboard.LeaderboardScore;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends FragmentActivity {

    public GoogleApiClient apiClient;
    private MainActivity main = this;
    public GameSurface gameSurface;
    RelativeLayout layout;
    RelativeLayout adlayout;
    private Saver saver;

    private static final String HIGHSCORE = "highscore";
    private String leaderscore = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Games.API)
                .addScope(Games.SCOPE_GAMES)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getBaseContext(), "Connection To Google Games Failed, No App Found Or No Internet Or Google Services Need Updating", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        MobileAds.initialize(this, getString(R.string.adappid));
        RelativeLayout.LayoutParams viewParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);viewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);viewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        final AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setLayoutParams(viewParams);
        adView.setAdUnitId("ca-app-pub-9910251603200622/6060837786");
        AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
        adRequestBuilder.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        adView.loadAd(adRequestBuilder.build());
        apiClient.connect();
        saver = Saver.getInstance(this);

        // fullscreen
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        playerscores();

        //ads
        gameSurface = new GameSurface(this, main);
        layout.addView(gameSurface);

        layout.addView(adView);

        setContentView(layout);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                System.out.println("ad banner finished loading!");
                adView.setVisibility(View.GONE);
                adView.setVisibility(View.VISIBLE);
            }
        });
    }
            // Set No Title
            //this.setContentView(new GameSurface(this));

    public void playerscores() {
        if (apiClient != null && apiClient.isConnected()) {
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(apiClient, "CgkI-cvoyfsfEAIQAQ", LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(
                    new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {

                        @Override
                        public void onResult(Leaderboards.LoadPlayerScoreResult arg0) {
                            LeaderboardScore c = arg0.getScore();
                            String score = c.getDisplayScore();
                            GameSurface.HighScore = Integer.parseInt(score);
                            saver.saveString(HIGHSCORE, score);
                        }
                    });
        }
    }

    public void gameover() {
        Games.Leaderboards.submitScore(apiClient, getString(R.string.leaderboard_highscores), GameSurface.HighScore);
    }

    public void showLeaderboard() {
        if (apiClient != null && apiClient.isConnected()) {
             startActivityForResult(Games.Leaderboards.getLeaderboardIntent(apiClient, getString(R.string.leaderboard_highscores)), 1);
        } else {
             apiClient.connect();
        }
    }
}
