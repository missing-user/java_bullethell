package com.mygdx.game;

import com.Levels.Enemy;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class StandardMovingEnemyFinn extends Enemy {

	private float vx;
	private float vy;
	private float radius = 50;
	private float interval = 0.5f, spawnTime, delay = 1, previousTime;

	public StandardMovingEnemyFinn(GameWorld world) {
		this.world = world;
		texture = world.bulletTexture;
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale((radius / 256f) * 2);
		world.gameInstance.vordergrund.add(this);
		spawnTime = world.time;
		previousTime = spawnTime + delay;

		vx = 100;
		vy = 100;
	}

	private void fire() {
		float bvx = world.p.x - x;
		float bvy = world.p.y - y;
		float v = (float) Math.sqrt(bvx * bvx + bvy * bvy);
		bvx /= v;
		bvy /= v;
		bvx *= 500;
		bvy *= 500;
		Bullet b = new Bullet(world);
		b.create(world.bulletTexture, x, y, bvx, bvy);
	}

	@Override
	public void update(float delta) {
		move(delta);
		if (collide(world.p.x, world.p.y, world.p.radius)) {
			world.p.hit();
		}
		float yt = (world.p.y - y) / 20f;
		float xt = (world.p.x - x) / 20f;
		float f = (float) Math.sqrt(yt * yt + xt * xt);
		
		while (world.time - previousTime >= interval) {
			previousTime += interval;
			fire();
		}
		sprite.setColor(Utility.distToColor(f, world.gameColor));
	}

	@Override
	protected void move(float delta) {
		x += vx * delta;
		y += vy * delta;
		sprite.setPosition(x, y);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug() {
		world.gameInstance.shapeRenderer.setColor(Color.GREEN);
		world.gameInstance.shapeRenderer.circle(x, Gdx.graphics.getHeight() - y,
				10);
	}

	@Override
	public boolean collide(float x, float y, float r) {
		// is the bullet outside the screens bounds?
		int thresholdScreen = 100;
		if (this.x < -thresholdScreen
				|| this.x > world.gameInstance.WIDTH_CAMERA + thresholdScreen
				|| this.y < 0 - thresholdScreen
				|| this.y > ((world.gameInstance.WIDTH_CAMERA / 16) * 9)
						+ thresholdScreen) {
			free();
		} else {
			// no, perform bounding box check for collision
			if (Math.abs(this.x - x) <= radius + r) {
				// 2 consecutive if s have better performance than and
				if (Math.abs(this.y - y) <= radius + r) {
					// check for the actual circle
					float distx, disty;
					// squared distance to the player (x and y)
					distx = (this.x - x) * (this.x - x);
					disty = (this.y - y) * (this.y - y);
					float sumRadiusSquared = (radius + r) * (radius + r);
					// faster performance with floats, instead of casted
					// integers
					if (sumRadiusSquared >= distx + disty) {
						free();
						return true;
					}
				}
			}
		}
		return false;
	}

	public void free() {
		//iterators don't like this, use similiar method to bullets
		//world.gameInstance.vordergrund.remove(this);
	}

	@Override
	public void pause(boolean b) {
		// TODO Auto-generated method stub

	}

	@Override
	public void reverseCheck() {
		if (world.time <= spawnTime) {
			free();
		}
		if (previousTime - world.time > interval) {
			if (previousTime - interval > spawnTime + delay)
				previousTime -= interval;
		}
	}

}
