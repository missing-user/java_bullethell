package com.UtilityAndOptimization;

public interface BulletInterface {
	public boolean isFree();
	public void free();
	public boolean collide(float x, float y, float r);
}
