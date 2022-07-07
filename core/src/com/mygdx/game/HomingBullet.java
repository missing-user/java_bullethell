package com.mygdx.game;

import com.UtilityAndOptimization.BulletInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;

public class HomingBullet extends PhysicsObject
		implements
			Poolable,
			BulletInterface {
	public float vx = 0, vy = 0, x = 0, y = 0, acc = 900;
	public boolean missile = true;
	private float v = 0, rotSpeed = 4, triggerRadius = 20;
	private final float radius = 15;
	protected boolean free = true;
	private ParticleEffect trail;
	private float rad;

	public HomingBullet() {
	}

	public HomingBullet(GameWorld world) {
		this.world = world;
		spawnTime = world.time;
	}

	public HomingBullet create(Texture t, float x, float y, float v,
			float deg) {
		texture = t;
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale((radius / 256f) * 2);
		world.gameInstance.bulletRenderListe.add(this);
		// myPool = world.bp;
		this.v = v;
		rad = (float) Math.toRadians(deg);
		this.x = x;
		this.y = y;
		free = false;
		spawnTime = world.time;
		return this;
	}

	@Override
	public boolean isFree() {
		return free;
	}

	public void free() {
		free = true;
		// myPool.free(this);
	}

	@Override
	public void reset() {
		dispose();
		vx = 0;
		vy = 0;
		x = 0;
		y = 0;
		acc = 0;
		v = 0;
		rad = 0;
		rotSpeed = 10;
	}

	@Override
	public void update(float delta) {
		move(delta);
		if (collide(world.p.x, world.p.y, world.p.radius)) {
			world.p.hit();
		}
		reverseCheck();
		float yt = (world.p.y - y) / 20f;
		float xt = (world.p.x - x) / 20f;
		float f = (float) Math.sqrt((yt * yt + xt * xt));
		sprite.setColor(Utility.distToColor(f, world.gameColor));
	}

	@Override
	protected void move(float delta) {
		float angleToPlayer, distanceToPlayer, distx, disty;
		distx = world.p.x - x;
		disty = world.p.y - y;
		if (missile) {
			distanceToPlayer = Utility.sqrt(distx * distx + disty * disty);
			angleToPlayer = Utility.asin(distx / distanceToPlayer);
			float angleDiff = Utility.AngleDifference(rad, angleToPlayer);
			if (angleDiff < rotSpeed * delta) {
				rad = angleToPlayer;
			} else {
				rad += rad < angleToPlayer
						? rotSpeed * delta
						: -rotSpeed * delta;
			}
			// rad=angleToPlayer;
			vx = Utility.sin(rad) * v;
			vy = Utility.cos(rad) * v;

			/*
			 * sobald die y Koordinate des Spielers (p) die des Bullets
			 * überschreitet, müsste der außenwinkel des neu entstandenen
			 * Rechtwinkligen Dreiecks betrachtet werden, um die korrekte
			 * Richtung zu erlangen. stattdessen kompensieren wir hier den
			 * effekt indem wir vy negativ machen
			 */
			if (world.p.y < y) {
				vy = -vy;
			}
		} else {
			// if not missile, stupid bullet model
			vx += distx > 0 ? acc * delta : -acc * delta;
			vx = Math.max(Math.min(v, vx), -v);

			vy += disty > 0 ? acc * delta : -acc * delta;
			vy = Math.max(Math.min(v, vy), -v);
		}
		x += vx * delta;
		y += vy * delta;

		if (trail != null) {
			trail.setPosition(x + 128, y + 128);
		}
		sprite.setPosition(x, y);
	}

	@Override
	public void dispose() {
		if (trail != null) {
			trail.dispose();
		}
	}

	@Override
	public void debug() {
		world.gameInstance.shapeRenderer.setColor(Color.RED);
		world.gameInstance.shapeRenderer.line(x, 1440 - y, world.p.x,
				1440 - world.p.y);
		world.gameInstance.shapeRenderer.line(x, 1440 - y, x + vx * v,
				1440 - (y + vy * v));
	}

	@Override
	public boolean collide(float x, float y, float r) {
		// is the bullet outside the screens bounds?
		int thresholdScreen = 50;
		if (this.x < -thresholdScreen
				|| this.x > world.gameInstance.WIDTH_CAMERA + thresholdScreen
				|| this.y < 0 - thresholdScreen
				|| this.y > ((world.gameInstance.WIDTH_CAMERA / 16) * 9)
						+ thresholdScreen) {
			// yes, put it back into the pool
			free();
		} else {
			float distx, disty, sumRadius;
			distx = Math.abs(this.x - x);
			disty = Math.abs(this.y - y);
			sumRadius = radius + r;
			// no, perform bounding box check for collision
			if (distx <= sumRadius) {
				// 2 consecutive if s have better performance than and
				if (disty <= sumRadius) {
					// check for the actual circle
					float distxsq, distysq;
					// squared distance to the player (x and y)
					distxsq = distx * distx;
					distysq = disty * disty;
					float sumRadiusSquared = sumRadius * sumRadius;
					// faster performance with floats, instead of casted
					// integers
					if (sumRadiusSquared >= distxsq + distysq) {
						explode();
						free();
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void pause(boolean b) {
	}

	private void explode() {
		// spawn explosion
		ParticleEffect p = new ParticleEffect();
		world.gameInstance.effects.add(p);
		p.load(Gdx.files.internal("effects/explosion1.p"),
				Gdx.files.internal("sprite"));
		p.getEmitters().get(0).getTint().setColors(new float[]{
				sprite.getColor().r, sprite.getColor().g, sprite.getColor().b});
		p.scaleEffect(radius / 7);
		p.setPosition(x + 128, y + 128);
		p.getEmitters().get(0).getWind().setHigh(vx);
		p.getEmitters().get(0).getGravity().setHigh(vy);
		// p.getEmitters().get(0).getGravity().setActive(true);
		// p.getEmitters().get(0).getWind().setActive(true);
		p.start();
	}

	@Override
	public void reverseCheck() {
		if(world.time<= spawnTime)
		{
			free();
		}
	}
}
