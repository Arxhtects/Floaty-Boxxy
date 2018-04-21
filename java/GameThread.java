package archtectsproductions.floaty_boxxy_release;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

    private boolean running;
    private GameSurface gameSurface;
    private SurfaceHolder surfaceHolder;
    private MainActivity mainActivity;

    public GameThread(GameSurface gameSurface, SurfaceHolder surfaceHolder)  {
        this.gameSurface= gameSurface;
        this.surfaceHolder= surfaceHolder;
    }

    @Override
    public void run()  {
        double startTime = 60.0;
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        while(running)  {
            Canvas canvas= null;
            try {
                // Get Canvas from Holder and lock it.
                canvas = this.surfaceHolder.lockCanvas();
                // Synchronized
                synchronized (canvas)  {
                    this.gameSurface.update();
                    this.gameSurface.draw(canvas);
                }
            }catch(Exception e)  {
                // Do nothing.
            } finally {
                if(canvas!= null)  {
                    // Unlock Canvas.
                    this.surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            long now = System.nanoTime() ;
            // Interval to redraw game
            // (Change nanoseconds to milliseconds)
            delta+= (now - startTime) / ns;
            if(delta >= 1)  {
                delta--; // Millisecond.
            }
            frames++;

            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
                //updates = 0;
            }

            System.out.print(" Wait Time="+ delta);

            startTime = System.nanoTime();
            System.out.print(".");


        }
    }

    public void setRunning(boolean running)  {
        this.running= running;
    }


}