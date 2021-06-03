package net.simplifiedcoding.fish;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class Enemy {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 1;
    private int hp = 0;
    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    Context context;
    Activity activity;

    //creating a rect object
    private Rect detectCollision;

    public Enemy(Context context, int screenX, int screenY, int hpPlayer, int level) {
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = (int) Math.round(screenY * 0.1);

        this.context = context;

        hp = new Random().nextInt(50) + hpPlayer - 40;

        activity = (Activity) context;
        Random generator = new Random();

        if(level==1){
            speed = 10;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        } else if(level == 2) {
            speed = 30;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.friend);
        }else if(level == 3){
            speed = 50;
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
        }

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();

        //initializing rect object
        detectCollision = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void update(int playerSpeed) {
        //x -= playerSpeed;
        x -= speed;
        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();
            //speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }



        //Adding the top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();


    }

    //adding a setter to x coordinate so that we can change it after collision
    public void setX(int x){

        this.x = x;

    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    //getters
    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth(){
        return bitmap.getWidth();
    }
    public void setHp(int _hp) { hp = _hp; }

    public int getHp() {return hp;}

    public int getSpeed() {
        return speed;
    }

}
