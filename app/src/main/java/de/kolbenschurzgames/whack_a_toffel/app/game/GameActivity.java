package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.SubmitHighscoreActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Touch;
import org.androidannotations.annotations.WindowFeature;

import java.util.Date;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity
class GameActivity extends FragmentActivity {

    private static final long DEFAULT_DURATION_IN_MILLISECONDS = 15 * 1000;
    private static final short TICK_INTERVAL = 1 * 1000;

    CountDownTimer countDownTimer;

    GameView gameView;

    @Bean
    ToffelManager toffelManager;

    private ToffelTap lastTap;
    private int score;
    private long timerDurationLeft = DEFAULT_DURATION_IN_MILLISECONDS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new GameView_(this);
        setContentView(gameView);
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.stopDrawing();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        countDownTimer = createCountDownTimer(timerDurationLeft);
        countDownTimer.start();
        gameView.startDrawing();
    }

    private CountDownTimer createCountDownTimer(long duration) {
        return new CountDownTimer(duration, TICK_INTERVAL) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerDurationLeft = millisUntilFinished;
                gameView.updateTimer(Long.toString(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                Log.i("CountdownTimer", "Game round finished");
                gameView.stopDrawing();
                SubmitHighscoreActivity_.intent(GameActivity.this)
                        .extra("score", score)
                        .extra("endOfGame", new Date())
                        .start();
            }
        };
    }

    @Touch(R.id.game_view_id)
    void gameViewTouched(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            lastTap = toffelManager.getTapResult(motionEvent.getX(), motionEvent.getY());
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP && lastTap.isTapped()) {
            ToffelTap upTap = toffelManager.getTapResult(motionEvent.getX(), motionEvent.getY());
            if (upTap.equals(lastTap)) {
                onToffelTapped(upTap.getField());
            }
        }
    }

    private void onToffelTapped(ToffelField field) {
        ++score;
        toffelManager.toffelTapped(field);
        gameView.updateScore(score);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        countDownTimer.cancel();
        NavUtils.navigateUpFromSameTask(this);
    }
}
