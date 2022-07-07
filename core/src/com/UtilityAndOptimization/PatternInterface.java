package com.UtilityAndOptimization;

import com.mygdx.game.GameWorld;

public interface PatternInterface {
	//public void setOwner(EnemyBase owner);
	//public EnemyBase getOwner();
	public void update(float delta);
	public void deactivate();
	public void activate(GameWorld world, float activationDelay);
	public void setSelected(boolean b);
}
