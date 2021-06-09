package net.simplifiedcoding.fish;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    public static final int MAX_LEVEL = 3;
    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;
    public Bitmap background;
    public Bitmap[] backgroundsLevel;
    public Bitmap gameover_title;
    public Bitmap you_win_title;
    public int retrycounter=0;
    public int level;
    public static boolean youWon = false;
    //a screenX holder
    int screenX;
    int screenY;

    //context to be used in onTouchEvent to cause the activity transition from GameAvtivity to MainActivity.
    Context context;

    //the score holder
    int score;

    //the high Scores Holder
    int highScore[] = new int[4];

    //Shared Prefernces to store the High Scores
    SharedPreferences sharedPreferences;


    //to count the number of Misses
    int countMisses;

    //indicator that the enemy has just entered the game screen
    boolean flag ;

    //an indicator if the game is Over
    private boolean isGameOver ;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Enemy[] enemies;

    //defining a boom object to display blast
    private Boom boom;

    //the mediaplayer objects to configure the background music
    static  MediaPlayer gameOnsound;

    final MediaPlayer killedEnemysound;

    final MediaPlayer gameOversound;

    static MediaPlayer areYouWinningSon;

    public GameView(Context context, int _screenX, int _screenY) {
        super(context);
        level = 1;
        screenX = _screenX;
        screenY = _screenY;
        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        //initializing context
        this.context = context;

        backgroundsLevel = new Bitmap[4];
        backgroundsLevel[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        backgroundsLevel[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background2);
        backgroundsLevel[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background3);
        backgroundsLevel[3] = BitmapFactory.decodeResource(context.getResources(), R.drawable.background2);

        //set background game
        background = backgroundsLevel[0];

        enemies = new Enemy[4];
        for(int i=0; i<4; i++){
            enemies[i] = new Enemy(context,screenX,screenY, player.getHp(), level);
        }

        //initializing boom object
        boom = new Boom(context);

        //setting the score to 0 initially
        score = 0;

        //setting the countMisses to 0 initially
        countMisses = 0;

        this.screenX = screenX;

        isGameOver = false;

        //initializing shared Preferences
        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME",Context.MODE_PRIVATE);


        //initializing the array high scores with the previous values
        highScore[0] = sharedPreferences.getInt("score1",0);
        highScore[1] = sharedPreferences.getInt("score2",0);
        highScore[2] = sharedPreferences.getInt("score3",0);
        highScore[3] = sharedPreferences.getInt("score4",0);


        //initializing the media players for the game sounds
        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        killedEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOversound = MediaPlayer.create(context,R.raw.gameover);
        areYouWinningSon = MediaPlayer.create(context, R.raw.apex_legends_music);

        //starting the music to be played across the game
        gameOnsound.start();

        gameover_title = BitmapFactory.decodeResource(context.getResources(), R.drawable.gameover);
        this.gameover_title = Bitmap.createScaledBitmap(this.gameover_title,
                this.gameover_title.getWidth()*2,
                this.gameover_title.getHeight()*2,
                true);

        you_win_title = BitmapFactory.decodeResource(context.getResources(), R.drawable.champion);
        this.you_win_title = Bitmap.createScaledBitmap(this.you_win_title,
                (int) (this.you_win_title.getWidth() * 0.25),
                (int) (this.you_win_title.getHeight() * 0.25),
                true);

    }

    @Override
    public void run() {
        while (playing) {
            if(!youWon) update();
            draw();
            control();
        }
    }

    private void update() {

        //incrementing score as time passes
        score++;

        player.update();

        //setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);

        //setting the flag true when the enemy just enters the screen
        for(int i=0; i<4; i++){

            if(enemies[i].getX()==screenX){
                flag = true;
            }


            enemies[i].update(player.getHp());
            //if collision occurs with player
            if (Rect.intersects(player.getDetectCollision(), enemies[i].getDetectCollision())) {

                //displaying boom at that location
                boom.setX(enemies[i].getX());
                boom.setY(enemies[i].getY());

                //playing a sound at the collision between player and the enemy
                killedEnemysound.start();
                enemies[i].setX(-350);
                //enemies[i] =  new Enemy(context,screenX, screenY, player.getHp(), level);

                //add hp if player eat enemy
                if(player.getHp() > enemies[i].getHp()){
                    player.addHp(enemies[i].getHp());
                } else {
                    player.addHp(-enemies[i].getHp()/4);
                }

                //check hp player
                if(player.getHp() <= 0){
                    player.setAnimation_type(player.DEATH_ANIMATION);
                    gameOver();
                } else if (player.getHp() >= 500){
                    // Set next level
                    level += 1;
                    if(level > MAX_LEVEL){
                        youWon = true;
                        gameOver(); //change to Win player
                    }else {
                        // update Player
                        player.resetHp();
                        // update Background
                        background = backgroundsLevel[level-1];
                        //update all enemy
                        updateAllEnemy(player.getHp(), level);
                    }
                }

                // Set new attributes fish
                //enemies[i] =  new Enemy(context,screenX, screenY, player.getHp(), level);
                /*enemies[i].setX(-350);
                int hp = new Random().nextInt(200);
                enemies[i].setHp(hp);*/

            }


        }

    }

    public void updateAllEnemy(int playerHp, int level) {
        for(int i=0; i<4; i++) {
            enemies[i] =  new Enemy(context,screenX, screenY, playerHp, level);
        }
    }
    private void gameOver() {
        //setting playing false to stop the game.
        //playing = false;
        isGameOver = true;


        //stopping the gameon music
        gameOnsound.stop();
        //play the game over sound
        gameOversound.start();

        //Assigning the scores to the highscore integer array
        for(int i=0;i<4;i++){
            if(highScore[i]<score){

                final int finalI = i;
                highScore[i] = score;
                break;
            }
        }

        //storing the scores through shared Preferences
        SharedPreferences.Editor e = sharedPreferences.edit();

        for(int i=0;i<4;i++){

            int j = i+1;
            e.putInt("score"+j,highScore[i]);
        }
        e.apply();
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.WHITE);

            paint.setColor(Color.BLACK);
            paint.setTextSize(60);

            canvas.drawBitmap(background, 0, 0, paint);

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawText(player.getHp()+"", player.getX()+player.getWidth()/2,player.getY(), paint);

            for(int i=0; i<4; i++) {

                canvas.drawBitmap(
                        enemies[i].getBitmap(),
                        enemies[i].getX(),
                        enemies[i].getY(),
                        paint);

                canvas.drawText(enemies[i].getHp()+"", enemies[i].getX()+enemies[i].getWidth()/2, enemies[i].getY(), paint);

            }

            //drawing the score on the game screen
            paint.setColor(Color.BLACK);
            paint.setTextSize(80);
            canvas.drawText("Level:"+level,100,60,paint);
            canvas.drawText("Score:"+score,100,115,paint);


            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );


            //draw game Over when the game is over
            if(isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                //canvas.drawText("Game Over",canvas.getWidth()/2,yPos,paint);
                if (youWon){
                    areYouWinningSon.start();
                    canvas.drawBitmap(you_win_title,(canvas.getWidth()/2)-you_win_title.getWidth()/2,yPos-you_win_title.getHeight()/2,paint);
                }
                else canvas.drawBitmap(gameover_title,(canvas.getWidth()/2)-gameover_title.getWidth()/2,yPos-gameover_title.getHeight()/2,paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    //stop the music on exit
    public static void stopMusic(){

        gameOnsound.stop();
    }


    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if(!isGameOver) {
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    player.stopBoosting();
                    break;
                case MotionEvent.ACTION_DOWN:
                    player.setBoosting();
                    break;

            }
        }
        //if the game's over, tappin on game Over screen sends you to MainActivity
        else {
            if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                retrycounter++;
                if(retrycounter>1) {
                    context.startActivity(new Intent(context, MainActivity.class));
                }

            }

        }

        return true;

    }




}

