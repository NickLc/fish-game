package net.simplifiedcoding.fish;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

import java.util.Random;

import static net.simplifiedcoding.fish.enemy_constants.green_enemy_swim;
import static net.simplifiedcoding.fish.enemy_constants.red_enemy_swim;
import static net.simplifiedcoding.fish.flying_fish_constants.iddle_animation_coordinates;

public class Enemy {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 1;
    private int hp = 0;
    private int maxX;
    private int minX;

    private int[] speedsByLevel = {-1 , 15, 30, 30};

    private int maxY;
    private int minY;

    Context context;
    Activity activity;

    //creating a rect object
    private Rect detectCollision;


    //animation variables------------------------------------------------------------------------------------------------------------------------------------------
    private int real_height = 165;
    private int real_width = 165;
    private int animation_type = 0;
    private int animation_frame_index = 0;
    private int animation_total_frames = 24;
    private  Bitmap[] current_animation_frame;
    public static final int IDDLE_ANIMATION = 0;
    public static final int DEATH_ANIMATION = 1;
    private Bitmap[] iddle_animation;
    private Bitmap[] death_animation;


    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public Enemy(Context context, int screenX, int screenY, int hpPlayer, int level) {
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = (int) Math.round(screenY * 0.1);

        this.context = context;

        hp = new Random().nextInt(50) + hpPlayer - 40;

        activity = (Activity) context;
        Random generator = new Random();
        speed = getRandomNumber(speedsByLevel[level] - 2, speedsByLevel[level] + 2);

        if(level==1){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1and2);
        } else if(level == 2) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1and2);
        }else if(level == 3){
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy1and2);
        }

        x = screenX;
        y = generator.nextInt(maxY) - getHeight();

        //initializing rect object
        detectCollision = new Rect(x, y, bitmap.getWidth(), getHeight());

        //animation part------------------------------------------------------------------------------------------------------------------------------------------
        //swim animation
        if (level == 1) {
            this.iddle_animation = new Bitmap[animation_total_frames];
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f); //para darle vuelta a la imagen
            for (int frame = 0; frame < this.animation_total_frames; frame++) {
                this.iddle_animation[frame] = this.createSubImageAt(red_enemy_swim[frame][0] * 3.5,
                        red_enemy_swim[frame][1] * 3.5,
                        red_enemy_swim[frame][2] * 3.5,
                        red_enemy_swim[frame][3] * 3.5,
                        false);
                this.iddle_animation[frame] = Bitmap.createBitmap(this.iddle_animation[frame],
                        0, 0,
                        this.iddle_animation[frame].getWidth(),
                        this.iddle_animation[frame].getHeight(),
                        matrix,
                        true);
                this.iddle_animation[frame] = Bitmap.createScaledBitmap(this.iddle_animation[frame],
                        this.iddle_animation[frame].getWidth() * 2,
                        this.iddle_animation[frame].getHeight() * 2,
                        true);
            }
        }
        if (level == 2 || level == 3) {
            this.iddle_animation = new Bitmap[animation_total_frames];
            Matrix matrix = new Matrix();
            matrix.preScale(-1.0f, 1.0f); //para darle vuelta a la imagen
            for (int frame = 0; frame < this.animation_total_frames; frame++) {
                this.iddle_animation[frame] = this.createSubImageAt(green_enemy_swim[frame][0] * 3.5,
                        green_enemy_swim[frame][1] * 3.5,
                        green_enemy_swim[frame][2] * 3.5,
                        green_enemy_swim[frame][3] * 3.5,
                        false);
                this.iddle_animation[frame] = Bitmap.createBitmap(this.iddle_animation[frame],
                        0, 0,
                        this.iddle_animation[frame].getWidth(),
                        this.iddle_animation[frame].getHeight(),
                        matrix,
                        true);
                this.iddle_animation[frame] = Bitmap.createScaledBitmap(this.iddle_animation[frame],
                        this.iddle_animation[frame].getWidth() * 3,
                        this.iddle_animation[frame].getHeight() * 3,
                        true);
            }
        }

        setReal_height(this.iddle_animation[0].getHeight());
        setReal_width(this.iddle_animation[0].getWidth());

    }

    public void update(int playerHp) {
        //x -= playerSpeed;
        x -= speed;
        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();
            //speed = generator.nextInt(10) + 10;
            // speed = getRandomNumber(speed - 2, speed + 2);
            if(playerHp - 20 < 0){
                this.hp = getRandomNumber(1, playerHp + 20);
            }else {
                this.hp = getRandomNumber(playerHp - 20, playerHp + 20);
            }
            x = maxX;
            y = generator.nextInt(maxY) - getHeight();
        }

        //Adding the top, left, b ottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + real_width;
        detectCollision.bottom = y + real_height;

        //Animation part ------------------------------------------------------------------------------------------------------------------------------------------
        this.animation_frame_index++;
        if(this.hp <= 0 && this.animation_frame_index == this.animation_total_frames-1){
            this.animation_frame_index--;
        }
        else if(this.animation_frame_index >= this.animation_total_frames)  {
            this.animation_frame_index = 0;
        }

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
        return getCurrentAnimationFrame();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth(){
        return real_width;
    }

    public int getHeight(){
        // return getHeight(); Antes de la animacion
        return real_height;
    }

    public void setHp(int _hp) { hp = _hp; }

    public int getHp() {return hp;}

    public int getSpeed() {
        return speed;
    }

    //Animation methods ------------------------------------------------------------------------------------------------------------------------------------------
    protected Bitmap createSubImageAt(double col, double row, double col_len, double row_len, boolean type)  {
        // createBitmap(bitmap, x, y, width, height).
        int sprite_width=0;
        int sprite_height=0;
        Bitmap subImage;
        if (type) {
            subImage = Bitmap.createBitmap(bitmap, (int)(col * sprite_width), (int)(row * sprite_height), sprite_width, sprite_height);
            /*System.out.println("col * sprite_width = "+col * sprite_width);
            System.out.println("row * sprite_height = "+row * sprite_height);
            System.out.println("sprite_width = "+sprite_width);
            System.out.println("sprite_height = "+sprite_height);
            System.out.println("sheet_width = "+sheet_width);
            System.out.println("sheet_height = "+sheet_height);*/
        }else{
            subImage = Bitmap.createBitmap(bitmap, (int)col, (int)row, (int)col_len, (int)row_len);
        }
        return subImage;

    }

    public void setAnimation_type(int animation_type) {
        this.animation_type = animation_type;
    }

    public Bitmap[] getCurrentAnimation()  {
        switch (animation_type)  {
            case DEATH_ANIMATION:
                return this.death_animation;
            default:
                return this.iddle_animation;
        }
    }

    public Bitmap getCurrentAnimationFrame()  {
        current_animation_frame = this.getCurrentAnimation();
        return current_animation_frame[this.animation_frame_index];
    }

    /*static double[][] scalarProductMat(double mat[][], int k){
        for (int i = 0; i < mat.length; i++)
            for (int j = 0; j < mat[i].length; j++)
                mat[i][j] = mat[i][j] * k;
        return mat;
    }*/

    public void setReal_height(int real_height) {
        this.real_height = real_height;
    }

    public void setReal_width(int real_width) {
        this.real_width = real_width;
    }

}
