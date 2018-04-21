package archtectsproductions.floaty_boxxy_release;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Frame {
    private int x;
    private int y;
    protected Bitmap image;
    public static int gravity = 10;
    private GameSurface gameSurface;

    private int xSpeed = 0;

    private int width;
    private int height;

    private int mcurrentFrame = 0;

    private Rect playerone;
    private Rect framer;

    public Frame(GameSurface gameSurface, Bitmap image, int x, int y) {
        this.gameSurface = gameSurface;
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public Rect getBounds() {
        return new Rect(this.x, this.y, this.x + width, this.y + height);
    }

    public int returnY() {
        return y;
    }

    public boolean checkCollision(Rect playerone, Rect framer) {
        this.playerone = playerone;
        this.framer = framer;
        return Rect.intersects(playerone, framer);
    }


    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, null);
    }
}
