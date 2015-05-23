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
public class GameView extends SurfaceView {
    private static final int TEXT_OFFSET = 30;
    private static final String COLON_SPACE = ": ";

    private final Paint timerPaint;
    private final Paint scorePaint;

    @StringRes(value = R.string.time)
    String timeString;

    @StringRes(value = R.string.score)
    String scoreString;

    @Bean
    ToffelManager toffelManager;

    private float textSize;
    private float timerXPos;
    private float timerYPos;
    private float scoreXPos;
    private float scoreYPos;

    private GameLoopThread gameLoopThread;
    private Rect screenSize;
    private Bitmap toffelHood;

    private String secondsUntilFinished;
    private String score = "0";

    GameView(Context context) {
        super(context);
        this.setId(R.id.game_view_id);

        this.toffelHood = BitmapFactory.decodeResource(getResources(), R.drawable.game_board_blank);

        this.timerPaint = new Paint();
        this.timerPaint.setColor(Color.WHITE);

        this.scorePaint = new Paint(timerPaint);
        this.scorePaint.setTextAlign(Paint.Align.RIGHT);
    }

    public void startDrawing() {
        gameLoopThread = new GameLoopThread(this);
        gameLoopThread.setRunning(true);
        gameLoopThread.start();
    }

    public void stopDrawing() {
        boolean retry = true;
        gameLoopThread.setRunning(false);
        while (retry) {
            try {
                gameLoopThread.join();
                retry = false;
            } catch (InterruptedException e) {
                Log.i("game view", "Game Loop Thread interrupted", e);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setScreenSize(width, height);
        setTextSize(height);

        setTimerPos();
        setScorePos(width);

        setupToffelHood(width, height);
    }

    private void setTextSize(int height) {
        this.textSize = height / 20;
        this.timerPaint.setTextSize(textSize);
        this.scorePaint.setTextSize(textSize);
    }

    private void setScreenSize(int width, int height) {
        screenSize = new Rect(0, 0, width, height);
    }

    private void setTimerPos() {
        this.timerXPos = TEXT_OFFSET;
        this.timerYPos = textSize + TEXT_OFFSET;
    }

    private void setScorePos(int width) {
        this.scoreXPos = width - TEXT_OFFSET;
        this.scoreYPos = textSize + TEXT_OFFSET;
    }

    private void setupToffelHood(int width, int height) {
        try {
            toffelManager.initializeToffelHood(width, height);
        } catch (IllegalStateException e) {
            Log.e("ToffelHood", "Error while setting toffel hood sizes", e);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (gameLoopThread.isRunning() && canvas != null) {
            super.draw(canvas);
            canvas.drawBitmap(toffelHood, null, screenSize, null);
            canvas.drawText(timeString + COLON_SPACE + secondsUntilFinished, timerXPos, timerYPos, timerPaint);
            canvas.drawText(scoreString + COLON_SPACE + score, scoreXPos, scoreYPos, scorePaint);
            toffelManager.updateToffels(canvas);
        }
    }

    void updateTimer(String secondsUntilFinished) {
        this.secondsUntilFinished = secondsUntilFinished;
    }

    String getCurrentTimerValue() {
        return this.secondsUntilFinished;
    }

    void updateScore(int score) {
        this.score = Integer.toString(score);
    }

    String getCurrentScore() {
        return this.score;
    }
}
