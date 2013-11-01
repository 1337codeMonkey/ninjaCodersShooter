/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.tehshootie;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class MyGLRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyGLRenderer";
    private Triangle mTriangle;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mScratch = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16];
    
    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    public volatile float dyShip = 0.0f;
    public volatile float dxShip = 0.0f;
    public float dy=0.0f;
    long lastFrameTime = (long)0;
    public float dx=0.0f;
    public volatile boolean isShooting = false;
    private final int MAX_BULLET = 20;
    private Square[] bulletArray = new Square[MAX_BULLET];
    
    public int bulletCount = 0;
    public int bulletOnScreen = 0;
    boolean done = false;
    long currentTime;

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
       currentTime = System.currentTimeMillis();
       lastFrameTime = currentTime;

        mTriangle = new Triangle();
        //mSquare = new Square(0.0f,-0.75f,0.025f,0.05f);
       
        
    }

    @Override
    public void onDrawFrame(GL10 unused) {
            // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
   
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
       
        rendership();
      
       
            update(); 
            renderbullet();
            
    }
    
    public void update(){
            currentTime =  System.currentTimeMillis();
            float elapsed = (currentTime - lastFrameTime) * .001f;//convert ms to seconds
            dy =  elapsed*7.0f;
            lastFrameTime = currentTime; 
            if(isShooting){
                    if (bulletOnScreen <=MAX_BULLET) {
                            if (bulletCount == MAX_BULLET) bulletCount = 0;
                     bulletArray[bulletCount]   = new Square(mTriangle.getTopVertexX()+0.0f,mTriangle.getTopVertexY()+.025f,0.025f,0.05f);
                     bulletCount++;
                     if(bulletOnScreen<MAX_BULLET)
                     bulletOnScreen++;
                    }
            
            }//
            
    }
    
    public void renderbullet(){
            if(bulletCount >1)
        for( int i = 0; i<bulletOnScreen; i++){
                bulletArray[i].dy+=dy;
            
            Matrix.setIdentityM(mTranslationMatrix, 0);
            Matrix.translateM(mTranslationMatrix, 0, 0, bulletArray[i].dy, 0);
            Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0, mTranslationMatrix , 0);
            bulletArray[i].draw(mScratch);
        }
            
            //mSquare.draw(mScratch);
            
            

    }
    
    public void rendership(){
        //Create a rotation for the triangle
            //long time = SystemClock.uptimeMillis() % 4000L;
            //float angle = 0.090f * ((int) time);
            // Set the camera position (View matrix)
        //Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
       // Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
      //Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);

      // Combine the rotation matrix with the projection and camera view
     // Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);

      // Draw triangle
            Matrix.setIdentityM(mTranslationMatrix, 0);
            Matrix.translateM(mTranslationMatrix,0, dxShip, dyShip, 0);
            Matrix.multiplyMM(mScratch, 0,mMVPMatrix  , 0,mTranslationMatrix, 0);
            mTriangle.setTopVertexX(dxShip);
            mTriangle.setTopVertexY(dyShip);
            mTriangle.draw(mScratch);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
}

