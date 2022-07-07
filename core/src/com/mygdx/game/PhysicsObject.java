package com.mygdx.game;

public abstract class PhysicsObject extends DynamicObject {

	public float x,y;
	protected  float spawnTime;
	public abstract void update(float delta);
	protected abstract void move(float delta);
	public abstract void dispose();
	public abstract void debug();
	public abstract boolean collide(float x, float y, float r);
}
