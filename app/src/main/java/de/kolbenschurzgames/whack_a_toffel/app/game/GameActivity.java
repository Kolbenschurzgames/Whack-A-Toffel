package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import de.kolbenschurzgames.whack_a_toffel.app.highscores.HighscoreActivity_;
import de.kolbenschurzgames.whack_a_toffel.app.model.ToffelField;
import org.androidannotations.annotations.*;

@WindowFeature(Window.FEATURE_NO_TITLE)
@EActivity
public class GameActivity extends Activity {

	private static final int DURATION_IN_MILLISECONDS = 30000;
	private static final int TICK_INTERVAL = 1000;

	CountDownTimer countDownTimer;

	GameView gameView;

	@Bean
	ToffelManager toffelManager;

	private ToffelTap lastTap;
	private int score;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameView = new GameView_(this);
		setContentView(gameView);
	}

	@AfterViews
	void initCountdownTimer() {
		countDownTimer = new CountDownTimer(DURATION_IN_MILLISECONDS, TICK_INTERVAL) {

			@Override
			public void onTick(long millisUntilFinished) {
				gameView.updateTimer(Long.toString(millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				Log.i("CountdownTimer", "Game round finished");
				HighscoreActivity_.intent(GameActivity.this).start();
			}
		};

		countDownTimer.start();
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
}
