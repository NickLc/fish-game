package net.simplifiedcoding.fish;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;

import static net.simplifiedcoding.fish.flying_fish_constants.death_animation_coordinates;
import static net.simplifiedcoding.fish.flying_fish_constants.iddle_animation_coordinates;

public class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int hp = 0;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -10;
    private int maxY;
    private int minY;

    private final int MIN_SPEED = 1;
    private final int MAX_SPEED = 20;

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

    public Player(Context context, int screenX, int screenY) {
        x = 75;
        y = 50;
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player_fish);
        //maxY = screenY - bitmap.getHeight(); antes de la animacion
        maxY = screenY - real_height;
        minY = 0;
        boosting = false;

        //initializing rect object
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        resetHp();

        //animation part------------------------------------------------------------------------------------------------------------------------------------------
        //swim animation
        this.iddle_animation = new Bitmap[animation_total_frames];
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f); //para darle vuelta a la imagen
        for(int frame = 0; frame< this.animation_total_frames; frame++ ) {
            this.iddle_animation[frame] = this.createSubImageAt(iddle_animation_coordinates[frame][0]*3.5,
                    iddle_animation_coordinates[frame][1]*3.5,
                    iddle_animation_coordinates[frame][2]*3.5,
                    iddle_animation_coordinates[frame][3]*3.5,
                    false);
            this.iddle_animation[frame] = Bitmap.createBitmap(this.iddle_animation[frame],
                    0, 0,
                    this.iddle_animation[frame].getWidth(),
                    this.iddle_animation[frame].getHeight(),
                    matrix,
                    true);
            this.iddle_animation[frame] = Bitmap.createScaledBitmap(this.iddle_animation[frame],
                    this.iddle_animation[frame].getWidth()*2,
                    this.iddle_animation[frame].getHeight()*2,
                    true);
        }

        //death animation
        this.death_animation = new Bitmap[animation_total_frames];
        for(int frame = 0; frame< this.animation_total_frames; frame++ ) {
            this.death_animation[frame] = this.createSubImageAt(death_animation_coordinates[frame][0]*3.5,
                    death_animation_coordinates[frame][1]*3.5,
                    death_animation_coordinates[frame][2]*3.5,
                    death_animation_coordinates[frame][3]*3.5,
                    false);
            this.death_animation[frame] = Bitmap.createBitmap(this.death_animation[frame],
                    0, 0,
                    this.death_animation[frame].getWidth(),
                    this.death_animation[frame].getHeight(),
                    matrix,
                    true);
            this.death_animation[frame] = Bitmap.createScaledBitmap(this.death_animation[frame],
                    this.death_animation[frame].getWidth()*2,
                    this.death_animation[frame].getHeight()*2,
                    true);
        }

        setReal_height(this.iddle_animation[0].getHeight());
        setReal_width(this.iddle_animation[0].getWidth());
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }

    public void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 5;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }

        //adding top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        /*detectCollision.right = x + bitmap.getWidth(); //Antes de la animacion
        detectCollision.bottom = y + bitmap.getHeight();*/
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

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    public Bitmap getBitmap() {
        return getCurrentAnimationFrame();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHeight(){
        // return bitmap.getHeight(); Antes de la animacion
        return real_height;
    }

    public int getWidth(){
        //return  bitmap.getWidth(); Antes de la animacion
        return real_width;
    }

    public void addHp(int _hp) { hp += _hp; }

    public int getHp() {return hp;}
    public void resetHp() {
        hp = 200;
    }
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
