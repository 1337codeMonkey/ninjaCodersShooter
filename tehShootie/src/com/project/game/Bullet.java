package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

public class Bullet {
	public float centerX;
	public float centerY;
	public float width;
	public float height;
	public int life = 3;
	public Square mSquare;
	public int resourceID;
	public float dy = 0.0f;
	public float dx = 0.0f;
	public Bullet(float cx,float cy, float width, float height ) {
		mSquare = new Square(cx,cy,width,height);
		centerX = cx;
		centerY = cy;
		this.width = width;
		this.height = height;
				
	}
	 public void loadGLTexture(GL10 gl, Bitmap bitmap){
		 mSquare.loadGLTexture(gl, bitmap);
	 }
	 public void draw(float[] mvpMatrix) {
		 mSquare.draw(mvpMatrix);
	 }
	 public void setX(float x){
		 this.centerX += x;
	 }
	 public void setY(float y){
		 this.centerY += y;
	 }
	 public float getCX(){
		 return centerX;
	 }
	 public float getCY(){
		 return centerY+dy;
	 }
	 public float getLeftBound(){
		 return centerX-(0.5f*width);
	 }
	 public float getRightBound(){
		 return centerX+(0.5f*width);
	 }
	 public float getNorthBound(){
		 return centerY+dy+(0.5f*height);
	 }
	 public float getSouthBound(){
		 return centerY+dy-(0.5f*height);
	 }
}
