package archtectsproductions.floaty_boxxy_release;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameSurface extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private MainActivity mainactivity;

    private GameThread gameThread;
    private Player player;
    private Collectone collectone;
    private Frame frame;
    private Enemy enemy;
    private Saver saver;
    public static int Score = 0; //score static
    public static int HighScore; //highscore static
    public int gameRan = 0;
    private int settingscanvas = 0;
    private int bitmapsetting;

    private Bitmap playerbitmap;
    private Bitmap collectonebitmap;
    private Bitmap collectonebitmap2;
    private Bitmap collectonebitmap3;
    private Bitmap enemybitmap;
    private Bitmap groundspikes;
    private Bitmap settings;
    private Bitmap closebutton;

    private static final String HIGHSCORE = "highscore";
    private static final String BITMAPSELECT = "bitmap";

    private List<Collectone> block = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<Frame> framey = new ArrayList<>();

    private int timerDrop = 40;
    //get Display size for centering text.

    int mWidthSize = this.getResources().getDisplayMetrics().widthPixels /2;
    int mHeightSize = this.getResources().getDisplayMetrics().heightPixels /2;
    int lengthofscreen = this.getResources().getDisplayMetrics().widthPixels;
    int bottomofscreen = this.getResources().getDisplayMetrics().heightPixels;
    //setup

    private Paint textpaint = new Paint();
    private Paint topheader = new Paint();
    private Paint smallpaint = new Paint();

    private int tutorial;
    private int randomtipint;

    //players velocity
    public static float velocity;

    public GameSurface(Context context, MainActivity mainActivity)  {
        super(context);
        this.mContext = context;
        this.mainactivity = mainActivity;
        DisplayMetrics dm = new DisplayMetrics();
        double incx = Math.pow(dm.widthPixels/dm.xdpi,2);
        double incy = Math.pow(dm.heightPixels/dm.ydpi,2);
        double inches = Math.sqrt(incx+incy);

        //increase velocity for smaller phone screens

        if(inches < 5.1) {
            velocity = 6.0f;
        } else {
            velocity = 3.5f;
        }

        // Make Game Surface focusable so it can handle events.
        this.setFocusable(true);
        // Sét callback.
        this.getHolder().addCallback(this);
        saver = Saver.getInstance(context);
        String savedhighscore = saver.getString(HIGHSCORE);
        String savedsettingbitmaps = saver.getString(BITMAPSELECT);
        if (savedhighscore == null) {
            HighScore = 0;
        } else {
            int SAVEDHISCORE = Integer.parseInt(savedhighscore);
            HighScore = SAVEDHISCORE;
        }

        if (savedsettingbitmaps == null) {
            bitmapsetting = 2;
            enemybitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.minenemy);
            settings = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
            closebutton = BitmapFactory.decodeResource(this.getResources(), R.drawable.closebutton);
            groundspikes = BitmapFactory.decodeResource(this.getResources(), R.drawable.spikes);
            collectonebitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
            collectonebitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
            collectonebitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
            playerbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.minplayer);
        } else if (savedsettingbitmaps.equals("originalbitmapstyle")) {
            bitmapsetting = 1;
            settings = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
            closebutton = BitmapFactory.decodeResource(this.getResources(), R.drawable.closebutton);
            groundspikes = BitmapFactory.decodeResource(this.getResources(), R.drawable.spikes);
            enemybitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemies);
            collectonebitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block3);
            collectonebitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block2);
            collectonebitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.block);
            playerbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.player);
        } else if (savedsettingbitmaps.equals("redesignbitmapstyle")) {
            bitmapsetting = 2;
            enemybitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.minenemy);
            settings = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
            closebutton = BitmapFactory.decodeResource(this.getResources(), R.drawable.closebutton);
            groundspikes = BitmapFactory.decodeResource(this.getResources(), R.drawable.spikes);
            collectonebitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
            collectonebitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
            collectonebitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
            playerbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.minplayer);
        }

        framey.add(new Frame(this, groundspikes, 0, 0- groundspikes.getHeight())); //top one;
        framey.add(new Frame(this, groundspikes, 0, bottomofscreen));
    }

    public void update() {
        this.player.update();

        if (Score > HighScore) {
            HighScore = Score;
        }

        if (player.getX() == bottomofscreen) {
            gameRan = 2;
        }
        spawnblocks();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (gameRan == 0) {
                if (y < this.getResources().getDisplayMetrics().heightPixels / 16) {
                    settingscanvas = 1;
                    gameRan = 3;
                } else if (y < this.getResources().getDisplayMetrics().heightPixels / 3) {
                    gameRan = 1;
                } else if (y < this.getResources().getDisplayMetrics().heightPixels / 3 + bottomofscreen / 3) {
                    tutorial = 1;
                    gameRan = 3;
                } else {
                    mainactivity.showLeaderboard();
                   }
            } else if (settingscanvas == 1) {
                if (y < this.getResources().getDisplayMetrics().heightPixels / 16) {
                        settingscanvas = 0;
                        gameRan = 0;
                } else {
                    if (bitmapsetting == 1) {
                        bitmapsetting = 2;
                        saver.saveString(BITMAPSELECT, "redesignbitmapstyle");
                        enemybitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.minenemy);
                        settings = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
                        groundspikes = BitmapFactory.decodeResource(this.getResources(), R.drawable.spikes);
                        collectonebitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
                        collectonebitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
                        collectonebitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.mincollect);
                        playerbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.minplayer);
                        this.player = new Player(this, playerbitmap, mWidthSize-playerbitmap.getHeight()/2-playerbitmap.getHeight()/5, bottomofscreen/5+playerbitmap.getHeight()); //player

                    } else if (bitmapsetting == 2) {
                        bitmapsetting = 1;
                        saver.saveString(BITMAPSELECT, "originalbitmapstyle");
                        settings = BitmapFactory.decodeResource(this.getResources(), R.drawable.settings);
                        groundspikes = BitmapFactory.decodeResource(this.getResources(), R.drawable.spikes);
                        enemybitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.enemies);
                        collectonebitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block3);
                        collectonebitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.block2);
                        collectonebitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.block);
                        playerbitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.player);
                        this.player = new Player(this, playerbitmap, mWidthSize-playerbitmap.getHeight()/2-playerbitmap.getHeight()/5, bottomofscreen/5+playerbitmap.getHeight()); //player
                    }
                }
            } else if (tutorial == 1) {
                tutorial = 2;
                gameRan = 3;
            } else if (tutorial == 2) {
                tutorial = 3;
                gameRan = 3;
            } else if (tutorial == 3) {
                tutorial = 4;
                gameRan = 3;
            } else if (tutorial == 4) {
                tutorial = 5;
                gameRan = 3;
            } else if (tutorial == 5) {
                tutorial = 0;
                gameRan = 0;
            } else if (gameRan == 1) {
                Random randomland = new Random();
                int random = randomland.nextInt(7);
                randomtipint = random;
                gameRan = 4;
                player.jumpPower = 22;
                player.gravity = 1;
                player.vSpeed = 2;
                collectone.gravitynpc = 12;
                enemy.gravitynpc = 12;
               } else if (gameRan == 2) {
                if (x < this.getResources().getDisplayMetrics().widthPixels/2 && y > this.getResources().getDisplayMetrics().heightPixels/2) { //retry
                    gameRan = 1;
                    Score = 0;
                    enemies.clear();
                    block.clear();
                    timerDrop = 40;
                    this.player = new Player(this, playerbitmap, mWidthSize-playerbitmap.getHeight()/2-playerbitmap.getHeight()/5, bottomofscreen/5+playerbitmap.getHeight()); //player
                } else if (y > this.getResources().getDisplayMetrics().heightPixels/2 && x > this.getResources().getDisplayMetrics().widthPixels/2) { //exit
                    gameRan = 0;
                    Score = 0;
                    enemies.clear();
                    block.clear();
                    this.player = new Player(this, playerbitmap, mWidthSize-playerbitmap.getHeight()/2-playerbitmap.getHeight()/5, bottomofscreen/5+playerbitmap.getHeight()); //player
                    timerDrop = 40;
                } else {
                    //do nothing.
                }
            } else {
                player.setActionDown();
                int movingVectorX = x - this.player.getX();
                int movingVectorY = y - this.player.getY();
                this.player.setMovingVector(movingVectorX, movingVectorY);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        player.setactionUp();
                    }
                }, 100);
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            player.setactionUp();
            }
        return false;
    }


    public void spawnblocks() {
        timerDrop ++;
        if (gameRan == 0){
            //do nothing
        } else if (gameRan == 1) {
            //do nothing
        } else if(gameRan == 2) {
            //do nothing
        } else if (gameRan == 3) {
            //do nothing
        } else {
            if (timerDrop >= 80) {
                Random randomBlock = new Random();
                int random = randomBlock.nextInt(4);

                int min = 300;
                int max = this.getResources().getDisplayMetrics().widthPixels - 300;
                int topofscreen = 0 - 200; //set above the screen height
                Random randomXpos = new Random();
                int randomposX = randomXpos.nextInt((max - min) + 1) + min;
                int randomposXtwo = randomXpos.nextInt((max - min) + 1) + min;
                int randomposXthree = randomXpos.nextInt((max - min) + 1) + min;
                int currentBlock = 1;

                switch (random) {
                    case 1:
                            enemies.add(new Enemy(this, enemybitmap, randomposX, topofscreen));
                            block.add(new Collectone(this, collectonebitmap, randomposXtwo, topofscreen-400));
                            block.add(new Collectone(this, collectonebitmap3, randomposXthree, topofscreen-800));
                        break;
                    case 2:
                            block.add(new Collectone(this, collectonebitmap2, randomposX, topofscreen-400));
                            block.add(new Collectone(this, collectonebitmap, randomposXtwo, topofscreen-800));
                            enemies.add(new Enemy(this, enemybitmap, randomposXtwo, topofscreen));
                        break;
                    case 3:
                            block.add(new Collectone(this, collectonebitmap3, randomposX, topofscreen));
                            block.add(new Collectone(this, collectonebitmap2, randomposXtwo, topofscreen-400));
                            enemies.add(new Enemy(this, enemybitmap, randomposXtwo, topofscreen-800));
                        break;
                }
                timerDrop = 0;
            }
        }
    }

    @Override
    public void draw(Canvas canvas)  {
        super.draw(canvas);

        canvas.drawColor(Color.parseColor("#eeeeee"));

        for (int i = 0; i < framey.size(); i++) {
            framey.get(i).draw(canvas);
            Rect playerone = player.getBounds();
            Rect framer = framey.get(i).getBounds();
            if (framey.get(i).checkCollision(playerone, framer)){
                gameRan = 2;
            }
        }

        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "font/pixel.ttf");
        int scaledSize = getResources().getDimensionPixelSize(R.dimen.Scalable_fontsize); //scales text to dimenstion
        int smallerSize = getResources().getDimensionPixelSize(R.dimen.Scalable_smallsize); //scales text to diemntions for smaller sizes

        textpaint.setTypeface(font);
        textpaint.setTextSize(scaledSize);
        textpaint.setColor(Color.parseColor("#EEEEEE"));
        topheader.setColor(Color.parseColor("#2d2d2d"));
        textpaint.setTextAlign(Paint.Align.CENTER);

        smallpaint.setTypeface(font);
        smallpaint.setTextSize(smallerSize);
        smallpaint.setColor(Color.parseColor("#EEEEEE"));
        smallpaint.setTextAlign(Paint.Align.CENTER);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textpaint.descent() + textpaint.ascent()) / 2)) ;

        canvas.drawRect(lengthofscreen/60, 0, lengthofscreen-lengthofscreen/60, bottomofscreen/14, topheader);

        String score = String.valueOf(Score);
        String highscore = String.valueOf(HighScore);
        float scorehalf = score.length();
        canvas.drawText(score, lengthofscreen/10+scorehalf, bottomofscreen/17, textpaint); //drawing score
        canvas.drawText(highscore, lengthofscreen-lengthofscreen/10+scorehalf, bottomofscreen/17, textpaint); //drawing score TODO add highscores

        this.player.draw(canvas);

        for(int i = 0; i < block.size(); i++) {
            block.get(i).draw(canvas);
            Rect playerone = player.getBounds();
            Rect Collect1 = block.get(i).getBounds();
            if (block.get(i).returnY() > bottomofscreen){ //TODO change to bottom of screen
                block.remove(i);
            }
            if (block.get(i).checkCollision(playerone, Collect1)){
                block.remove(i);
                Score += 1;
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(canvas);
            Rect playerone = player.getBounds();
            Rect Enemys = enemies.get(i).getBounds();
            if (enemies.get(i).returnY() > bottomofscreen){ //TODO change to bottom of screen
                enemies.remove(i);
            }
            if (enemies.get(i).checkCollision(playerone, Enemys)){
                gameRan = 2;
            }
        }

        if (gameRan == 0) {
            Paint yellow = new Paint();
            Paint green = new Paint();
            Paint red = new Paint();
            yellow.setColor(Color.parseColor("#EFC473"));
            green.setColor(Color.parseColor("#59E1C0"));
            red.setColor(Color.parseColor("#FF574F"));
            canvas.drawRect(0,0,lengthofscreen,bottomofscreen,yellow); //score button
            canvas.drawRect(0,0,lengthofscreen,bottomofscreen/3+bottomofscreen/3, red);//how to button
            canvas.drawRect(0,0,lengthofscreen,bottomofscreen/3, green); //play button
            canvas.drawBitmap(settings, lengthofscreen-settings.getWidth()-20, 20, null);
            String playtxt = "PLAY!";
            String howtotxt = "HOW TO?";
            String scoretxt = "HIGHSCORES";
            canvas.drawText(playtxt, xPos-playtxt.length()/2, bottomofscreen/6, textpaint);
            canvas.drawText(howtotxt, xPos-howtotxt.length()/2, bottomofscreen/2, textpaint);
            canvas.drawText(scoretxt, xPos-score.length()/2, bottomofscreen-bottomofscreen/7, textpaint);
        } else if (gameRan == 1) {
                String tap = "READY?";
                float halftext = tap.length();
                canvas.drawColor(Color.parseColor("#80000000"));
                canvas.drawText(tap, xPos - halftext, yPos, textpaint);
        } else if (gameRan == 2) {
                String SAVEDHIGHSCORE = Integer.toString(HighScore);
                saver.saveString(HIGHSCORE, SAVEDHIGHSCORE); //save the string
                String gameover = "GAME OVER";
                float halftext = gameover.length();
                canvas.drawColor(Color.parseColor("#80000000"));
                canvas.drawText(gameover, xPos-halftext/2, yPos-scaledSize*5,textpaint);

                String scoretxt = "SCORE: "+Score;
                float halftext2 = scoretxt.length();
                canvas.drawText(scoretxt, xPos-halftext2/2, yPos-scaledSize*3, textpaint);

                String exittxt = "EXIT";
                String retrytxt = "RETRY";
                canvas.drawText(retrytxt, xPos-xPos/2-retrytxt.length()/2, yPos+scaledSize*6, textpaint);
                canvas.drawText(exittxt, xPos+xPos/2+retrytxt.length()/2, yPos+scaledSize*6, textpaint);

                int random = randomtipint;
                if (random == 0) {
                    String tiptxt1 = "USE GAPS IN DROPS WISELY";
                    canvas.drawText(tiptxt1, xPos - exittxt.length() / 2, bottomofscreen - bottomofscreen / 9, smallpaint);
                } else if (random == 1) {
                    String tiptxt2 = "HOLDING YOUR TAP MOVES OVER FURTHER";
                    canvas.drawText(tiptxt2, xPos - exittxt.length() / 2, bottomofscreen - bottomofscreen / 9, smallpaint);
                } else if (random == 2) {
                    String tiptxt3 = "YOU DON'T HAVE TO GO FOR EVERYTHING";
                    canvas.drawText(tiptxt3, xPos - exittxt.length() / 2, bottomofscreen - bottomofscreen / 9, smallpaint);
                } else if (random == 3) {
                    String tiptxt4 = "EDGES MAY BE BEST PLACE TO TAP";
                    canvas.drawText(tiptxt4, xPos - exittxt.length() / 2, bottomofscreen - bottomofscreen / 9, smallpaint);
                } else if (random == 4) {
                    String tiptxt5 = "SETTINGS CAN CHANGE THE THEME";
                    canvas.drawText(tiptxt5, xPos - exittxt.length() / 2, bottomofscreen - bottomofscreen / 9, smallpaint);
                } else if (random == 5) {
                    String tiptxt6 = "FALLING THEN TAPPING IS THE KEY";
                    canvas.drawText(tiptxt6, xPos - exittxt.length() /2, bottomofscreen-bottomofscreen / 9, smallpaint);
                } else if (random == 6) {
                    String tiptxt7 = "YOU FALL FASTER THAN OTHER STUFF";
                    canvas.drawText(tiptxt7, xPos - exittxt.length() /2, bottomofscreen-bottomofscreen / 9, smallpaint);
                }

                player.jumpPower = 0;
                player.gravity = 0;
                player.vSpeed = 0;
                collectone.gravitynpc = 0;
                enemy.gravitynpc = 0;

                if (Score == HighScore) {
                    mainactivity.gameover();
                } else {
                    //do nothing
                }
        }

        String tap = "> TAP";
        if (tutorial == 1) {
            String tuttxt1 = "TAP BELLOW";
            String tuttxtp1 = "BOXXY TO FLOAT";
            canvas.drawColor(Color.parseColor("#FF574F"));
            canvas.drawText(tuttxt1, xPos-tuttxt1.length()/2, yPos-scaledSize, textpaint);
            canvas.drawText(tuttxtp1, xPos-tuttxtp1.length()/2, yPos+scaledSize, textpaint);
            canvas.drawText(tap, xPos-tap.length()/2, yPos+scaledSize*3, textpaint);
        } else if (tutorial == 2) {
            String tuttxt1 = "TAP LEFT OR RIGHT";
            String tuttxtp1 = "OF BOXXY TO MOVE";
            canvas.drawColor(Color.parseColor("#FF574F"));
            canvas.drawText(tuttxt1, xPos-tuttxt1.length()/2, yPos-scaledSize, textpaint);
            canvas.drawText(tuttxtp1, xPos-tuttxtp1.length()/2, yPos+scaledSize, textpaint);
            canvas.drawText(tap, xPos-tap.length()/2, yPos+scaledSize*3, textpaint);
        } else if (tutorial == 3) {
            String tuttxt1 = "COLLECT YELLOWS";
            String tuttxtp1 = "AVOID REDS";
            canvas.drawColor(Color.parseColor("#FF574F"));
            canvas.drawText(tuttxt1, xPos-tuttxt1.length()/2, yPos-scaledSize, textpaint);
            canvas.drawText(tuttxtp1, xPos-tuttxtp1.length()/2, yPos+scaledSize, textpaint);
            canvas.drawText(tap, xPos-tap.length()/2, yPos+scaledSize*3, textpaint);
        } else if (tutorial == 4) {
            String tuttxt1 = "KEEP TAPPING TO";
            String tuttxtp1 = "KEEP B0XXY FLOATY";
            canvas.drawColor(Color.parseColor("#FF574F"));
            canvas.drawText(tuttxt1, xPos-tuttxt1.length()/2, yPos-scaledSize, textpaint);
            canvas.drawText(tuttxtp1, xPos-tuttxtp1.length()/2, yPos+scaledSize, textpaint);
            canvas.drawText(tap, xPos-tap.length()/2, yPos+scaledSize*3, textpaint);
        } else if (tutorial == 5) {
            String tuttxt1 = "DONT LET";
            String tuttxtp1 = "BOXXY FALL";
            canvas.drawColor(Color.parseColor("#FF574F"));
            canvas.drawText(tuttxt1, xPos - tuttxt1.length() / 2, yPos - scaledSize, textpaint);
            canvas.drawText(tuttxtp1, xPos - tuttxtp1.length() / 2, yPos + scaledSize, textpaint);
            canvas.drawText(tap, xPos - tap.length() / 2, yPos + scaledSize * 3, textpaint);
        }

        if (settingscanvas == 1) {
            canvas.drawColor(Color.parseColor("#59E1C0"));
            String tapswitch = "TAP TO SWTICH";
            String sbt = "SWITCH THEME";
            canvas.drawText(tapswitch, xPos - tap.length() / 2, yPos - scaledSize * 6, textpaint);
            canvas.drawText(sbt, xPos - sbt.length() / 2, yPos - scaledSize * 4 , textpaint);

            if (bitmapsetting == 1){
                String btheme = "HUNGRY BOXXY";
                canvas.drawText(btheme, xPos - btheme.length() /2, yPos + scaledSize*2, textpaint);
            } else if (bitmapsetting == 2) {
                String otheme = "BASIC BOXXY";
                canvas.drawText(otheme, xPos - otheme.length() /2, yPos + scaledSize*2, textpaint);
            }
            canvas.drawBitmap(closebutton, lengthofscreen-closebutton.getWidth()-20, 20, null);
        }
    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (gameRan == 1) {
           //leave the player where it was and everything where it was
       } else {
           this.player = new Player(this, playerbitmap, mWidthSize-playerbitmap.getHeight()/2-playerbitmap.getHeight()/5, bottomofscreen/5+playerbitmap.getHeight()); //player
           gameRan = 0;
           Score = 0;
           player.jumpPower = 0;
           player.gravity = 0;
           player.vSpeed = 0;
           collectone.gravitynpc = 0;
           enemy.gravitynpc = 0;

       }
        this.gameThread = new GameThread(this,holder);
        this.gameThread.setRunning(true);
        this.gameThread.start();
    }

    //public void start() {
      //  if(!mGameIsRunning) {
        //    gameThread.start();
          //  mGameIsRunning = true;
        //} else {
        //    gameThread.onResume();
        //}
   // }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    // Implements method of SurfaceHolder.Callback
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
                gameRan = 1;
                player.jumpPower = 0;
                player.gravity = 0;
                player.vSpeed = 0;
                collectone.gravitynpc = 0;
                enemy.gravitynpc = 0;
    }
}