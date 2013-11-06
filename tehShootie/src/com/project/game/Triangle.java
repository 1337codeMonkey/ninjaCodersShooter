package com.project.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

class Triangle {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
        "attribute vec2 a_TextureCoordinates;" +
        "varying vec2 v_TextureCoordinates;"+
        "attribute vec4 vPosition;" +
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  gl_Position = uMVPMatrix * vPosition;" +
        "  v_TextureCoordinates = a_TextureCoordinates;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform vec4 vColor;" +
        "uniform sampler2D u_TextureUnit;"+
        "varying vec2 v_TextureCoordinates;"+
        "void main() {" +
        "  gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
        "}";

    private final FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    static final int COORDS_PER_TEXTURE = 2;
    float textureCoords[];
    private int[] textures = new int[1];
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = { // in counterclockwise order:
         0.0f,  -0.7f, 0.0f,   // top
        -0.1f, -0.9f, 0.0f,   // bottom left
         0.1f, -0.9f, 0.0f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);
        
        float textureCoords[] = { 0.0f, 0.0f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                };
   	 	this.textureCoords = textureCoords;
        
        bb= ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        textureBuffer = bb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);


        // prepare shaders and OpenGL program
        int vertexShader = glRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                                                   vertexShaderCode);
        int fragmentShader = glRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                                                     fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }
    
    
    public void setTopVertexX(float dx){
    	triangleCoords[0]= 0.0f+dx;
    	
    }
    
    public void setTopVertexY(float dy){
    	triangleCoords[1]= -0.7f+dy;
    	
    }
    
    public float getTopVertexY()
    {
    	return triangleCoords[1];
    }
    
    public float getTopVertexX()
    {
    	return triangleCoords[0];
    }
    
    public void loadGLTexture(GL10 gl, Context context) {
    	final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
    	// loading texture
    	
    	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
    			R.drawable.airplane,options);
    	
    	// generate one texture pointer
    	gl.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // create nearest filtered texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    	
    	//Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
    	
    	//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
    	
    	//gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

    	 //Use Android GLUtils to specify a two-dimensional texture image from our bitmap
    
    	 GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
    
    	 // Clean up
    	
    	 bitmap.recycle();
    	
    }

    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mTextureCoordinateHandle);
        GLES20.glVertexAttribPointer(mTextureCoordinateHandle, COORDS_PER_TEXTURE,
                                     GLES20.GL_FLOAT, false,
                                     0, textureBuffer);
     // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
     
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // bind the previously generated texture
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

        

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        glRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        glRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}
