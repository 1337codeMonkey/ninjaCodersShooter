package com.project.game;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

class Square {

    private final String vertexShaderCode =
        // This matrix member variable provides a hook to manipulate
        // the coordinates of the objects that use this vertex shader
        "uniform mat4 uMVPMatrix;" +
        "attribute vec2 a_TextureCoordinates;" +
        "attribute vec4 vPosition;" +
        "varying vec2 v_TextureCoordinates;"+
        "void main() {" +
        // the matrix must be included as a modifier of gl_Position
        "  v_TextureCoordinates = a_TextureCoordinates;" +
        "  gl_Position = uMVPMatrix * vPosition;" +
        "}";

    private final String fragmentShaderCode =
        "precision mediump float;" +
        "uniform sampler2D u_TextureUnit;"+
        "varying vec2 v_TextureCoordinates;"+
        "void main() {" +
        "  gl_FragColor = texture2D(u_TextureUnit, v_TextureCoordinates);" +
        "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private FloatBuffer textureBuffer;  // buffer holding the texture coordinates
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int[] textures = new int[1];
    private int mMVPMatrixHandle;
    private int mTextureUniformHandle;
    private int mTextureCoordinateHandle;
    public float dy =0;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static final int COORDS_PER_TEXTURE = 2;
    float textureCoords[];
    float squareCoords[];
    

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };

    public Square(float cx,float cy, float width, float height) {
    	 float squareCoord[] = { cx-(0.5f*width), cy+(0.5f*height), 0.0f,   // top left
    			 				 cx-(0.5f*width), cy-(0.5f*height), 0.0f,   // bottom left
    			 				 cx+(0.5f*width), cy-(0.5f*height), 0.0f,   // bottom right
    			 				 cx+(0.5f*width), cy+(0.5f*height), 0.0f }; // top right
    	 float textureCoords[] = { 0.0f, 0.0f,
                 0.0f, 1.0f,
                 1.0f, 1.0f,
                 1.0f, 0.0f};
    	 this.textureCoords = textureCoords;
    	 
    	 this.squareCoords = squareCoord;
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
        
        bb= ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        textureBuffer = bb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);


        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

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
    
    public void loadGLTexture(GL10 gl, Context context, int resouceID) {
    	final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;   // No pre-scaling
    	// loading texture
    	
    	Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
    			resouceID,options);
    	
    	// generate one texture pointer
    	gl.glGenTextures(1, textures, 0);
        // ...and bind it to our array
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // create nearest filtered texture
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
    	gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
    	
    	
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
        

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgram, "u_Texture");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgram, "a_TexCoordinate");
     
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // bind the previously generated texture
        GLES20.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);

       
        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        glRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        glRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the square
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length,
                              GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTextureCoordinateHandle);
    }
}
