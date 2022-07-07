package com.mygdx.game;

import com.UtilityAndOptimization.BulletInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Bullet extends PhysicsObject implements Poolable, BulletInterface {
	public float vx = 0, vy = 0, x = -1, y = -1, acc = 0;
	private float initialvx = 0, initialvy = 0;
	public boolean destroyOnReturn = false;
	private final float radius = 15;
	public boolean free = true;
	private ParticleEffect trail;

	// circular motion vars
	public float rotationRate = 0, startDir = 0, vOut = 0, pathRadius = 0;
	private float time;
	private float lastx = 0, lasty = 0;
	public boolean circular = false, keepCenter = true;
	private float rotation = 0;
	private float circleCenterx = 0, circleCentery = 0, vOutInitial = 0;
	public BulletTester referenceBT;
	public boolean selected;

	public Bullet(GameWorld world) {
		this.world = world;
	}

	public DynamicObject create(GameWorld w) {
		// dont use this in production, just debug purposes
		world = w;
		texture = new Texture(Gdx.files.internal("sprite/whiteBullet30.png"));
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale((radius / 256f) * 2);
		free = false;
		world.gameInstance.bulletRenderListe.add(this);
		spawnTime = world.time;
		return this;
	}

	public Bullet create(Texture t, float x, float y, float vx, float vy) {
		texture = t;
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale((radius / 256f) * 2);
		world.gameInstance.bulletRenderListe.add(this);
		this.vx = vx;
		this.vy = vy;
		initialvx = vx;
		initialvy = vy;
		this.x = x;
		this.y = y;
		lastx = x;
		lasty = y;
		circleCenterx = x;
		circleCentery = y;
		free = false;
		spawnTime = world.time;
		sprite.setPosition(x, y);
		return this;
	}

	@Override
	public void update(float delta) {
		move(delta);
		if (collide(world.p.x, world.p.y, world.p.radius)) {
			world.p.hit();
			explode();
		}
		float yt = (world.p.y - y) / 20f;
		float xt = (world.p.x - x) / 20f;
		float f = (float) Math.sqrt(yt * yt + xt * xt);
		if (!selected)
			sprite.setColor(Utility.distToColor(f, world.gameColor));
		//else sprite.setColor(world.gameColor);
	}

	@Override
	protected void move(float delta) {
		x = lastx + vx * delta;
		lastx = x;
		y = lasty + vy * delta;
		lasty = y;
		if (keepCenter && circular) {
			x = circleCenterx;
			y = circleCentery;
		}

		if (referenceBT != null) {
			x += referenceBT.x;
			y += referenceBT.y;
		}

		// only execute complex calculations if necessary
		if (circular) {
			// increase the time for sin and cos
			time += delta;
			y += pathRadius * Utility
					.sin(Utility.toRadians(rotationRate * time + startDir));
			x += pathRadius * Utility
					.cos(Utility.toRadians(rotationRate * time + startDir));
			sprite.setRotation(rotationRate * time + startDir);
			// increase the path radius to get spiraling effect
			pathRadius += vOut * delta;
			vOut += (acc * vOutInitial * delta) / 10;
		} else if (acc != 0) {
			// check if the direction has changed
			if (initialvx > 0 && vx < 0 || initialvx < 0 && vx > 0) {
				sprite.setRotation(rotation);
			} else {
				sprite.setRotation(rotation - 180);
			}
			// change the linear velocities, multiplied by initial velocity to
			// get
			// the correct direction vector
			vy += (initialvy * acc * delta) / 10;
			vx += (initialvx * acc * delta) / 10;
		}

		if (destroyOnReturn) {
			if (!circular) {
				if (Math.abs(vx) > Math.abs(initialvx))
					if (Math.abs(vy) > Math.abs(initialvy)) {
						free();
					}
			} else {
				if (Math.abs(vOut) > Math.abs(vOutInitial)) {
					free();
				}
			}
		}

		if (trail != null) {
			trail.setPosition(x + 128, y + 128);
		}
		sprite.setPosition(x, y);
	}

	@Override
	public void reset() {
		dispose();
		destroyOnReturn = false;
		vx = 0;
		vy = 0;
		x = -1;
		y = -1;
		acc = 0;
		initialvx = 0;
		initialvy = 0;
		rotationRate = 0;
		startDir = 0;
		vOut = 0;
		vOutInitial = 0;
		time = 0;
		pathRadius = 0;
		circular = false;
		circleCenterx = 0;
		circleCentery = 0;
		referenceBT = null;
		keepCenter = true;
		free = true;
		trail = null;
		lastx = -1;
		lasty = -1;
		rotation = 0;
	}

	@Override
	public boolean collide(float x, float y, float r) {
		if (this.x != -1 && this.y != -1) {
			// is the bullet outside the screens bounds?
			int thresholdScreen = 400;
			if (this.x < -thresholdScreen
					|| this.x > world.gameInstance.WIDTH_CAMERA
							+ thresholdScreen
					|| this.y < 0 - thresholdScreen
					|| this.y > ((world.gameInstance.WIDTH_CAMERA / 16) * 9)
							+ thresholdScreen) {
				// yes, put it back into the pool
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
		}
		return false;
	}

	private void explode() {
		// spawn explosion
		ParticleEffect p = new ParticleEffect();
		world.gameInstance.effects.add(p);
		p.load(Gdx.files.internal("effects/explosion2.p"),
				Gdx.files.internal("sprite"));
		p.getEmitters().get(0).getTint().setColors(new float[]{
				sprite.getColor().r, sprite.getColor().g, sprite.getColor().b});
		p.scaleEffect(radius / 70);
		p.setPosition(x + 128, y + 128);
		p.getEmitters().get(0).getWind().setHigh(vx);
		p.getEmitters().get(0).getGravity().setHigh(vy);
		// p.getEmitters().get(0).getGravity().setActive(true);
		// p.getEmitters().get(0).getWind().setActive(true);
		p.start();
	}

	public void startParticleTrail(ParticleEffect p) {
		// loading the particle effects from a pool has no significant
		// performance benefit
		trail = p;
		world.gameInstance.effects.add(trail);
		trail.getEmitters().get(0).getTint().setColors(new float[]{
				sprite.getColor().r, sprite.getColor().g, sprite.getColor().b});
		trail.setPosition(x + 128, y + 128);
		trail.start();
	}

	public void startParticleTrail() {
		// emitts the default trail
		trail = new ParticleEffect();
		trail.load(Gdx.files.internal("effects/trail1.p"),
				Gdx.files.internal("sprite"));
		world.gameInstance.effects.add(trail);
		trail.getEmitters().get(0).getTint().setColors(new float[]{
				sprite.getColor().r, sprite.getColor().g, sprite.getColor().b});
		trail.setPosition(x + 128, y + 128);
		trail.start();
	}

	@Override
	public void dispose() {
		if (trail != null) {
			trail.dispose();
		}
	}

	public void setVout(float f) {
		vOut = f;
		vOutInitial = vOut;
		if (vOutInitial == 0)
			vOutInitial = 100;
	}

	public void setDegrees(float tmpDeg) {
		rotation = tmpDeg;
		sprite.setRotation(rotation - 180);
	}

	@Override
	public void debug() {
		world.gameInstance.shapeRenderer.setColor(Color.GRAY);

		if (referenceBT != null) {
			circleCenterx = referenceBT.x;
			circleCentery = referenceBT.y;
			lastx = referenceBT.x;
			lasty = referenceBT.y;
		}
		// circular motion around center point
		if (keepCenter && circular) {
			world.gameInstance.shapeRenderer.line(circleCenterx,
					Gdx.graphics.getHeight() - circleCentery, x,
					Gdx.graphics.getHeight() - y);
			world.gameInstance.shapeRenderer.circle(circleCenterx,
					Gdx.graphics.getHeight() - circleCentery,
					Math.abs(pathRadius));
		} else if (circular) {
			world.gameInstance.shapeRenderer.line(lastx,
					Gdx.graphics.getHeight() - lasty, x,
					Gdx.graphics.getHeight() - y);
			world.gameInstance.shapeRenderer.circle(lastx,
					Gdx.graphics.getHeight() - lasty, Math.abs(pathRadius));
		} else {
			// velocity linear, where will the bullet be in 1s
			world.gameInstance.shapeRenderer.line(lastx,
					Gdx.graphics.getHeight() - lasty, lastx + 1 * vx,
					Gdx.graphics.getHeight() - (lasty + 1 * vy));
		}

		world.gameInstance.shapeRenderer.setColor(Color.BLUE);
		world.gameInstance.shapeRenderer.circle(x, Gdx.graphics.getHeight() - y,
				radius);
		world.gameInstance.shapeRenderer.point(x, Gdx.graphics.getHeight() - y,
				0);

		world.gameInstance.shapeRenderer.setColor(Color.GREEN);
		// world.gameInstance.shapeRenderer.line(x, Gdx.graphics.getHeight() -
		// y, world.p.x,Gdx.graphics.getHeight() - world.p.y);
		float yt = (world.p.y - y) / 20f;
		float xt = (world.p.x - x) / 20f;
		float f = (float) Math.sqrt((yt * yt + xt * xt));
		world.gameInstance.shapeRenderer.circle(x, Gdx.graphics.getHeight() - y,
				f);

		if (referenceBT != null) {
			circleCenterx = 0;
			circleCentery = 0;
			lastx = 0;
			lasty = 0;
		}
	}

	@Override
	public void pause(boolean b) {
		paused = b;
	}

	@Override
	public boolean isFree() {
		return free;
	}

	public void free() {
		free = true;
	}

	@Override
	public void reverseCheck() {
		if(world.time<= spawnTime)
		{
			free();
		}
	}
}
