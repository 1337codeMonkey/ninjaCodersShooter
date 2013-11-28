package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class ScoreBoard {
	public Square mSquare1;
	public Square mSquare2;
	public Square mSquare3;
	public Square mSquare4;
	int score1 = 0;
	int score2 = 0;
	int score3 = 0;
	int score4 = 0;
	boolean change1 = false;
	boolean change2 = false;
	boolean change3 = false;
	boolean change4 = false;
	
	
	public ScoreBoard(){
		mSquare1 = new Square(0.3f,-.95f,0.1f,0.1f);
		mSquare2 = new Square(0.4f,-.95f,0.1f,0.1f);
		mSquare3 = new Square(0.5f,-.95f,0.1f,0.1f);
		mSquare4 = new Square(0.6f,-.95f,0.1f,0.1f);
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
		 
		 mSquare1.loadGLTexture(gl, context, getResourceId(score1));
		 mSquare2.loadGLTexture(gl, context, getResourceId(score2));
		 mSquare3.loadGLTexture(gl, context, getResourceId(score3));
		 mSquare4.loadGLTexture(gl, context, getResourceId(score4));
	 }
	 public int getResourceId(int x){
		 switch(x){
		 	case 0: return R.drawable.zero;
		 	case 1: return R.drawable.one;
		 	case 2: return R.drawable.two;
		 	case 3: return R.drawable.three;
		 	case 4: return R.drawable.four;
		 	case 5: return R.drawable.five;
		 	case 6: return R.drawable.six;
		 	case 7: return R.drawable.seven;
		 	case 8: return R.drawable.eight;
		 	case 9: return R.drawable.nine;
		 	default: return R.drawable.zero;
		 }
	 }
	 public void draw(float[] mvpMatrix) {
		 mSquare1.draw(mvpMatrix);
		 mSquare2.draw(mvpMatrix);
		 mSquare3.draw(mvpMatrix);
		 mSquare4.draw(mvpMatrix);
	 }
	 

}
