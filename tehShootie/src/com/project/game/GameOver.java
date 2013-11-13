package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class GameOver {
	
	public Square mSquare1;
	public Square mSquare2;
	
	
	
	public GameOver(){
		mSquare1 = new Square(0.0f,0.0f,0.7f,0.1f);
		mSquare2 = new Square(0.0f,-0.1f,0.4f,0.1f);
		
	}
	public void loadGLTexture(GL10 gl, Context context, int resourceID1, int resourceID2){
		
		 
		 mSquare1.loadGLTexture(gl, context, resourceID1);
		 mSquare2.loadGLTexture(gl, context, resourceID2);

	 }
	 
	 public void draw(float[] mvpMatrix) {
		 mSquare1.draw(mvpMatrix);
		 mSquare2.draw(mvpMatrix);
	 }


}

