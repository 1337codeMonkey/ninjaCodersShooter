package com.project.game;


import com.swarmconnect.*;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class FirstActivity extends SwarmActivity  {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this,getSystemService(Context.SENSOR_SERVICE) );
        setContentView(mGLView);
        
        
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
}

class MyGLSurfaceView extends GLSurfaceView implements SensorEventListener {

    private final glRenderer mRenderer;
    float[] linearAcceleration = {0.0f, 0.0f};//, 0.0f};

    public MyGLSurfaceView(Context context,Object o) {
        super(context);
        SensorManager manager = (SensorManager) o; 
        Sensor accelerometer = manager.getSensorList( Sensor.TYPE_ACCELEROMETER).get(0);
        manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        
      
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);
       
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new glRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
    	if(!mRenderer.gameOver)
    		gameMovement (e);
    	if(mRenderer.gameOver)
    		getGameOverControl(e);
    	return true;
      
    }
    @Override
    public void onSensorChanged(SensorEvent event) { 
            float[] input = {event.values[0], event.values[1]};//,event.values[2]};                
            float[] gravity = {0.0f, 0.0f};//, 0.0f};

            float timeConstant = 0.18f;
            float alpha = 0.1f;
            float dt = 0;
             
            // Timestamps for the low-pass filters
            float timestamp = System.nanoTime();
            float timestampOld = System.nanoTime();
                     
        // Find the sample period (between updates).
        // Convert from nanoseconds to seconds
        dt = (timestamp - timestampOld) / 1000000000.0f;
     
        timestampOld = timestamp;
     
        alpha = timeConstant / (timeConstant + dt);
     
        gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
       // gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];
     
        //@Ky, I'm adding 1.0f under the intention of multiplying the 
        //ships coords by this value. If you want to add to the ship's 
        //coords, just remove this loop.
        //
        //change to i < 3 for z-axis support
        /*for(int i = 0; i < 2; i++){
                linearAcceleration[i] = input[i] - gravity[i];
                if(Math.abs(linearAcceleration[i]) < 1.0f)
                        linearAcceleration[i] = 1.0f;
        }*/
       
        mRenderer.dxShip = (-0.02f*(input[0]));
        mRenderer.dyShip = (-0.02f*(input[1]));
            
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
            
    }

    public float getX(){
            return linearAcceleration[0];
    }
    
    public float getY(){
            return linearAcceleration[1];
    }
    public boolean gameMovement(MotionEvent e){
    	  float x = e.getX();
          float y = e.getY();
          float normalizedX = x/(float)this.getWidth()*2-1;
          float normalizedY = -(y/(float)this.getHeight()*2-1);
          switch (e.getAction()) {
         
          	  case MotionEvent.ACTION_DOWN:
          		  mRenderer.shootBeam = true;
          		  return true;
          	  case MotionEvent.ACTION_UP:
          		  mRenderer.shootBeam = false;
          		  return true;
          		  
            /*  case MotionEvent.ACTION_MOVE:

                  float dx = normalizedX - mPreviousX;
                  float dy = normalizedY - mPreviousY;

                  mRenderer.dyShip = mRenderer.dyShip + dy;
                  mRenderer.dxShip = mRenderer.dxShip + dx;
                  
             
              */
            	  
               
          }

          mPreviousX = normalizedX;
          mPreviousY = normalizedY;
          return true;
    }
    public boolean getGameOverControl(MotionEvent e){
    	float x = e.getX();
        float y = e.getY();
        float normalizedX = x/(float)this.getWidth()*2-1;
        float normalizedY = -(y/(float)this.getHeight()*2-1);
        float dx = 0.2f;
        float dy = 0.05f;
       
        switch (e.getAction()) {
       
        	
        	case MotionEvent.ACTION_MOVE:
        		if(normalizedX<=(0.0f+dx) && normalizedX >=(0.0f-dx) && normalizedY<=(-0.1f+dy) && normalizedY>=(-0.1f-dy)){
        			mRenderer.retryHold = true;
        			mRenderer.swarmHold = false;
        		}
        			
        		if(normalizedX<=(0.0f+dx) && normalizedX >=(0.0f-dx) && normalizedY<=(-0.2f+dy) && normalizedY>=(-0.2f-dy)){
        			mRenderer.retryHold = false;
        			mRenderer.swarmHold = true;
        		}
        		return true;
            case MotionEvent.ACTION_UP:
            	if(normalizedX<=(0.0f+dx) && normalizedX >=(0.0f-dx) && normalizedY<=(-0.1f+dy) && normalizedY>=(-0.1f-dy))
            		mRenderer.restart = true;
            	if(normalizedX<=(0.0f+dx) && normalizedX >=(0.0f-dx) && normalizedY<=(-0.2f+dy) && normalizedY>=(-0.2f-dy))
            		SwarmLeaderboard.submitScore(12540, mRenderer.score);
            	mRenderer.retryHold = false;
            	mRenderer.swarmHold = false;
            	
            	
            
        }
    	return true;
    }
}

