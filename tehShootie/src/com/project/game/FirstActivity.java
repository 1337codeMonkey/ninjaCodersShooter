package com.project.game;


import com.swarmconnect.*;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class FirstActivity extends SwarmActivity {

    private GLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
        mGLView = new MyGLSurfaceView(this);
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

class MyGLSurfaceView extends GLSurfaceView {

    private final glRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
      
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
    public boolean gameMovement(MotionEvent e){
    	  float x = e.getX();
          float y = e.getY();
          float normalizedX = x/(float)this.getWidth()*2-1;
          float normalizedY = -(y/(float)this.getHeight()*2-1);
          switch (e.getAction()) {
         

              case MotionEvent.ACTION_MOVE:
              	
                  

                  float dx = normalizedX - mPreviousX;
                  float dy = normalizedY - mPreviousY;

                 
                  mRenderer.dyShip = mRenderer.dyShip + dy;
                  mRenderer.dxShip = mRenderer.dxShip + dx;
               
                  mRenderer.isShooting = true;
              
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

