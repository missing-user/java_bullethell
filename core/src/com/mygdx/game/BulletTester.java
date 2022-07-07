package com.mygdx.game;

import javax.xml.bind.annotation.XmlTransient;

import com.UtilityAndOptimization.PatternInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class BulletTester extends DynamicObject implements PatternInterface {

	// circular tester
	public boolean circular = false;
	public float vOut = 200, initialPathRadius = 100, rotationFactor = 1;
	public float maxRotationRate = 50, rotationChangeDuration = 0,
			rotationRate = 50;
	public float minSpray = 10, sprayChangeDuration = 20, maxSpray = 50,
			sprayChangeRate;
	public float interval = (float) 0.05;
	public int loopTime = 1, breakTime = 2;
	public float dir = 0, sprayDegrees = 15, totalSpray = 360, speed = 400,
			acc = -4;
	public int cones = 7, bulletsPerCone = 2;
	public float vx = 0, vy = 0;
	public boolean destroy = true;
	public boolean trail = false;
	public boolean keepCenter = false;
	public float x = 1100, y = 500;
	public boolean relative = false;
	public float randomness = 0;
	public boolean rotChange = false, sprayChange = false;
	public boolean smoothRot = false, smoothSpray = false;
	private Timer t, td;
	private Task task;
	private String name = "current pattern";

	// private EnemyBase owner;
	private boolean active = true;
	private Color col = Color.DARK_GRAY;
	private int shootLoopCounter = 0;
	private boolean reactivation = false;
	@XmlTransient
	private boolean selected = false;
	private float spawnTime;

	public BulletTester() {

	}

	@Override
	public void update(float delta) {

		col = world.gameColor;
		if (!selected) {
			col = Color.DARK_GRAY;
		}
		sprite.setColor(col);
		move(delta);
		sprite.setPosition(x, y);
		if (circular) {
			sprite.setRotation(dir);
		} else {
			sprite.setRotation(-dir);
		}
	}

	private void move(float delta) {
		if (rotChange) {
			float f = Utility.triangleWave(world.time,
					rotationChangeDuration / 20, 1) * maxRotationRate;
			if (smoothRot) {
				rotationRate = f;
				dir += rotationRate * delta;
			} else {
				dir = f;
			}
		} else {
			dir += maxRotationRate * delta;
		}

		if (sprayChange) {

			if (smoothSpray) {
				sprayDegrees = minSpray + (maxSpray - minSpray)
						* (Utility.sineWave(world.time - spawnTime,
								sprayChangeDuration / 20, 1) + 1)
						/ 2;
			} else {
				sprayDegrees = minSpray + (maxSpray - minSpray)
						* (Utility.triangleWave(world.time,
								sprayChangeDuration / 20, 1) + 1)
						/ 2;
			}
		}

		x += vx * delta;
		y += vy * delta;
		if (x < 0 || x > world.gameInstance.WIDTH_CAMERA || y < 0
				|| y > (world.gameInstance.WIDTH_CAMERA / 16) * 9) {
			x = 1100;
			y = 500;
		}
	}

	private void fire() {
		shootLoopCounter++;
		shootLoopCounter = shootLoopCounter % (loopTime + breakTime);

		if (shootLoopCounter < loopTime) {
			for (int c = 0; c < cones; c++) {
				// create the cones
				for (int i = 0; i < bulletsPerCone; i++) {
					// fill the individual cones (sprayDegrees)
					float vx = 0, vy = 0, tmpDeg = 0, vOutB = vOut;
					float bulletx = relative ? 0 : x,
							bullety = relative ? 0 : y;
					tmpDeg = ((sprayDegrees / bulletsPerCone) * (i + 1f))
							- sprayDegrees / 2f;
					tmpDeg += dir;
					tmpDeg += (totalSpray / cones) * (c + 1);

					if (randomness > 0) {
						bulletx += (Math.random() - 0.5) * randomness / 8;
						bullety += (Math.random() - 0.5) * randomness / 8;
						tmpDeg += (Math.random() - 0.5) * randomness / 5;
					}

					vx += (float) Math.sin(Math.toRadians(tmpDeg)) * speed;
					vy += (float) Math.cos(Math.toRadians(tmpDeg)) * speed;
					if (randomness > 0) {
						float multiplicator = (float) (1
								+ ((Math.random() - 0.5) * randomness / 400));
						vx = vx * multiplicator;
						vy = vy * multiplicator;
						vOutB *= multiplicator;
					}

					Bullet b = new Bullet(world);
					b.create(texture, bulletx, bullety, vx, vy);
					b.sprite.setColor(col);
					b.selected = selected;
					b.acc = acc;
					if (acc < 0)
						b.destroyOnReturn = destroy;
					b.setDegrees(-tmpDeg - 90);

					if (relative) {
						b.referenceBT = this;
					}

					if (circular) {
						b.setVout(vOutB);
						b.rotationRate = rotationRate * rotationFactor;
						b.pathRadius = initialPathRadius;
						b.circular = true;
						b.startDir = tmpDeg;
						b.keepCenter = keepCenter;
					}

					if (trail) {
						b.startParticleTrail();
					}
				}
			}
		}
	}

	// timer for delayed activation !!!only used in pattern editor
	public void setTimer(float delay) {
		t.clear();
		t.scheduleTask(task, delay, interval);
	}
	// timer for instant activation
	public void setTimer() {
		t.clear();
		t.scheduleTask(task, 0, interval);
	}

	public void deactivate() {
		active = false;
		world.gameInstance.vordergrund.remove(this);
		if (td != null)
			td.clear();
		t.clear();
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	public void activate(GameWorld world, float activationDelay) {
		this.world = world;
		texture = world.bulletTexture;
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale(0.3f);
		active = true;
		
		spawnTime = world.time;
		if (!reactivation) {
			// initializing the fire event task
			task = new Task() {
				@Override
				public void run() {
					if (active)
						fire();
				}
			};
			t = new Timer();
		}
		// add to world and render only when activated, delay activation
		if (activationDelay > 0) {
			t.scheduleTask(new Task() {
				@Override
				public void run() {
					addToWorld();
					setTimer();
				}
			}, activationDelay);
		} else {
			addToWorld();
			setTimer();
		}
		reactivation = true;
	}

	private void addToWorld() {
		world.gameInstance.vordergrund.add(this);
	}

	public void deactivationTimer(float lifetime) {
		td = new Timer();
		td.scheduleTask(new Task() {
			@Override
			public void run() {
				deactivate();
			}
		}, lifetime);
	}

	@Override
	public void debug() {
		world.gameInstance.shapeRenderer.setColor(Color.BLUE);
		world.gameInstance.shapeRenderer.circle(x, Gdx.graphics.getHeight() - y,
				80);
	}

	@Override
	public void pause(boolean b) {
		paused = b;
		if (b) {
			t.stop();
			if (td != null)
				td.stop();
		} else {
			t.start();
			if (td != null)
				td.start();
		}
	}

	public void setRotationRate(float f) {
		rotationRate = f;
	}

	public void setName(String s) {
		name = s;
	}

	public String getName() {
		return name;
	}

	public void setSelected(boolean b) {
		selected = b;
	}

	@Override
	public void reverseCheck() {
		System.out.println("no reverseCheck bullet tester");
	}
}