package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

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
	public lifeCounter lifeC;
	public Ship(float cx,float cy, float width, float height ) {
		mSquare = new Square(cx,cy,width,height);
		centerX = cx;
		centerY = cy;
		orgCenterX = centerX;
		orgCenterY = centerY;
		this.width = width;
		this.height = height;
		lifeC = new lifeCounter();
				
	}
	 public void loadGLTexture(GL10 gl, Bitmap bitmap){
		 mSquare.loadGLTexture(gl, bitmap);
		 lifeC.loadGLTexture(gl, bitmap);
	 }
	 public void draw(float[] mvpMatrix1, float[] mvpMatrix2) {
		 mSquare.draw(mvpMatrix1);
		 lifeC.draw(mvpMatrix2,life);
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
	 public float getLeftBound(){
		 return centerX-(0.5f*width);
	 }
	 public float getRightBound(){
		 return centerX+(0.5f*width);
	 }
	 public float getNorthBound(){
		 return centerY+(0.5f*height);
	 }
	 public float getSouthBound(){
		 return centerY-(0.5f*height);
	 }
}
