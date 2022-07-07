package com.mygdx.game;

import com.UtilityAndOptimization.BulletInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Pool.Poolable;

public class PassiveBullet extends PhysicsObject
		implements
			Poolable,
			BulletInterface {

	public float x = 0, offsetX=0, y = 0, offsetY=0;
	private float lastx = 0, lasty = 0;
	private final float radius = 15;
	// private PassiveBulletPool myPool;
	protected boolean free = true;
	private ParticleEffect trail;
	public PassiveBulletTester referenceBT;

	public PassiveBullet() {
	}
	public PassiveBullet(GameWorld world) {
		this.world = world;
	}

	public PassiveBullet create(Texture t, float x, float y, float v,
			float deg) {
		texture = t;
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale((radius / 256f) * 2);
		world.gameInstance.bulletRenderListe.add(this);
		// myPool = world.bp;
		this.x = x;
		this.y = y;
		lastx = x;
		lasty = y;
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
		x = 0;
		y = 0;
		offsetX=0;
				offsetY=0;
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
		lastx = x;
		lasty = y;
		if (referenceBT != null) {
			x += referenceBT.x;
			y += referenceBT.y;
		}
		sprite.setPosition(x+offsetX, y+offsetY);
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
		world.gameInstance.shapeRenderer.circle(x, 1440 - y, radius);
	}

	@Override
	public boolean collide(float x, float y, float r) {
		// is the bullet outside the screens bounds?
		int thresholdScreen = 400;
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
		p.load(Gdx.files.internal("effects/explosion3.p"),
				Gdx.files.internal("sprite"));
		p.getEmitters().get(0).getTint().setColors(new float[]{
				sprite.getColor().r, sprite.getColor().g, sprite.getColor().b});
		p.scaleEffect(radius / 7);
		p.setPosition(x + 128, y + 128);
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
	public void reverseCheck() {
		if(world.time<= spawnTime)
		{
			free();
		}
	}
}