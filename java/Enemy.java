package archtectsproductions.floaty_boxxy_release;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Enemy {
    private int x;
    private int y;
    public static int gravitynpc = 12;
    private GameSurface gameSurface;

    private int xSpeed = 0;

    private int width;
    private int height;
    protected Bitmap image;

    private int mcurrentFrame = 0;

    private Rect playerone;
    private Rect Enemy;

    public Enemy(GameSurface gameSurface, Bitmap image, int x, int y) {
        this.gameSurface = gameSurface;
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public void update() {
        x += xSpeed;
        y += gravitynpc; //falls at velocity like player
        //
    }

    public Rect getBounds() {
        return new Rect(this.x, this.y, this.x + width, this.y + height);
    }

    public int returnY() {
        return y;
    }

    public boolean checkCollision(Rect playerone, Rect Enemy) {
        this.playerone = playerone;
        this.Enemy = Enemy;
        return Rect.intersects(playerone, Enemy);
    }


    public void draw(Canvas canvas) {
        update();
        int srcX = mcurrentFrame*width;
        canvas.drawBitmap(image, x, y, null);
    }
}
