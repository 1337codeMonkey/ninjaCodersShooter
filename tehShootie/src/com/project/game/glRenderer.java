package com.project.game;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
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
    private Square background1;
    private GameOver gameOverScreen;
    private ScoreBoard scoreB;
    private BeamPower Beam;
    private Boss gameBoss;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mScratch = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mTranslationMatrix = new float[16]; 
    private final float[] mRotationMatrix = new float[16]; 
    private int[] mesh = new int[12];
    private Bitmap[] bitmaps = new Bitmap[12];
    private int[] mesh1 = new int[10];
    private Bitmap[] bitmaps1 = new Bitmap[10];
    private int[] mesh2 = new int[5];
    private Bitmap[] bitmaps2 = new Bitmap[5];

    // Declare as volatile because we are updating it from another thread
    public volatile float mAngle;
    public volatile float dyShip = 0.0f;
    public volatile float dxShip = 0.0f;

    public float dy=0.0f;
    public float dyBackground = 0.0f;
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
    public volatile boolean isShooting = true;
    public volatile boolean shootBeam = false;
    private final int MAX_BULLET = 10;
    private final int MAX_ENEMY = 50;
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
    boolean genBeam = false;
    boolean isFiringBeam = false;
    boolean bossTime = false;
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
    public int score3 =0;
    Random randomG = new Random();
    boolean gamestart = true;
    int deadCount = 0;
    Activity act;
	private AndroidAudio audio;
    public volatile AndroidMusic music;
    public float movex = 1.0f;
    public glRenderer(Context context,Activity act) {
    
    	    this.context = context;
    	    this.act = act;
 
    	
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
       playerShip.loadGLTexture(unused,bitmaps[0],bitmaps2[0]);
       background = new Square(0.0f,0.0f,2.0f,2.0f);
       background.loadGLTextureB(unused, bitmaps[2]);
       //background1 = new Square(0.0f,2.0f,2.0f,2.0f);
       //background1.loadGLTexture(unused, bitmaps[2]);
       //mSquare = new Square(0.0f,-0.75f,0.025f,0.05f);
       scoreB = new ScoreBoard(bitmaps1);
       scoreB.loadGLTexture(unused, this.context);
       gameOverScreen = new GameOver();
       gameOverScreen.loadGLTexture(unused, bitmaps[3], bitmaps[6],bitmaps[8]);
       gamestart = false;
        //mTriangle = new Triangle();
        //mTriangle.loadGLTexture(unused, this.context);
       audio = new AndroidAudio(act);
       music = (AndroidMusic) audio.newMusic("vemiRuma.ogg");
       music.setLooping(true);
   
        
        
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
        	 bossTime = false;
              
        	
        }
        
        if(!gameOver){
        	if(bossTime)
        		updateBoss(unused);
        	else update(unused); 
    	
        	renderBackground(unused);
        	rendership(unused);
        	renderbullet(unused);
        	if(bossTime){
        		renderBoss(unused);
        	}
        	renderEnemyShip(unused);
        	renderScoreBoard();
        	renderDeadEnemy(unused);
    	}
        if(gameOver){
        	if(retryHold){
        		 gameOverScreen.loadGLTexture(unused,bitmaps[3], bitmaps[7],bitmaps[8]);
        	}
        	else if(swarmHold)
        		 gameOverScreen.loadGLTexture(unused,bitmaps[3], bitmaps[6],bitmaps[9]);
        	else
        		 gameOverScreen.loadGLTexture(unused,bitmaps[3], bitmaps[6],bitmaps[8]);
        	
        	background.draw(mMVPMatrix);
        	renderScoreBoard();
        	renderGameOver();
        }
        
    	
    }
    public void loadMesh(GL10 gl, Context context){
    	int[] mesh = {R.drawable.airplane,
    				  R.drawable.airplane02,
    				  R.drawable.scrollback,
    				  R.drawable.gameover,
    				  R.drawable.pencil,
    				  R.drawable.pencil02,
    				  R.drawable.retry1,
    				  R.drawable.retry2,
    				  R.drawable.swarm1,
    				  R.drawable.swarm2,
    				  R.drawable.beam,
    				  R.drawable.beam2
    	};  	
    	this.mesh = mesh;
	 	int[] mesh1 = {R.drawable.zero,
				  R.drawable.one,
				  R.drawable.two,
				  R.drawable.three,
				  R.drawable.four,
				  R.drawable.five,
				  R.drawable.six,
				  R.drawable.seven,
				  R.drawable.eight,
				  R.drawable.nine,
				
	 	};  
	 	this.mesh1 = mesh1;
		int[] mesh2 = {R.drawable.powerup,
				  	   R.drawable.madpoo,
				  	   R.drawable.zaozombie,
				  	   R.drawable.evilbear,
				  	   R.drawable.bosslifebar
	 	};  
	 	this.mesh2 = mesh2;
    	
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
    	for(int i =0;i<this.mesh1.length;i++)
    	{
    		this.bitmaps1[i] = BitmapFactory.decodeResource(context.getResources(),
        			this.mesh1[i],options);
    	}
    	for(int i =0;i<this.mesh2.length;i++)
    	{
    		this.bitmaps2[i] = BitmapFactory.decodeResource(context.getResources(),
        			this.mesh2[i],options);
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
    	 enemyArray = new EnemyShip[MAX_ENEMY];
    	 dEnemyShip = new deadEnemyShip[MAX_ENEMY];
    	 bulletCount = 0;
    	 bulletOnScreen = 0;
    	 enemyCount = 0;
    	 enemyOnScreen = 0;
    	 done = false;
    	 genBullet = true;
    	 genEnemy = false;
    	 genBeam = false;
    	 isFiringBeam = false;
    	 shootBeam = false;
    	 bossTime = false;
    	 isShooting = true;
    	 GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
         currentTime = System.currentTimeMillis();
         lastFrameTime = currentTime;
         score = 0;
         score2 = 0;

          //mTriangle = new Triangle();
          //mTriangle.loadGLTexture(unused, this.context);
         playerShip = new Ship(0.0f,-0.8f,0.25f,0.25f);
         playerShip.loadGLTexture(gl, bitmaps[0],bitmaps2[0]);
         background = new Square(0.0f,0.0f,2.0f,2.0f);
         background.loadGLTextureB(gl, bitmaps[2]);
         //background1 = new Square(0.0f,2.0f,2.0f,2.0f);
         //background1.loadGLTexture(gl, bitmaps[2]);
          //mSquare = new Square(0.0f,-0.75f,0.025f,0.05f);
         scoreB = new ScoreBoard(bitmaps1);
         scoreB.loadGLTexture(gl, this.context);
         gameOverScreen = new GameOver();
         gameOverScreen.loadGLTexture(gl, bitmaps[3], bitmaps[6],bitmaps[8]);
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
    	dyBackground += -dy;
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
    	if(shootBeam&&playerShip.powerAmount >0){
    		isShooting = false;
    		genBeam = true;
    		shootBeam = false;
    		
    	}
    	if(genBeam)
    	{
    		Beam = new BeamPower(0.0f,0.45f,0.2f,2.0f);
    		Beam.loadGLTexture(gl, bitmaps[10]);
    		playerShip.powerAmount--;
    		isFiringBeam = true;
    		genBeam = false;
    	}
    	
    	if(isFiringBeam){
    		Beam.timeRe-=elapsed;
    		if(Beam.timeRe <=0.0f){
    			isFiringBeam = false;
    			isShooting = true;
    		}
    		if(Beam.timeRe<=0.5f)
    			Beam.loadGLTexture(gl, bitmaps[11]);
    		else if(Beam.timeRe<=1.0f)
    			Beam.loadGLTexture(gl, bitmaps[10]);
    		else if(Beam.timeRe<=1.5f)
    			Beam.loadGLTexture(gl, bitmaps[11]);
    		else if(Beam.timeRe<=2.0f)
    			Beam.loadGLTexture(gl, bitmaps[10]);
    		else if(Beam.timeRe<=2.5f)
    			Beam.loadGLTexture(gl, bitmaps[11]);
    	}
    	if(isShooting&&genBullet){
    		//if (bulletOnScreen <=MAX_BULLET && genBullet) {
    			if (bulletCount == MAX_BULLET ) bulletCount = 0;
    		 bulletArray[bulletCount]   = new Bullet(playerShip.getShootX()+0.0f,playerShip.getShootY(),0.025f,0.05f);
    		 bulletArray[bulletCount].loadGLTexture(gl, bitmaps[5]);
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

    		int t = randomG.nextInt(2)+1;
    		float xInt1 = x1*0.1f;
    		
    		if (enemyCount == MAX_ENEMY) enemyCount = 0;
    		
    		 enemyArray[enemyCount]   = new EnemyShip(xInt1,1.0f,0.3f,0.3f);
    		 if(score3 >= 20){
    			 enemyArray[enemyCount].beamPowerUp = true;
    			 enemyArray[enemyCount].loadGLTexture(gl, bitmaps2[0]);
    			 score3 = 0;
    		 }
    		 else
    		 enemyArray[enemyCount].loadGLTexture(gl, bitmaps2[t]);
    		 
    		 
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
        		 if(enemyArray[j] != null&&enemyArray[j].beamPowerUp){
        			 boolean collideShip = checkCollisionShip(enemyArray[j]);
            	 	 if(collideShip){
            	 		 if( playerShip.powerAmount<3)
            	 			 playerShip.powerAmount++;
            	 		 enemyArray[j] = null;
            	 	 }
        		 }
        		 else if ((enemyArray[j] != null)&&!invincible&& i == 0){
        			 boolean collideShip = checkCollisionShip(enemyArray[j]);
            	 	 if(collideShip){
            	 			 playerShip.life--;
            	 			 invincible = true;
            	 		 
            	     }
            		 
        		 }
        		 if ((bulletArray[i] != null)&&(enemyArray[j] != null)&&(!enemyArray[j].beamPowerUp)){
        			 boolean collide = false;
        			 if(isShooting)collide = checkCollision(bulletArray[i],enemyArray[j]);
        			 
        			 if(collide){
        				 Bullet b = bulletArray[i];
        				 if(isFiringBeam) b = Beam;
        				 if(deadCount == MAX_ENEMY) deadCount = 0;
        				 deadEnemyShip deadShip = new deadEnemyShip(enemyArray[j].getLeftBound(),
        						 									(b.getLeftBound()+b.getRightBound())/2,
        						 										enemyArray[j].getRightBound(),enemyArray[j].getCY(),enemyArray[j].getWidth(),enemyArray[j].getHeight());
        				 deadShip.loadGLTexture(gl, enemyArray[j].bitmap);
        				 dEnemyShip[deadCount] = deadShip;
        				 enemyArray[j] = null;
        				 bulletArray[i] = null;
        				 scoreB.loadGLTexture(gl, this.context);
        				 score++;
        				 score2++;
        				 score3++;
        				 deadCount++;
        				 
        			 }
        		 }
        		 if(isFiringBeam&&enemyArray[j]!=null){
        			 if(!enemyArray[j].beamPowerUp){
        			 boolean collide = checkCollision(Beam,enemyArray[j]);
        			 if(collide){
        				 Bullet b = bulletArray[i];
        				 if(isFiringBeam) b = Beam;
        				 if(deadCount == MAX_ENEMY) deadCount = 0;
        				 deadEnemyShip deadShip = new deadEnemyShip(enemyArray[j].getLeftBound(),
        						 									(b.getLeftBound()+b.getRightBound())/2,
        						 										enemyArray[j].getRightBound(),enemyArray[j].getCY(),enemyArray[j].getWidth(),enemyArray[j].getHeight());
        				 deadShip.loadGLTexture(gl, enemyArray[j].bitmap);
        				 dEnemyShip[deadCount] = deadShip;
        				 enemyArray[j] = null;
        				 scoreB.loadGLTexture(gl, this.context);
        				 score++;
        				 score2++;
        				 score3++;
        				 deadCount++;
        			 }
        			 }
        		 }
        	}
        if(playerShip.life == 0){
        	gameOver = true;
        }
        if(score >= 50 && score < 100)
        {
        	gameBoss = new Boss(0.0f,1.5f,0.5f,0.5f);
        	gameBoss.loadGLTexture(gl, bitmaps2[3],bitmaps2[4]);
        	bossTime = true;
        	enemyTimer2 = 0.5f;
        	enemyArray = new EnemyShip[MAX_ENEMY];
        	
        }
    }
    public void updateBoss(GL10 gl){
    	currentTime =  System.currentTimeMillis();
    	float elapsed = (currentTime - lastFrameTime) * .001f;//convert ms to seconds
    	dy =  elapsed*1.0f;
    	dyBackground = 0.0f;
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
    	if(shootBeam&&playerShip.powerAmount >0){
    		isShooting = false;
    		genBeam = true;
    		shootBeam = false;
    		
    	}
    	if(genBeam)
    	{
    		Beam = new BeamPower(0.0f,0.45f,0.2f,2.0f);
    		Beam.loadGLTexture(gl, bitmaps[10]);
    		playerShip.powerAmount--;
    		isFiringBeam = true;
    		genBeam = false;
    	}
    	
    	if(isFiringBeam){
    		Beam.timeRe-=elapsed;
    		if(Beam.timeRe <=0.0f){
    			isFiringBeam = false;
    			isShooting = true;
    		}
    		if(Beam.timeRe<=0.5f)
    			Beam.loadGLTexture(gl, bitmaps[11]);
    		else if(Beam.timeRe<=1.0f)
    			Beam.loadGLTexture(gl, bitmaps[10]);
    		else if(Beam.timeRe<=1.5f)
    			Beam.loadGLTexture(gl, bitmaps[11]);
    		else if(Beam.timeRe<=2.0f)
    			Beam.loadGLTexture(gl, bitmaps[10]);
    		else if(Beam.timeRe<=2.5f)
    			Beam.loadGLTexture(gl, bitmaps[11]);
    	}
    	if(isShooting&&genBullet){
    		//if (bulletOnScreen <=MAX_BULLET && genBullet) {
    			if (bulletCount == MAX_BULLET ) bulletCount = 0;
    		 bulletArray[bulletCount]   = new Bullet(playerShip.getShootX()+0.0f,playerShip.getShootY(),0.025f,0.05f);
    		 bulletArray[bulletCount].loadGLTexture(gl, bitmaps[5]);
    		 bulletCount++;
    		
    		 //if(bulletOnScreen<MAX_BULLET)
    		 //bulletOnScreen++;
    		//}
    	
    	}
    	for(int i =0;i<4;i++){
    		if(genEnemy){
    			int t = randomG.nextInt(2)+1;

    			if (enemyCount == MAX_ENEMY) enemyCount = 0;
    			if(i==0)
    				enemyArray[enemyCount]   = new EnemyShip(gameBoss.shoot1x(),gameBoss.shoot1y(),0.1f,0.1f);
    			else if(i==1)
    				enemyArray[enemyCount]   = new EnemyShip(gameBoss.shoot2x(),gameBoss.shoot2y(),0.1f,0.1f);
    			else if(i==2)
    				enemyArray[enemyCount]   = new EnemyShip(gameBoss.shoot3x(),gameBoss.shoot3y(),0.1f,0.1f);
    			else if(i==3)
    				enemyArray[enemyCount]   = new EnemyShip(gameBoss.shoot4x(),gameBoss.shoot4y(),0.1f,0.1f);
    		
    			enemyArray[enemyCount].loadGLTexture(gl, bitmaps2[t]);
    			enemyArray[enemyCount].sdy = enemyArray[enemyCount].centerY;
    			enemyArray[enemyCount].sdx = enemyArray[enemyCount].centerX;
    			
    			enemyCount++;
    			
	    	}
    	}
    	
    	 for( int j = 0; j<MAX_ENEMY; j++){
    		 if ((enemyArray[j] != null)&&!invincible){
    			 boolean collideShip = checkCollisionShip(enemyArray[j]);
    			 if(collideShip){
    	 			 playerShip.life--;
    	 			 invincible = true;
    	 		 
    			 }
    		 }
    	 }
    	 for( int j = 0; j<MAX_BULLET; j++){
    		 if(bulletArray[j]!=null&&isShooting){
    			 boolean collide = checkCollision(bulletArray[j],gameBoss);
    			 if(collide){
    				 gameBoss.life--;
    				 if(gameBoss.life == 0){
    					 dEnemyShip = new deadEnemyShip[MAX_ENEMY];
    					 dEnemyShip[0] = new deadEnemyShip(gameBoss.getLeftBound(),
									(bulletArray[j].getLeftBound()+bulletArray[j].getRightBound())/2,
									gameBoss.getRightBound(),gameBoss.getCY(),gameBoss.getWidth(),gameBoss.getHeight());
    					 dEnemyShip[0].loadGLTexture(gl, gameBoss.bitmap);
    						bossTime = false;
    			        	score+=1000;
    			        	this.scoreB.loadGLTextureB(gl, this.context);
    			        	enemyArray = new EnemyShip[MAX_ENEMY];
    				 }
    				 bulletArray[j]=null;
    			 }
    		 }

    	 }
    	 if(isFiringBeam&&bossTime){
    		 boolean collide = checkCollision(this.Beam,gameBoss);
			 if(collide){
				 gameBoss.life--;
				 if(gameBoss.life == 0){
					 dEnemyShip = new deadEnemyShip[MAX_ENEMY];
					 dEnemyShip[0] = new deadEnemyShip(gameBoss.getLeftBound(),
								(Beam.getLeftBound()+Beam.getRightBound())/2,
								gameBoss.getRightBound(),gameBoss.getCY(),gameBoss.getWidth(),gameBoss.getHeight());
					 dEnemyShip[0].loadGLTexture(gl, gameBoss.bitmap);
						bossTime = false;
			        	score+=1000;
			        	this.scoreB.loadGLTextureB(gl, this.context);
			        	enemyArray = new EnemyShip[MAX_ENEMY];
				 }
			 }
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
    	
        
        if(playerShip.life == 0){
        	gameOver = true;
        }
        if(gameBoss.life == 0){
        
        	
        }
    	
    }
    public void invincibleani(float elapsed,GL10 gl,boolean called)
    {
    
    	invciTime-=elapsed;
    	
    	if(invciTime <= 0)
    	{
    		playerShip.loadGLTexture(gl, bitmaps[0],bitmaps2[0]);
    		invciTime = 3.0f;
    		invincible = false;
    		a1= true;
    		a2 = true;
    		a3 = true;
    		a4 = true;
    		a5 = true;
    	}
    	else if(invciTime <= 0.5 && a1){
    		playerShip.loadGLTexture(gl, bitmaps[1],bitmaps2[0]);
    		a1 = false;
    	}
    	else if(invciTime <= 1.0 && a2){
    		playerShip.loadGLTexture(gl, bitmaps[0],bitmaps2[0]);
    		a2 = false;
    	}
    	else if(invciTime <= 1.5 && a3){
    		playerShip.loadGLTexture(gl, bitmaps[1],bitmaps2[0]);
    		a3 = false;
    	}
    	else if(invciTime <= 2 && a4){
    		playerShip.loadGLTexture(gl, bitmaps[0],bitmaps2[0]);
    		a4 = false;
    	}
    	else if(invciTime <= 2.5 && a5){
    		playerShip.loadGLTexture(gl, bitmaps[1],bitmaps2[0]);
    		a5 = false;
    	}
    }
    
    public boolean checkCollision(Bullet b, EnemyShip e){
    	if(b.getLeftBound()<= e.getRightBound() && b.getRightBound()>=e.getLeftBound()&& b.getNorthBound()>= e.getSouthBound()&& b.getSouthBound()<= e.getNorthBound())
    		return true;
    	else return false;
    		
    }
    public boolean checkCollisionShip( EnemyShip e){
    	if(playerShip.getLeftBound()+.1f<= e.getRightBound() && playerShip.getRightBound()-.1f>=e.getLeftBound()&& playerShip.getNorthBound()-.1f>= e.getSouthBound()+.1f&& playerShip.getSouthBound()<= e.getNorthBound())
    		return true;
    	else return false;
    		
    }
    public void renderBackground(GL10 gl){
    	background.dy+=(0.1*dy);
    	if(background.dy>=1.0f)
    		background.dy = 0.0f+(background.dy-1.0f);
        ///Matrix.setIdentityM(mTranslationMatrix, 0);
       // Matrix.translateM(mTranslationMatrix, 0, 0, dyBackground, 0);
       // Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0, mTranslationMatrix , 0);
        //bulletArray[i].loadGLTexture(gl, this.context);
    	background.drawB(mMVPMatrix);
    	
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
    public void renderBoss(GL10 gl){
    	if(gameBoss.getCY() >=0.0f)
    		gameBoss.dy-=dy/2;
    	gameBoss.dAngle+=dAngle;
    	Matrix.setIdentityM(mRotationMatrix, 0);
    	Matrix.setIdentityM(mTranslationMatrix, 0);
        Matrix.translateM(mTranslationMatrix, 0, 0, gameBoss.dy, 0);
		Matrix.rotateM(mRotationMatrix, 0, (float)gameBoss.dAngle, 0.0f, 0.0f, 1.0f);
		Matrix.multiplyMM(mScratch,0,mRotationMatrix,0,mTranslationMatrix,0);
		Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mScratch , 0);
		gameBoss.draw(mScratch,mMVPMatrix);
    }
    
    public void renderEnemyShip(GL10 gl){

        for( int i = 0; i<MAX_ENEMY; i++){
        	
        	if(enemyArray[i] != null){
        		if(!bossTime){
        			enemyArray[i].dy-=dy;
        			if(enemyArray[i].dx>0.2f){
        				movex = -movex;
        				enemyArray[i].dx = 0.2f;
        			}
        			if(enemyArray[i].dx <-0.2f){
        				movex = -movex;
        				enemyArray[i].dx = -0.2f;
        			}
        				enemyArray[i].dx+=(movex*dy);
        				Matrix.setIdentityM(mTranslationMatrix, 0);
 
        				Matrix.translateM(mTranslationMatrix, 0, enemyArray[i].dx, enemyArray[i].dy, 0);
        				Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mTranslationMatrix , 0);
        				//bulletArray[i].loadGLTexture(gl, this.context);
        				enemyArray[i].draw(mScratch);
        		}
        		if(bossTime){
        			enemyArray[i].dx = (enemyArray[i].dx)+(dy*enemyArray[i].sdx);
        			enemyArray[i].dy = (enemyArray[i].dy)+(dy*enemyArray[i].sdy);
        			Matrix.setIdentityM(mTranslationMatrix, 0);
        			 
    				Matrix.translateM(mTranslationMatrix, 0, enemyArray[i].dx, enemyArray[i].dy, 0);
    				Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mTranslationMatrix , 0);
    				//bulletArray[i].loadGLTexture(gl, this.context);
    				enemyArray[i].draw(mScratch);
        		}
        	}
        }
    	
    	//mSquare.draw(mScratch);
    	
    	

    }
    public void renderDeadEnemy(GL10 gl){
    	

        for( int i = 0; i<MAX_ENEMY; i++){
        	
        	if(dEnemyShip[i] != null){
        		dEnemyShip[i].dx+=(dy);
        		dEnemyShip[i].dy+=(dy*2.0f);
        		Matrix.setIdentityM(mTranslationMatrix, 0);
        		Matrix.translateM(mTranslationMatrix, 0, 0, dEnemyShip[i].dy, 0);
        		Matrix.multiplyMM(mScratch, 0,mMVPMatrix, 0,mTranslationMatrix , 0);
        		//bulletArray[i].loadGLTexture(gl, this.context);
        		dEnemyShip[i].draw(mScratch);
        		if (dEnemyShip[i].dy >= 2.0f)
            		dEnemyShip[i] = null;
        	}
        }
    }
    
    
    public void rendership(GL10 gl){
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
    	
    	playerShip.dx+=(dxShip);
    	playerShip.dy+=(dyShip);
    	if(playerShip.getSouthBound()<=-0.925f){
    		dyShip = 0.0f;
    		playerShip.dy=(dyShip);
    	}
    	if(playerShip.getNorthBound()>=1.0f){
    		
    		dyShip = 1.675f;
    		playerShip.dy=(dyShip);
    	}
    	if(playerShip.getLeftBound()<=-0.7f){
    		
    		dxShip = -.575f;
    		playerShip.dx=(dxShip);
    	}
    	if(playerShip.getRightBound()>=0.7f){
    		
    		dxShip = 0.575f;
    		playerShip.dx=(dxShip);
    	}
    	
    	Matrix.setIdentityM(mTranslationMatrix, 0);
    	Matrix.translateM(mTranslationMatrix,0, playerShip.dx, playerShip.dy, 0);
    	Matrix.multiplyMM(mScratch, 0,mMVPMatrix  , 0,mTranslationMatrix, 0);
    	
    	playerShip.draw(mScratch,mMVPMatrix);
    	if(isFiringBeam){
    		Beam.dx = playerShip.dx;
    		Beam.dy = playerShip.dy;
    		Beam.draw(mScratch);
    	}
    
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


