package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public class Player extends DynamicObject {
	public float radius = 20;
	public float x = 200, y = 200;
	private float mouseFactor;
	private int WINDOW_HEIGHT;
	private ParticleEffect trail;
	public int hits=0;

	public Player(GameWorld world, float mouseFactor) {
		this.world = world;
		texture = new Texture(Gdx.files.internal("sprite/whiteBullet30.png"));
		sprite = new Sprite(texture, 256, 256);
		sprite.setScale((radius / 256f) * 2);
		this.world.gameInstance.vordergrund.add(this);
		this.mouseFactor = mouseFactor;
		WINDOW_HEIGHT = (this.world.gameInstance.WIDTH_CAMERA / 16) * 9;
		startParticleTrail();
	}

	@Override
	public void update(float delta) {
		move(delta);
	}

	void move(float delta) {
		x = Math.round(Gdx.input.getX() * mouseFactor);
		y = Math.round(Gdx.input.getY() * mouseFactor);
		sprite.setPosition(x, y);
		if (trail != null)
			trail.setPosition(x + 128, y + 128);
	}

	public void hit() {
		sprite.setColor(Color.RED);

		world.timeFactor = 0.1f;
		trail.dispose();
		
		Task task = new Task() {
			@Override
			public void run() {
				sprite.setColor(Color.WHITE);
			}
		};
		new Timer().scheduleTask(task, 0.5f);
		
		Task reset = new Task() {
			@Override
			public void run() {
				world.timeFactor = 1f;
				world.resetLevel();
			}
		};
		new Timer().scheduleTask(reset, 1.5f);
		hits++;
	}

	public void debugHit() {
		sprite.setColor(Color.GREEN);

		Task task = new Task() {
			@Override
			public void run() {
				sprite.setColor(Color.WHITE);
			}
		};
		new Timer().scheduleTask(task, 0.2f);
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	@Override
	public void debug() {
		// debug shapes
		float s = radius;
		world.gameInstance.shapeRenderer.setColor(Color.RED);
		world.gameInstance.shapeRenderer.rect(x - s, 1440 - y - s, s, s);
		world.gameInstance.shapeRenderer.rect(x - s, 1440 - y - s, 2 * s,
				2 * s);
		world.gameInstance.shapeRenderer.circle(x, 1440 - y, s);
		// with the bulletradius included
		world.gameInstance.shapeRenderer.setColor(Color.BLUE);
		world.gameInstance.shapeRenderer.circle(x, 1440 - y, s + 20);
	}

	public void startParticleTrail() {
		// emitts the default trail
		trail = new ParticleEffect();
		trail.load(Gdx.files.internal("effects/playerTrail.p"),
				Gdx.files.internal("sprite"));
		world.gameInstance.effects.add(trail);
		trail.setPosition(x + 128, y + 128);
		trail.start();
	}

	@Override
	public void pause(boolean b) {
		paused = b;

	}

	@Override
	public void reverseCheck() {
		System.out.println("no reverseCheck player");
	}
}