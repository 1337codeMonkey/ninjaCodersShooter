package com.project.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.Context;

public class Parser {
	Context context;
	public ArrayList<Float> v = new ArrayList<Float>();
	public ArrayList<Short> f = new ArrayList<Short>();

	Parser(Context context) {
	    this.context = context;
	    BufferedReader reader = null;
	    String line = null;

	    try { // try to open file
	        reader = new BufferedReader(new InputStreamReader(context
	                .getResources().getAssets().open("tcubetest.obj")));
	    } catch (IOException e) {

	    }

	    try {
	        while ((line = reader.readLine()) != null) {

	            if (line.startsWith("v")) {
	                processVLine(line);
	            } else if (line.startsWith("f")) {
	                processFLine(line);
	            }
	        }
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	}

	private void processVLine(String line) {
	    String[] tokens = line.split("[ ]+"); // split the line at the spaces
	    int c = tokens.length;
	    for (int i = 1; i < c; i++) { // add the vertex to the vertex array
	        v.add(Float.valueOf(tokens[i]));
	    }
	}

	private void processFLine(String line) {
	    String[] tokens = line.split("[ ]+");
	    int c = tokens.length;
	    
	    for (int i = 1; i < c; i++) {
	                Short s = Short.valueOf(tokens[i]);
	                s--;
	                f.add(s);
	    }

	}
}
