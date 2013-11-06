package com.project.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

public class glRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Ship playerShip;
    private Square   mSquare;
    private Square background;
    private ScoreBoard scoreB;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mScratch = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16];
    private final float[] mTranslationMatrix2 = new float[16];

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    public volatile float dyShip = 0.0f;
    public volatile float dxShip = 0.0f;
    public float dy=0.0f;
    long lastFrameTime = (long)0;
    public float dx=0.0f;
    public float dAngle = 0.0f;
    public float totalTime = 0.0f;
    public float sdx = 0.0f;
    public float sdy = 0.0f;
    public volatile boolean isShooting = false;
    private final int MAX_BULLET = 5;
    private final int MAX_ENEMY = 5;
    private Bullet[] bulletArray = new Bullet[MAX_BULLET];
    private EnemyShip[] enemyArray = new EnemyShip[MAX_BULLET];
    
    public int bulletCount = 0;
    public int bulletOnScreen = 0;
    public int enemyCount = 0;
    public int enemyOnScreen = 0;
    boolean done = false;
    long currentTime;
    private Context context;
    boolean genBullet = false;
    boolean genEnemy = false;
    Random randomG = new Random();

    
    public glRenderer(Context context) {
    
    	    this.context = context;
 
    	
    	}
    @Override

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
    	
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
       currentTime = System.currentTimeMillis();
       lastFrameTime = currentTime;

        //mTriangle = new Triangle();
        //mTriangle.loadGLTexture(unused, this.context);
       	playerShip = new Ship(0.0f,-0.8f,0.25f,0.25f);
       	playerShip.loadGLTexture(unused, this.context, R.drawable.airplane);
        //background = new Square(0.0f,0.0f,1.0f,1.0f);
        //background.loadGLTexture(unused, this.context);
        //mSquare = new Square(0.0f,-0.75f,0.025f,0.05f);
        scoreB = new ScoreBoard();
       
        
    };

    @Override
    public void onDrawFrame(GL10 unused) {
    	// Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
   
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        //background.draw(mMVPMatrix);
        
       
        rendership();
      
       
    	update(unused); 
    	renderbullet(unused);
    	renderEnemyShip(unused);
    	renderScoreBoard();
    	
    }
    
    public void update(GL10 gl){
    	currentTime =  System.currentTimeMillis();
    	float elapsed = (currentTime - lastFrameTime) * .001f;//convert ms to seconds
    	dy =  elapsed*1.0f;
    	//dAngle = elapsed*20.0f;
    	totalTime += elapsed;
    	lastFrameTime = currentTime; 
    	if(totalTime > 0.5f){
    		genBullet = true;
    		genEnemy = true;
    	}
    	if(isShooting&&genBullet){
    		//if (bulletOnScreen <=MAX_BULLET && genBullet) {
    			if (bulletCount == MAX_BULLET ) bulletCount = 0;
    		 bulletArray[bulletCount]   = new Bullet(playerShip.getShootX()+0.0f,playerShip.getShootY()+.025f,0.025f,0.05f);
    		 bulletArray[bulletCount].loadGLTexture(gl, this.context,R.drawable.pencil02);
    		 bulletCount++;
    		
    		 //if(bulletOnScreen<MAX_BULLET)
    		 //bulletOnScreen++;
    		//}
    	
    	}
    	
    	if(genEnemy){
    		int neg1 = randomG.nextInt(2);
    		int neg2 = randomG.nextInt(2);
    		int x1 = randomG.nextInt(10);
    		int x2 = randomG.nextInt(5);
    		if(neg1 == 1)
    			x1*=-1;
    		if(neg2 == 1)
    			x2*=-1;
    		
    		float xInt1 = x1*0.1f;
    		float xInt2 = x2*0.1f;
    		this.sdy = 1.0f;
    		this.sdx = xInt1 - xInt2;
    		
    		//if (enemyOnScreen <=MAX_ENEMY) {
    			if (enemyCount == MAX_ENEMY) enemyCount = 0;
    		
    		 enemyArray[enemyCount]   = new EnemyShip(xInt1,1.0f,0.15f,0.10f);
    		 
    		 enemyArray[enemyCount].loadGLTexture(gl, this.context,R.drawable.blackboard);
    		 
    		 enemyArray[enemyCount].sdy = this.sdy;
    		 enemyArray[enemyCount].sdx = this.sdx;
    		 
    		 enemyCount++;
    		 
    		 //if(enemyOnScreen<MAX_ENEMY)
    		 //enemyOnScreen++;
    		//}
    		
    			
    	}
    	if(genBullet && genEnemy){
    		 genBullet = false;
    		 genEnemy = false;
    		 totalTime = 0.0f;
    	}
    	
    	
        for( int i = 0; i<bulletCount; i++)
        	 for( int j = 0; j<enemyCount; j++)
        		 if ((bulletArray[i] != null)&&(enemyArray[j] != null)){
        			 boolean collide = checkCollision(bulletArray[i],enemyArray[j]);
        			 if(collide){
        				 enemyArray[j] = null;
        				 scoreB.loadGLTexture(gl, this.context);
        			 }
        		 }
    }
    
    public boolean checkCollision(Bullet b, EnemyShip e){
    	if(b.getLeftBound()<= e.getRightBound() && b.getRightBound()>=e.getLeftBound()&& b.getNorthBound()>= e.getSouthBound()&& b.getSouthBound()<= e.getNorthBound())
    		return true;
    	else return false;
    		
    }
    public void renderbullet(GL10 gl){
    	
        for( int i = 0; i<bulletCount; i++){
        	if (bulletArray[i] != null){
        	bulletArray[i].dy+=dy;
            
            Matrix.setIdentityM(mTranslationMatrix, 0);
            Matrix.translateM(mTranslationMatrix, 0, 0, bulletArray[i].dy, 0);
            Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0, mTranslationMatrix , 0);
            //bulletArray[i].loadGLTexture(gl, this.context);
            bulletArray[i].draw(mScratch);
        	}
        }
    	
    	//mSquare.draw(mScratch);
    	
    	

    }
    
    public void renderEnemyShip(GL10 gl){

        for( int i = 0; i<enemyCount; i++){
        	if(enemyArray[i] != null){
        		enemyArray[i].dy+=dy*enemyArray[i].sdy;
        		enemyArray[i].dx+=dy*enemyArray[i].sdx;
        		enemyArray[i].dAngle+=this.dAngle;
        		Matrix.setIdentityM(mTranslationMatrix, 0);
        		//Matrix.setIdentityM(mRotationMatrix, 0);
        		//Matrix.setRotateM(mRotationMatrix, 0,enemyArray[i].dAngle, enemyArray[i].getCX(), enemyArray[i].getCY(), 0.0f);
        		Matrix.translateM(mTranslationMatrix, 0, enemyArray[i].dx, -1.0f*enemyArray[i].dy, 0);
        		//Matrix.multiplyMM(mScratch, 0,mRotationMatrix, 0, mTranslationMatrix , 0);
        		Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mTranslationMatrix , 0);
        		//bulletArray[i].loadGLTexture(gl, this.context);
        		enemyArray[i].draw(mScratch);
        	}
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
    	playerShip.setX(dxShip);
    	playerShip.setY(dyShip);
    	playerShip.draw(mScratch);
    }
    public void renderScoreBoard(){
    	scoreB.draw(mMVPMatrix);
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


