package com.example.tehshootie;

public abstract class Screen {

    public abstract void update(float deltaTime);

    public abstract void paint(float deltaTime);

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
    
    public abstract void backButton();
}
