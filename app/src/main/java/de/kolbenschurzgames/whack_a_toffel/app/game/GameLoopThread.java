package de.kolbenschurzgames.whack_a_toffel.app.game;

import android.graphics.Canvas;
import android.util.Log;

class GameLoopThread extends Thread {
    private static final long FPS = 10;

    private final GameView view;

    private boolean isRunning = false;

    GameLoopThread(GameView view) {
        this.view = view;
    }

    boolean isRunning() {
        return this.isRunning;
    }

    void setRunning(boolean run) {
        this.isRunning = run;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long sleepTime;
        while (isRunning) {
            if (view != null && view.getHolder().getSurface().isValid()) {
                Canvas c = null;
                long startTime = System.currentTimeMillis();
                try {
                    c = view.getHolder().lockCanvas();
                    synchronized (view.getHolder()) {
                        view.draw(c);
                    }
                } finally {
                    if (c != null) {
                        view.getHolder().unlockCanvasAndPost(c);
                    }
                }
                sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
                try {
                    if (sleepTime > 0) {
                        sleep(sleepTime);
                    } else {
                        sleep(10);
                    }
                } catch (Exception e) {
                    Log.e("GameLoop", "Exception in GameLoop", e);
                }
            }
        }
    }
}
