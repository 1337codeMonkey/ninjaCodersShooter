package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;

public class EnemyShip {
	public float centerX;
	public float centerY;
	public float orgCenterX;
	public float orgCenterY;
	public float width;
	public float height;
	public boolean isDead = false;
	public Square mSquare;
	public int resourceID;
	public float dy = 0.0f;
	public float dx = 0.0f;
	public float sdx = 0.0f;
	public float sdy = 0.0f;
	public float dAngle = 0.0f;
	public EnemyShip(float cx,float cy, float width, float height ) {
		mSquare = new Square(cx,cy,width,height);
		centerX = cx;
		centerY = cy;
		orgCenterX = centerX;
		orgCenterY = centerY;
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
		 centerX = orgCenterX + x;
	 }
	 public void setY(float y){
		 centerY = orgCenterY + y;
	 }
	 public float getCX(){
		 return centerX+dx;
	 }
	 public float getCY(){
		 return centerY+dy;
	 }
	 public float getLeftBound(){
		 return centerX+(dx)-(0.5f*width);
	 }
	 public float getRightBound(){
		 return centerX+(dx)+(0.5f*width);
	 }
	 public float getNorthBound(){
		 return centerY+(dy)+(0.5f*height);
	 }
	 public float getSouthBound(){
		 return centerY+(dy)-(0.5f*height);
	 }
	 public float getHeight(){
		 return this.height;
	 }
	 public float getWidth(){
		 return this.width;
		 
	 }
}
