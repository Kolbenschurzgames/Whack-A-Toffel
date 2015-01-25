package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.WindowFeature;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity
public class GameActivity extends Activity {

    private static final int DURATION_IN_MILLISECONDS = 60000;
    private static final int TICK_INTERVAL = 1000;

    CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new GameView(this));
    }

    @AfterViews
    void initCountdownTimer() {
        countDownTimer = new CountDownTimer(DURATION_IN_MILLISECONDS, TICK_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Log.i("CountdownTimer", "Game round finished");
                HighscoreActivity_.intent(GameActivity.this).start();
            }
        };
        countDownTimer.start();
    }
}
