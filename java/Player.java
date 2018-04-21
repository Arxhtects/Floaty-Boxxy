package archtectsproductions.floaty_boxxy_release;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Player {
    public static int gravity = 1;
    public static int vSpeed = 2;
    static int playerHeight;
    static int playerWidth;
    public static int jumpPower = 22;

    private int width;
    private int height;
    protected Bitmap image;

    private int x;
    private int y;

    private int mColumnWidth = 2;
    private int animationcolumn = 0;
    private int mColumnHeight = 1;
    private int mcurrentFrame = 0;
    private int animationposy = 0;
    private int animationstate = 0;

    // Velocity of game character (pixel/millisecond)
    public static float VELOCITY = 0;

    public int movingVectorX = 0;
    public int movingVectorY = 0;

    private long lastDrawNanoTime =-1;

    private GameSurface gameSurface;

    public Player(GameSurface gameSurface, Bitmap image, int x, int y) {
        this.gameSurface= gameSurface;
        this.x = x;
        this.y = y;
        this.image = image;
        this.width = image.getWidth()/mColumnWidth;
        this.height = image.getHeight()/mColumnHeight;

        playerHeight=image.getHeight();
    }


    public void checkanimationstate(){
        if (vSpeed < 0){
            mcurrentFrame = 0;
            animationcolumn = 0;
            animationposy=1;
        }
        else if(vSpeed > 0){
            mcurrentFrame = 1;
            animationposy=1;
            animationcolumn = 0;
        }
        else{
            animationstate = 0;
        }
    }

    public void switchanimations(){
        if(animationstate ==0){
            animationcolumn = 2;
            animationposy = 0;
        }
        else if(animationstate ==1){
            mcurrentFrame = 0;
            animationcolumn = 0;
            animationposy=1;
        }
        else if(animationstate == 2){
            mcurrentFrame = 1;
            animationposy=1;
            animationcolumn = 0;
        }
    }


    public void  CheckHitground() {
        if (y < gameSurface.getHeight()-playerHeight) {
            vSpeed += gravity;
        } else if (vSpeed > 0) {
            vSpeed = 0;
            VELOCITY = 0;
        }
        y += vSpeed;
    }

    public void update()  {
        checkanimationstate();
        switchanimations();
        // Current time in nanoseconds
        long now = System.nanoTime();

        // Never once did draw.
        if(lastDrawNanoTime==-1) {
            lastDrawNanoTime= now;
        }
        // Change nanoseconds to milliseconds (1 nanosecond = 1000000 milliseconds).
        int deltaTime = (int) ((now - lastDrawNanoTime)/ 1000000 );

        // Distance moves
        float distance = VELOCITY * deltaTime;

        double movingVectorLength = Math.sqrt(movingVectorX* movingVectorX + movingVectorY*movingVectorY);

        // Calculate the new position of the game character.
        this.x = x +  (int)(distance* movingVectorX / movingVectorLength);
        this.y = y +  (int)(distance* movingVectorY / movingVectorLength);


        CheckHitground();
    }

    public void draw(Canvas canvas) {
        int srcX = mcurrentFrame*width;
        int srcY = animationposy*60; // 1.5 of your sprite height
        Rect src = new Rect(srcX,srcY,srcX + width,srcY+height);
        Rect dst = new Rect(x,y,x+(width),y+(height));
        canvas.drawBitmap(image, src, dst, null);
        this.lastDrawNanoTime= System.nanoTime(); //final redraw
    }

    public Rect getBounds() {
        return new Rect(this.x, this.y, this.x + width, this.y + height);
    }

    public int returnY() {
        return y;
    }

    public int getX()  {
        return this.x;
    }

    public int getY()  {
        return this.y;
    }

    public void setMovingVector(int movingVectorX, int movingVectorY)  {
        this.movingVectorX= movingVectorX;
        this.movingVectorY = movingVectorY;
    }

    public void setactionUp () {
        VELOCITY = 0;
    }
    public void setActionDown() {
        vSpeed = -jumpPower;
        VELOCITY = 4.0f;
    }

}