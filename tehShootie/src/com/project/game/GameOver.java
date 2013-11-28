package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

public class GameOver {
	
	public Square mSquare1;
	public Square mSquare2;
	public Square mSquare3;
	
	
	public GameOver(){
		mSquare1 = new Square(0.0f,0.0f,0.7f,0.1f);
		mSquare2 = new Square(0.0f,-0.1f,0.4f,0.1f);
		mSquare3 = new Square(0.0f,-0.2f,0.4f,0.1f);
		
	}
	public void loadGLTexture(GL10 gl, Bitmap bitmap1, Bitmap bitmap2, Bitmap bitmap3){
		
		 
		 mSquare1.loadGLTexture(gl, bitmap1);
		 mSquare2.loadGLTexture(gl, bitmap2);
		 mSquare3.loadGLTexture(gl, bitmap3);

	 }
	 
	 public void draw(float[] mvpMatrix) {
		 mSquare1.draw(mvpMatrix);
		 mSquare2.draw(mvpMatrix);
		 mSquare3.draw(mvpMatrix);
	 }


}

