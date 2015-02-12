package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import de.kolbenschurzgames.whack_a_toffel.app.R;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EView;
import org.androidannotations.annotations.res.StringRes;

@EView
class GameView extends SurfaceView {
	private final Paint timerPaint;

	@StringRes(value = R.string.time)
	String timeString;

	@Bean
	ToffelManager toffelManager;

	private GameLoopThread gameLoopThread;
	private boolean canDraw = false;
	private Rect screenSize;
	private Bitmap toffelHood;

	private float timerXPos;
	private float timerYPos;
	private String secondsUntilFinished;

	GameView(Context context) {
		super(context);
		this.setId(R.id.game_view_id);

		gameLoopThread = new GameLoopThread(this);
		toffelHood = BitmapFactory.decodeResource(getResources(), R.drawable.game_board_blank);

		this.timerPaint = new Paint();
		this.timerPaint.setColor(Color.WHITE);

		SurfaceHolder surfaceHolder = getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				toffelManager.plantToffels();
				startGameLoop();
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			}

			@Override
			public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
				boolean retry = true;
				gameLoopThread.setRunning(false);
				canDraw = false;
				while (retry) {
					try {
						gameLoopThread.join();
						retry = false;
						canDraw = true;
					} catch (InterruptedException e) {
						Log.w("surfaceHolder", "Game Loop Thread interrupted", e);
					}
				}
			}
		});
	}

	private void startGameLoop() {
		gameLoopThread.setRunning(true);
		gameLoopThread.start();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		setScreenSize(width, height);
		setTimerSizeAndPos(height);
		try {
			setToffelHoodSizes(width, height);
		} catch (Exception e) {
			Log.e("ToffelHood", "Error while setting toffel hood sizes", e);
		}
	}

	private void setScreenSize(int width, int height) {
		screenSize = new Rect(0, 0, width, height);
	}

	private void setTimerSizeAndPos(int height) {
		float timerSize = height / 20;
		this.timerPaint.setTextSize(timerSize);
		this.timerXPos = 30;
		this.timerYPos = timerSize + 30;
	}

	private void setToffelHoodSizes(int width, int height) throws Exception {
		toffelManager.setSizes(width, height);
		canDraw = true;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (canDraw) {
			canvas.drawBitmap(toffelHood, null, screenSize, null);
			canvas.drawText(timeString + secondsUntilFinished, timerXPos, timerYPos, timerPaint);
			toffelManager.updateToffels(canvas);
		}
	}

	void updateTimer(String secondsUntilFinished) {
		this.secondsUntilFinished = secondsUntilFinished;
	}

	String getCurrentTimerValue() {
		return this.secondsUntilFinished;
	}
}
