package com.project.game;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class FirstActivity extends Activity {

    private GLSurfaceView mGLView;

    @SuppressLint("NewApi")
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

        float x = e.getX();
        float y = e.getY();
        float normalizedX = x/(float)this.getWidth()*2-1;
        float normalizedY = -(y/(float)this.getHeight()*2-1);
        switch (e.getAction()) {
       

            case MotionEvent.ACTION_MOVE:
            	
                

                float dx = normalizedX - mPreviousX;
                float dy = normalizedY - mPreviousY;

          
                mRenderer.dyShip = (mRenderer.dyShip + dy);
                mRenderer.dxShip = mRenderer.dxShip + dx;
                mRenderer.isShooting = true;
            
        }

        mPreviousX = normalizedX;
        mPreviousY = normalizedY;
        return true;
    }
}

