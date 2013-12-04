package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public class BeamPower  extends Bullet{

	public float timeRe = 3.0f;
	public BeamPower(float cx,float cy, float width, float height )   {
		super(cx,cy,width,height);
				
	}
	 public float getLeftBound(){
		 return centerX+dx-(0.5f*width);
	 }
	 public float getRightBound(){
		 return centerX+dx+(0.5f*width);
	 }
	 public float getNorthBound(){
		 return centerY+dy+(0.5f*height);
	 }
	 public float getSouthBound(){
		 return centerY+dy-(0.5f*height);
	 }

}