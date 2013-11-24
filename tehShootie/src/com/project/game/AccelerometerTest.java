package com.project.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class AccelerometerTest extends Activity implements SensorEventListener{
	TextView textView;
	StringBuilder builder = new StringBuilder();
	float[] linearAcceleration = {0.0f, 0.0f};//, 0.0f};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		textView = new TextView(this); 
		setContentView(textView);
		SensorManager manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); 
		if (manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() == 0) {
			textView.setText("No accelerometer installed"); 
		} else {
			Sensor accelerometer = manager.getSensorList( Sensor.TYPE_ACCELEROMETER).get(0);
			if (!manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)) {
	              textView.setText("Couldn't register sensor listener");
	          }
		} 
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
	    for(int i = 0; i < 2; i++){
	    	linearAcceleration[i] = input[i] - gravity[i];
	    	if(Math.abs(linearAcceleration[i]) < 1.0f)
		    	linearAcceleration[i] = 1.0f;
	    }

		
		builder.setLength(0);
		builder.append("x: "); 
		builder.append(linearAcceleration[0]); 
		builder.append("\ny: ");
		builder.append(linearAcceleration[1]);
	    //builder.append("\nz: ");
	    //builder.append(linearAcceleration[2]);
	    textView.setText(builder.toString());
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
	
}
