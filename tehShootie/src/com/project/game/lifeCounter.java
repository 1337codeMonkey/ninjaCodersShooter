package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

public class lifeCounter {
	
	public Square mSquare1;
	public Square mSquare2;
	public Square mSquare3;
	
	
	
	public lifeCounter(){
		mSquare1 = new Square(-0.4f,-.95f,0.1f,0.1f);
		mSquare2 = new Square(-0.5f,-.95f,0.1f,0.1f);
		mSquare3 = new Square(-0.6f,-.95f,0.1f,0.1f);
	}
	public void loadGLTexture(GL10 gl, Bitmap bitmap){
		
		 
		 mSquare1.loadGLTexture(gl, bitmap);
		 mSquare2.loadGLTexture(gl, bitmap);
		 mSquare3.loadGLTexture(gl, bitmap);

	 }
	 
	 public void draw(float[] mvpMatrix, int life) {
		 if(life == 3){
			 mSquare1.draw(mvpMatrix);
		 	 mSquare2.draw(mvpMatrix);
		 	 mSquare3.draw(mvpMatrix);
		 }
		 	 
		 if(life == 2){
			 mSquare2.draw(mvpMatrix);
			 mSquare3.draw(mvpMatrix);
		 }
			 
		 if(life == 1)
			 mSquare3.draw(mvpMatrix);
		 
	 }

}
