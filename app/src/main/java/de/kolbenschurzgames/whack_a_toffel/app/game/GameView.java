package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import de.kolbenschurzgames.whack_a_toffel.app.R;

public class GameView extends SurfaceView {
    private Bitmap toffelHood;
    private Bitmap toffel;
    private Bitmap hole;
    private Rect screenSize;
    private ToffelManager toffelManager;
    private GameLoopThread gameLoopThread;
    private boolean ready = false;

    public GameView(Context context) {
        super(context);

        gameLoopThread = new GameLoopThread(this);
        toffelHood = BitmapFactory.decodeResource(getResources(), R.drawable.game_board_blank);
        toffel = BitmapFactory.decodeResource(getResources(), R.drawable.hole_toffel);
        hole = BitmapFactory.decodeResource(getResources(), R.drawable.hole_empty);
        toffelManager = new ToffelManager(toffel, hole);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                toffelManager.plantToffels();
                gameLoopThread.setRunning(true);
                gameLoopThread.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                boolean retry = true;
                gameLoopThread.setRunning(false);
                while (retry) {
                    try {
                        gameLoopThread.join();
                        retry = false;
                    } catch (InterruptedException e) {
                        // TODO
                    }
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setScreenSize(width, height);
        try {
            setToffelHoodSizes(width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setScreenSize(int width, int height) {
        screenSize = new Rect(0, 0, width, height);
    }

    private void setToffelHoodSizes(int width, int height) throws Exception {
        toffelManager.setSizes(width, height);
        ready = true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (ready) {
            canvas.drawBitmap(toffelHood, null, screenSize, null);
            toffelManager.updateToffels(canvas);
        }
    }
}
