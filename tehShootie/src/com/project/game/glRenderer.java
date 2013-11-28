package com.project.game;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

public class glRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    public volatile Ship playerShip;
    private Square   mSquare;
    private Square background;
    private GameOver gameOverScreen;
    private ScoreBoard scoreB;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mScratch = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16]; 
    private int[] mesh = new int[11];
    private Bitmap[] bitmaps = new Bitmap[11];

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    public volatile float dyShip = 0.0f;
    public volatile float dxShip = 0.0f;

    public float dy=0.0f;
    long lastFrameTime = (long)0;
    public float dx=0.0f;
    public float dAngle = 0.0f;
    public float totalTime = 0.0f;
    public float enemyTimer1 = 0.0f;
    public float enemyTimer2 = 0.5f;
    public float invciTime = 3.0f;
    public boolean invincible = false;
    public float sdx = 0.0f;
    public float sdy = 0.0f;
    public volatile boolean isShooting = false;
    private final int MAX_BULLET = 10;
    private final int MAX_ENEMY = 10;
    private Bullet[] bulletArray = new Bullet[MAX_BULLET];
    private EnemyShip[] enemyArray = new EnemyShip[MAX_ENEMY];
    private deadEnemyShip[] dEnemyShip = new deadEnemyShip[MAX_ENEMY];
    
    public int bulletCount = 0;
    public int bulletOnScreen = 0;
    public int enemyCount = 0;
    public int enemyOnScreen = 0;
    boolean done = false;
    long currentTime;
    private Context context;
    boolean genBullet = false;
    boolean genEnemy = false;
    boolean playerHit = false;
    boolean gameOver = false;
    boolean restart = false;
    boolean retryHold = false;
    boolean swarmHold = false;
    boolean a1= true;
	boolean a2 = true;
	boolean a3 = true;
	boolean a4 = true;
	boolean a5 = true;
    public int score = 0;
    public int score2 =0;
    Random randomG = new Random();
    boolean gamestart = true;
    int deadCount = 0;
    
    public glRenderer(Context context) {
    
    	    this.context = context;
 
    	
    	}
    @Override

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
    	
        //GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
       loadMesh(unused, this.context);
       loadBitmap(unused, this.context);
       currentTime = System.currentTimeMillis();
       lastFrameTime = currentTime;
       playerShip = new Ship(0.0f,-0.8f,0.25f,0.25f);
       playerShip.loadGLTexture(unused,bitmaps[0]);
       background = new Square(0.0f,0.0f,2.0f,2.0f);
       background.loadGLTexture(unused, bitmaps[3]);
       //mSquare = new Square(0.0f,-0.75f,0.025f,0.05f);
       scoreB = new ScoreBoard();
       gameOverScreen = new GameOver();
       gameOverScreen.loadGLTexture(unused, bitmaps[4], bitmaps[7],bitmaps[9]);
       gamestart = false;
        //mTriangle = new Triangle();
        //mTriangle.loadGLTexture(unused, this.context);
       
        
        
    };

    @Override
    public void onDrawFrame(GL10 unused) {
    
    	// Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GL10.GL_BLEND);

        // Set the camera position (View matrix)
        
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, 3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
   
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
        //background.draw(mMVPMatrix);
        
        if(restart){
        	 reset(unused);
        	 restart = false;
        	 gameOver = false;
              
        	
        }
        
        if(!gameOver){
    	update(unused); 
    	background.draw(mMVPMatrix);
    	rendership();
    	renderbullet(unused);
    	renderEnemyShip(unused);
    	renderScoreBoard();
    	renderDeadEnemy(unused);
    	}
        if(gameOver){
        	if(retryHold){
        		 gameOverScreen.loadGLTexture(unused,bitmaps[4], bitmaps[8],bitmaps[9]);
        	}
        	else if(swarmHold)
        		 gameOverScreen.loadGLTexture(unused,bitmaps[4], bitmaps[7],bitmaps[10]);
        	else
        		 gameOverScreen.loadGLTexture(unused,bitmaps[4], bitmaps[7],bitmaps[9]);
        	
        	background.draw(mMVPMatrix);
        	renderScoreBoard();
        	renderGameOver();
        }
    	
    }
    public void loadMesh(GL10 gl, Context context){
    	int[] mesh = {R.drawable.airplane,
    				  R.drawable.airplane02,
    				  R.drawable.blackboard,
    				  R.drawable.check,
    				  R.drawable.gameover,
    				  R.drawable.pencil,
    				  R.drawable.pencil02,
    				  R.drawable.retry1,
    				  R.drawable.retry2,
    				  R.drawable.swarm1,
    				  R.drawable.swarm2,
    	};  	
    	this.mesh = mesh;
    	
    }
    
    public void loadBitmap(GL10 gl, Context context){
    	final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
    	// loading texture
    	
    
    	for(int i =0;i<this.mesh.length;i++)
    	{
    		this.bitmaps[i] = BitmapFactory.decodeResource(context.getResources(),
        			this.mesh[i],options);
    	}
    		
    	
    }
    
    public void reset(GL10 gl){
    	gamestart = true;
    	 dy=0.0f;
    	 dx=0.0f;
    	 dAngle = 0.0f;
    	 totalTime = 0.0f;
    	 enemyTimer1 = 0.0f;
    	 enemyTimer2 = 0.5f;
    	 invciTime = 3.0f;
    	 invincible = false;
    	 sdx = 0.0f;
    	 sdy = 0.0f;
    	 bulletArray = new Bullet[MAX_BULLET];
    	 enemyArray = new EnemyShip[MAX_BULLET];
    	 bulletCount = 0;
    	 bulletOnScreen = 0;
    	 enemyCount = 0;
    	 enemyOnScreen = 0;
    	 done = false;
    	 genBullet = false;
    	 genEnemy = false;
    	 GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
         currentTime = System.currentTimeMillis();
         lastFrameTime = currentTime;
         score = 0;
         score2 = 0;

          //mTriangle = new Triangle();
          //mTriangle.loadGLTexture(unused, this.context);
         playerShip = new Ship(0.0f,-0.8f,0.25f,0.25f);
         playerShip.loadGLTexture(gl, bitmaps[0]);
         background = new Square(0.0f,0.0f,2.0f,2.0f);
         background.loadGLTexture(gl, bitmaps[3]);
          //mSquare = new Square(0.0f,-0.75f,0.025f,0.05f);
         scoreB = new ScoreBoard();
         gameOverScreen = new GameOver();
         gameOverScreen.loadGLTexture(gl, bitmaps[4], bitmaps[7],bitmaps[9]);
    }
    
    public void update(GL10 gl){
    	if(score2 > 10 && enemyTimer2 > 0.2f)
    	{
    		enemyTimer2-=0.1f;
    		if(enemyTimer2 <= 0.2f)
    			enemyTimer2 = 0.2f;
    		score2 =0;
    	}
    		
    	
    	currentTime =  System.currentTimeMillis();
    	float elapsed = (currentTime - lastFrameTime) * .001f;//convert ms to seconds
    	dy =  elapsed*1.0f;
    	dAngle = elapsed*20.0f;
    	totalTime += elapsed;
    	enemyTimer1 += elapsed;
    	lastFrameTime = currentTime; 
    	if(invincible)
    		invincibleani(elapsed,gl,true);
    	
    	
    	
    	if(totalTime > 0.5f){
    		genBullet = true;
    	}
    	if(enemyTimer1 > enemyTimer2)
    	{
    		genEnemy = true;
    	}
    	if(isShooting&&genBullet){
    		//if (bulletOnScreen <=MAX_BULLET && genBullet) {
    			if (bulletCount == MAX_BULLET ) bulletCount = 0;
    		 bulletArray[bulletCount]   = new Bullet(playerShip.getShootX()+0.0f,playerShip.getShootY()+.025f,0.025f,0.05f);
    		 bulletArray[bulletCount].loadGLTexture(gl, bitmaps[6]);
    		 bulletCount++;
    		
    		 //if(bulletOnScreen<MAX_BULLET)
    		 //bulletOnScreen++;
    		//}
    	
    	}
    	
    	if(genEnemy){
    		
    		int neg1 = randomG.nextInt(2);
    		int x1 = randomG.nextInt(5);
    		if(neg1 == 1)
    			x1*=-1;

  
    		float xInt1 = x1*0.1f;
    		
    		if (enemyCount == MAX_ENEMY) enemyCount = 0;
    		
    		 enemyArray[enemyCount]   = new EnemyShip(xInt1,1.0f,0.15f,0.10f);
    		 
    		 enemyArray[enemyCount].loadGLTexture(gl, bitmaps[2]);
    		 
    		 
    		 enemyCount++;
    		 
    		 //if(enemyOnScreen<MAX_ENEMY)
    		 //enemyOnScreen++;
    		//}
    		
    			
    	}
    	if(genBullet ){
    		 genBullet = false;
    		 totalTime = 0.0f;
    	}
    	if(genEnemy)
    	{
    		genEnemy = false;
    		enemyTimer1 = 0.0f;
    	}
    	
        for( int i = 0; i<MAX_BULLET; i++)
        	 for( int j = 0; j<MAX_ENEMY; j++){
        		 if ((enemyArray[j] != null)&&!invincible&& i == 0){
        			 boolean collideShip = checkCollisionShip(enemyArray[j]);
            	 	 if(collideShip){
            	 		 playerShip.life--;
            			 invincible = true;
            			 
            	     }
            		 
        		 }
        		 if ((bulletArray[i] != null)&&(enemyArray[j] != null)){
        			 boolean collide = checkCollision(bulletArray[i],enemyArray[j]);
        			 if(collide){
        				 if(deadCount == MAX_ENEMY) deadCount = 0;
        				 deadEnemyShip deadShip = new deadEnemyShip(enemyArray[j].getLeftBound(),
        						 									(bulletArray[i].getLeftBound()+bulletArray[i].getRightBound())/2,
        						 										enemyArray[j].getRightBound(),enemyArray[j].getCY(),enemyArray[j].getWidth(),enemyArray[j].getHeight());
        				 deadShip.loadGLTexture(gl, bitmaps[2]);
        				 dEnemyShip[deadCount] = deadShip;
        				 enemyArray[j] = null;
        				 scoreB.loadGLTexture(gl, this.context);
        				 score++;
        				 score2++;
        				 deadCount++;
        				 
        			 }
        		 }
        		 
        	}
        if(playerShip.life == 0){
        	gameOver = true;
        }
    }
    
    public void invincibleani(float elapsed,GL10 gl,boolean called)
    {
    
    	invciTime-=elapsed;
    	
    	if(invciTime <= 0)
    	{
    		playerShip.loadGLTexture(gl, bitmaps[0]);
    		invciTime = 3.0f;
    		invincible = false;
    		a1= true;
    		a2 = true;
    		a3 = true;
    		a4 = true;
    		a5 = true;
    	}
    	else if(invciTime <= 0.5 && a1){
    		playerShip.loadGLTexture(gl, bitmaps[1]);
    		a1 = false;
    	}
    	else if(invciTime <= 1.0 && a2){
    		playerShip.loadGLTexture(gl, bitmaps[0]);
    		a2 = false;
    	}
    	else if(invciTime <= 1.5 && a3){
    		playerShip.loadGLTexture(gl, bitmaps[1]);
    		a3 = false;
    	}
    	else if(invciTime <= 2 && a4){
    		playerShip.loadGLTexture(gl, bitmaps[0]);
    		a4 = false;
    	}
    	else if(invciTime <= 2.5 && a5){
    		playerShip.loadGLTexture(gl, bitmaps[1]);
    		a5 = false;
    	}
    }
    
    public boolean checkCollision(Bullet b, EnemyShip e){
    	if(b.getLeftBound()<= e.getRightBound() && b.getRightBound()>=e.getLeftBound()&& b.getNorthBound()>= e.getSouthBound()&& b.getSouthBound()<= e.getNorthBound())
    		return true;
    	else return false;
    		
    }
    public boolean checkCollisionShip( EnemyShip e){
    	if(playerShip.getLeftBound()<= e.getRightBound() && playerShip.getRightBound()>=e.getLeftBound()&& playerShip.getNorthBound()>= e.getSouthBound()&& playerShip.getSouthBound()<= e.getNorthBound())
    		return true;
    	else return false;
    		
    }
    public void renderbullet(GL10 gl){
    	
        for( int i = 0; i<MAX_BULLET; i++){
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

        for( int i = 0; i<MAX_ENEMY; i++){
        	if(enemyArray[i] != null){
        	
        		enemyArray[i].dy-=dy;
        		Matrix.setIdentityM(mTranslationMatrix, 0);
 
        		Matrix.translateM(mTranslationMatrix, 0, 0, enemyArray[i].dy, 0);
        		Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mTranslationMatrix , 0);
        		//bulletArray[i].loadGLTexture(gl, this.context);
        		enemyArray[i].draw(mScratch);
        	}
        }
    	
    	//mSquare.draw(mScratch);
    	
    	

    }
    public void renderDeadEnemy(GL10 gl){

        for( int i = 0; i<MAX_ENEMY; i++){
        	if(dEnemyShip[i] != null){
        	
        		dEnemyShip[i].dx+=dy;
        		dEnemyShip[i].dy+=(dy*2.0f);
        		Matrix.setIdentityM(mTranslationMatrix, 0);
        		Matrix.translateM(mTranslationMatrix, 0, 0, dEnemyShip[i].dy, 0);
        		Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mTranslationMatrix , 0);
        		//bulletArray[i].loadGLTexture(gl, this.context);
        		dEnemyShip[i].draw(mScratch);
        	}
        }
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
    	playerShip.setX(dxShip);
    	playerShip.setY(dyShip);
    	if(playerShip.getSouthBound()<=-0.925f){
    		dyShip = 0.0f;
    		playerShip.setY(dyShip);
    	}
    	if(playerShip.getNorthBound()>=1.0f){
    		dyShip = 1.675f;
    		playerShip.setY(dyShip);
    	}
    	if(playerShip.getLeftBound()<=-0.7f){
    		dxShip = -.575f;
    		playerShip.setY(dyShip);
    	}
    	if(playerShip.getRightBound()>=0.7f){
    		dxShip = 0.575f;
    		playerShip.setY(dyShip);
    	}
    	Matrix.setIdentityM(mTranslationMatrix, 0);
    	Matrix.translateM(mTranslationMatrix,0, dxShip, dyShip, 0);
    	Matrix.multiplyMM(mScratch, 0,mMVPMatrix  , 0,mTranslationMatrix, 0);
    	
    	playerShip.draw(mScratch,mMVPMatrix);
    
    }
    public void renderScoreBoard(){
    	scoreB.draw(mMVPMatrix);
    }
    
    public void renderGameOver(){
    	gameOverScreen.draw(mMVPMatrix);
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


