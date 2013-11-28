package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

public class deadEnemyShip {
	TwoSquare mSquare1;
	TwoSquare mSquare2;
	float dx;
	float dy;
	
	public deadEnemyShip(float x1, float x2, float x3, float cy, float width, float height){
		float scale = (x2-x1)/width;
		 float textureCoords1[] = { 0.0f, 0.0f,
                 0.0f, 1.0f,
                 1.0f*scale, 1.0f,
                 1.0f*scale, 0.0f};
		 float textureCoords2[] = { 0.0f, 0.0f,
                 0.0f, 1.0f,
                 1.0f, 1.0f,
                 1.0f, 0.0f};
		mSquare1 = new TwoSquare(((x1+x2)/2.0f),cy,width,height,textureCoords1);
		mSquare2 = new TwoSquare(((x2+x3)/2.0f),cy,width,height,textureCoords2);
		
	}
	 public void loadGLTexture(GL10 gl ,Bitmap bitmap){
		 mSquare1.loadGLTexture(gl, bitmap);
		 mSquare2.loadGLTexture(gl, bitmap);
	 }
	 public void draw(float[] mvpMatrix) {
		 mSquare1.draw(mvpMatrix,-dx);
		 mSquare2.draw(mvpMatrix,dx);
	 }

}
