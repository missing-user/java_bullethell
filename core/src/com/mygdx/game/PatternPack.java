package com.mygdx.game;

import java.util.ArrayList;

public class PatternPack {

	public ArrayList<BulletTester> patterns = new ArrayList<BulletTester>();
	public int in = 10;
	private GameWorld world;
	
	public PatternPack() {	System.out.println("successfully created PatternPack");}

	public void activateAll(GameWorld world, float delay) {
		this.world = world;
		for (int i = 0; i < patterns.size(); i++) {
			patterns.get(i).activate(getWorld(), delay);
		}
	}
	
	public void deactivateAll(float delay) {
		for (int i = 0; i < patterns.size(); i++) {
			patterns.get(i).deactivationTimer(delay);
		}
	}
	
	public void deactivateAll() {
		for (int i = 0; i < patterns.size(); i++) {
			patterns.get(i).deactivate();
		}
	}

	public GameWorld getWorld() {
		return world;
	}
}
