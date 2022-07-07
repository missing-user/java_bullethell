package com.Levels;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.Bullet;
import com.mygdx.game.GameWorld;
import com.mygdx.game.PatternPack;

public class StaticBulletPattern {

	private GameWorld world;
	private Texture texture;
	public ArrayList<Bullet> myBullets = new ArrayList<Bullet>();

	public StaticBulletPattern(GameWorld world) {
		this.world = world;
		texture = world.bulletTexture;
		
		for (int i = 0; i < 200; i++) {
			for (int j = 0;j < 200; j++) {
				makeBullet(i*5+300,j*5+300);
			}
		}
	}
	
	public void destroy()
	{
		for(Bullet b:myBullets) {
			b.free();
		}
		world=null;
	}
	
	public void makeBullet(int x, int y) {
		Bullet b = new Bullet(world);
		b.create(texture, x, y, 0,0);
		myBullets.add(b);
	}

}
