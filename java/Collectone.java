package archtectsproductions.floaty_boxxy_release;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Collectone {
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
    private Rect Collect1;

    public Collectone(GameSurface gameSurface, Bitmap image, int x, int y) {
        this.gameSurface = gameSurface;
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = image.getWidth()/1;
        this.height = image.getHeight()/1;
    }

    public void update() {
        x += xSpeed;
        y += gravitynpc; //falls at velocity like player

        //TODO if hit the bottom of the screen die.
        //
    }

    public Rect getBounds() {
        return new Rect(this.x, this.y, this.x + width, this.y + height);
    }

    public int returnY() {
        return y;
    }

    public boolean checkCollision(Rect playerone, Rect Collect1) {
        this.playerone = playerone;
        this.Collect1 = Collect1;
        return Rect.intersects(playerone, Collect1);
    }

    //TODO return collistion detection

    public void draw(Canvas canvas) {
        update();
        int srcX = mcurrentFrame*width;
        canvas.drawBitmap(image, x, y, null);
    }
}
