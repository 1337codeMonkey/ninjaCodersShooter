package com.project.game;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public class Boss extends EnemyShip {
	public int life =100;
	public Square lifeS;
	public Boss(float cx,float cy, float width, float height ){
		super(cx,cy,width,height);
		lifeS =  new Square(0.0f,0.8f,1.0f,0.05f);
	}
	public float shoot1x(){
		float x = orgCenterX+dx-(width/2);
		float y = orgCenterY+dy;
		return (x*(float)Math.cos(dAngle))-(y*(float)Math.sin(dAngle));
		
	}
	public float shoot1y(){
		float x = orgCenterX+dx-(width/2);
		float y = orgCenterY+dy;
		return (x*(float)Math.sin(dAngle))+(y*(float)Math.cos(dAngle));
		
	}
	public float shoot2x(){
		float x = orgCenterX+dx;
		float y = orgCenterY+dy+(height/2);
		return (x*(float)Math.cos(dAngle))-(y*(float)Math.sin(dAngle));
		
	}
	public float shoot2y(){
		float x = orgCenterX+dx;
		float y = orgCenterY+dy+(height/2);
		return (x*(float)Math.sin(dAngle))+(y*(float)Math.cos(dAngle));
		
	}
	public float shoot3x(){
		float x = orgCenterX+dx+(width/2);
		float y = orgCenterY+dy;
		return (x*(float)Math.cos(dAngle))-(y*(float)Math.sin(dAngle));
		
	}
	public float shoot3y(){
		float x = orgCenterX+dx+(width/2);
		float y = orgCenterY+dy;
		return (x*(float)Math.sin(dAngle))+(y*(float)Math.cos(dAngle));
		
	}
	public float shoot4x(){
		float x = orgCenterX+dx;
		float y = orgCenterY+dy-(height/2);
		return (x*(float)Math.cos(dAngle))-(y*(float)Math.sin(dAngle));
		
	}
	public float shoot4y(){
		float x = orgCenterX+dx;
		float y = orgCenterY+dy-(height/2);
		return (x*(float)Math.sin(dAngle))+(y*(float)Math.cos(dAngle));
		
	}
	public void loadGLTexture(GL10 gl, Bitmap bitmap,Bitmap bitmap1){
		 mSquare.loadGLTexture(gl, bitmap);
		 lifeS.loadGLTextureC(gl, bitmap1);
		 this.bitmap = bitmap;
	 }
	 public void draw(float[] mvpMatrix,float[] mvpMatrix2) {
		 mSquare.draw(mvpMatrix);
		 lifeS.dy = (float)0.005f*(100-life);
		 lifeS.drawC(mvpMatrix2);
	 }
}
