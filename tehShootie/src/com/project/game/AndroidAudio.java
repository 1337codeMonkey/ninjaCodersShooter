package com.project.game;

import java.io.IOException;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;

public class AndroidAudio implements Audio {
	AssetManager assets; //loads S.E.s from file
	SoundPool soundPool; //soundpool lives as long as the activity
	
	public AndroidAudio(Activity activity) { 
		//passing the games activity gives us media stream vol controls
		activity.setVolumeControlStream(AudioManager.STREAM_MUSIC); 
		this.assets = activity.getAssets();
		
		//can play up to 20 sounds simultaneously
		this.soundPool=new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
	}
	
	@Override
	public Music newMusic(String filename) {
		try {
			//create an internal MediaPlayer in an instance of this class
			AssetFileDescriptor assetDescriptor=assets.openFd(filename);
			return new AndroidMusic(assetDescriptor); 
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load music '"+filename+"'"); 
		}
	}

	@Override
	public Sound newSound(String filename) {
		try {
			//load a S.E. into the SoundPool
			AssetFileDescriptor assetDescriptor=assets.openFd(filename); 
			int soundId=soundPool.load(assetDescriptor, 0);
			return new AndroidSound(soundPool, soundId);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load sound '"+filename+"'");
		} 
	}

}
