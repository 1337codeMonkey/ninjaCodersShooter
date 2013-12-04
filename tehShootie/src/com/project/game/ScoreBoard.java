package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ScoreBoard {
	public Square mSquare1;
	public Square mSquare2;
	public Square mSquare3;
	public Square mSquare4;
	int score1 = 0;
	int score2 = 0;
	int score3 = 0;
	int score4 = -1;
	boolean change1 = false;
	boolean change2 = false;
	boolean change3 = false;
	boolean change4 = false;
	public Bitmap[] bitmaps1 = new Bitmap[10];
	
	
	public ScoreBoard(Bitmap[] bitmap){
		mSquare1 = new Square(0.3f,-.95f,0.1f,0.1f);
		mSquare2 = new Square(0.4f,-.95f,0.1f,0.1f);
		mSquare3 = new Square(0.5f,-.95f,0.1f,0.1f);
		mSquare4 = new Square(0.6f,-.95f,0.1f,0.1f);


    	bitmaps1 = bitmap;
    
    	
	}
	 public void loadGLTexture(GL10 gl, Context context){
		 score4++;
		 if(score4 == 10)
		 {
			 score4 =0;
			 score3++;
		 }
		 if(score3 == 10)
		 {
			 score3 =0;
			 score2++;
		 }
		 if(score2 == 10)
		 {
			 score2 =0;
			 score1++;
		 }
		 
		 mSquare1.loadGLTexture(gl, getResourceId(score1));
		 mSquare2.loadGLTexture(gl, getResourceId(score2));
		 mSquare3.loadGLTexture(gl, getResourceId(score3));
		 mSquare4.loadGLTexture(gl, getResourceId(score4));
	 }
	 public Bitmap getResourceId(int x){
		 switch(x){
		 	case 0: return bitmaps1[0];
		 	case 1: return bitmaps1[1];
		 	case 2: return bitmaps1[2];
		 	case 3: return bitmaps1[3];
		 	case 4: return bitmaps1[4];
		 	case 5: return bitmaps1[5];
		 	case 6: return bitmaps1[6];
		 	case 7: return bitmaps1[7];
		 	case 8: return bitmaps1[8];
		 	case 9: return bitmaps1[9];
		 	default: return bitmaps1[0];
		 }
	 }
	 public void draw(float[] mvpMatrix) {
		 mSquare1.draw(mvpMatrix);
		 mSquare2.draw(mvpMatrix);
		 mSquare3.draw(mvpMatrix);
		 mSquare4.draw(mvpMatrix);
	 }
	 

}
