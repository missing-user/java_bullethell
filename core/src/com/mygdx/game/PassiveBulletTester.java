package com.mygdx.game;

import com.UtilityAndOptimization.PatternInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class PassiveBulletTester extends DynamicObject
		implements
			PatternInterface {

	private Timer t;
	private Color col = Color.BROWN;
	private boolean selected = true;
	public float x = 1100;
	public float y = 500;
	private float vx;
	private float vy;
	private boolean relative = false;
	private boolean trail = false;
	private float randomness = 0;
	private float bulletsPerCone = 45;
	private int cones = 4;
	public float sprayDegrees = 45;
	public float totalSpray = 360;
	private float dir = 0;
	private float radius = 200;
	private boolean active = false;
	private Array<PassiveBullet> allBullets = new Array<PassiveBullet>();
	public float rotationRate = 20;
	public int minRadius = 200;
	public float maxRadius = 600;
	private float spawnTime;
	private boolean smoothRadiusChange;
	private float radiusChangeRate=1;

	public PassiveBulletTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void deactivate() {
		active = false;
	}

	@Override
	public void activate(GameWorld world, float activationDelay) {
		this.world = world;
		texture = world.bulletTexture;
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale(0.3f);
		spawnTime = world.time;

		if (activationDelay > 0) {
			t.scheduleTask(new Task() {
				@Override
				public void run() {
					addToWorld();
					active = true;
					fire();
				}
			}, activationDelay);
		} else {
			addToWorld();
			active = true;
			fire();
		}
	}

	@Override
	public void update(float delta) {
		col = world.gameColor;
		if (!selected) {
			col = Color.DARK_GRAY;
		}
		sprite.setColor(col);
		if (active && !paused)
			move(delta);
		sprite.setPosition(x, y);
	}

	private void move(float delta) {
		for (PassiveBullet b : allBullets) {
			for (int c = 0; c < cones; c++) {
				// create the cones
				for (int i = 0; i < bulletsPerCone; i++) {
					// fill the individual cones (sprayDegrees)
					float tmpDeg = 0;
					float bulletx = relative ? 0 : x,
							bullety = relative ? 0 : y;
					tmpDeg = ((sprayDegrees / bulletsPerCone) * (i + 1f))
							- sprayDegrees / 2f;
					tmpDeg += dir;
					tmpDeg += (totalSpray / cones) * (c + 1);

					bulletx += Utility.sin((float) Math.toRadians(tmpDeg))
							* radius;
					bullety += Utility.cos((float) Math.toRadians(tmpDeg))
							* radius;
					allBullets.get((int) (c * bulletsPerCone + i)).x = bulletx;
					allBullets.get((int) (c * bulletsPerCone + i)).y = bullety;
				}
			}
		}

		x += vx * delta;
		y += vy * delta;
		dir += rotationRate * delta;
		if (smoothRadiusChange)
			radius = ((Utility.sineWave(world.time - spawnTime, radiusChangeRate, 1) + 1)
					/ 2) * (maxRadius - minRadius) + minRadius;
		else
			radius = ((Utility.triangleWave(world.time - spawnTime, radiusChangeRate, 1)
					+ 1) / 2) * (maxRadius - minRadius) + minRadius;

		if (x < 0 || x > world.gameInstance.WIDTH_CAMERA || y < 0
				|| y > (world.gameInstance.WIDTH_CAMERA / 16) * 9) {
			x = 1100;
			y = 500;
		}
	}

	private void fire() {

		for (int c = 0; c < cones; c++) {
			// create the cones
			for (int i = 0; i < bulletsPerCone; i++) {
				// fill the individual cones (sprayDegrees)
				float tmpDeg = 0;
				float bulletx = relative ? 0 : x, bullety = relative ? 0 : y;
				tmpDeg = ((sprayDegrees / bulletsPerCone) * (i + 1f))
						- sprayDegrees / 2f;
				tmpDeg += dir;
				tmpDeg += (totalSpray / cones) * (c + 1);

				bulletx += Utility.sin((float) Math.toRadians(tmpDeg)) * radius;
				bullety += Utility.cos((float) Math.toRadians(tmpDeg)) * radius;

				if (randomness > 0) {
					tmpDeg += (Math.random() - 0.5) * randomness / 5;
				}
				PassiveBullet b = new PassiveBullet();
				b.setWorld(world);
				b.create(world.bulletTexture, bulletx, bullety, vx, vy);
				b.sprite.setColor(col);

				if (relative) {
					b.referenceBT = this;
				}

				if (randomness > 0) {
					b.offsetX = (float) ((Math.random() - 0.5) * randomness
							/ 8);
					b.offsetY = (float) ((Math.random() - 0.5) * randomness
							/ 8);
				}

				if (trail) {
					b.startParticleTrail();
				}
				allBullets.add(b);
			}
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause(boolean b) {
		paused = b;
	}

	@Override
	public void setSelected(boolean b) {
		selected = b;
	}

	private void addToWorld() {
		world.gameInstance.hintergrund.add(this);
	}

	@Override
	public void reverseCheck() {
		System.out.println("no reverseCheck passive bullet");
	}

}
