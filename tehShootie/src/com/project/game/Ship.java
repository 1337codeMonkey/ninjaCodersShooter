package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;

public class Ship {
	public float centerX;
	public float centerY;
	public float orgCenterX;
	public float orgCenterY;
	public float width;
	public float height;
	public int life = 3;
	public Square mSquare;
	public int resourceID;
	public Ship(float cx,float cy, float width, float height ) {
		mSquare = new Square(cx,cy,width,height);
		centerX = cx;
		centerY = cy;
		orgCenterX = centerX;
		orgCenterY = centerY;
		this.width = width;
		this.height = height;
				
	}
	 public void loadGLTexture(GL10 gl, Context context, int resouceID){
		 mSquare.loadGLTexture(gl, context, resouceID);
	 }
	 public void draw(float[] mvpMatrix) {
		 mSquare.draw(mvpMatrix);
	 }
	 public void setX(float x){
		 centerX = orgCenterX + x;
	 }
	 public void setY(float y){
		 centerY = orgCenterY + y;
	 }
	 public float getShootX(){
		 return centerX;
	 }
	 public float getShootY(){
		 return centerY+(0.5f*height);
	 }
	 public void isShoot(){
		 life+=-1;
	 }
}
