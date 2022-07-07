package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public abstract class DynamicObject extends StaticObject {

	protected Texture texture;
	protected Sprite sprite;
	protected boolean paused = false;
	public abstract void update(float delta);
	public abstract void dispose();
	public abstract void debug();
	abstract public void pause(boolean b);
	public abstract void reverseCheck();
}
