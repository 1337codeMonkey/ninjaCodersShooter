package com.project.game;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.project.game.FirstActivity;
import com.swarmconnect.Swarm;
import com.swarmconnect.SwarmActivity;

public class MenuActivity extends SwarmActivity {

	private AndroidAudio audio;
    private AndroidMusic music;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                this.requestWindowFeature(Window.FEATURE_NO_TITLE);
                this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_main);
                audio = new AndroidAudio(this);
                music = (AndroidMusic) audio.newMusic("pyGybigare.ogg");
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
                getMenuInflater().inflate(R.menu.main, menu);
                return true;
        }

        /** Called when the user clicks the arcade button */
        public void startArcade(View view) {
                Intent intent = new Intent(this, FirstActivity.class);
                startActivity(intent);
        }
        
        /** Called when the user clicks the score button */
        public void getLeaderboard(View view) {
                Swarm.showLeaderboards();
        }
        
        /** Called when the user clicks the social button */
        public void login(View view) {
        	
        	if(!Swarm.isInitialized()){
                // Allow guest accounts
                // Call before calling Swarm.init(...)
                Swarm.setAllowGuests(true);
                Swarm.init(this, 8468, "9cb6a991a386fb6a46959558ed062519");
        	}
        	else
                Swarm.showDashboard();
        }
        
        @Override
        protected void onStart() {
        	super.onStart();
        	music.play();
        	music.setLooping(true);
        }
        
        @Override
        protected void onPause() {
            super.onPause();
            music.pause();
        }
        @Override
        protected void onResume() {
            super.onResume();
            music.play();
        }
        
        @Override
        protected void onStop() {
        	super.onStop();
        	music.stop();
        }
}