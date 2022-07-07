 package com.Levels;

import java.util.ArrayList;

import com.Editors.EnemyEditorUI;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.mygdx.game.GameWorld;
import com.mygdx.game.PatternPack;
import com.mygdx.game.DynamicObject;

public class BossBase extends DynamicObject {

	public ArrayList<PatternPack> patternPacks = new ArrayList<PatternPack>();
	public ArrayList<Float> timings = new ArrayList<Float>();
	public ArrayList<Float> timingsDecativation = new ArrayList<Float>();
	public float x, y, vx, vy;
	private GameWorld world;
	private boolean active = false;
	private Timer timer;
	private float time;
	private EnemyEditorUI enemyEditorUI;

	public boolean isActive() {
		return active;
	}

	public BossBase() {
		System.out.println("enemyBase constructor called");
	}

	public void activateAll(GameWorld world) {
		this.world = world;
		System.out.println("enemy activated");
		for (int i = 0; i < patternPacks.size(); i++) {
			patternPacks.get(i).activateAll(world, timings.get(i));
			patternPacks.get(i).deactivateAll(timingsDecativation.get(i));
		}
		active = true;
		time = 0;
		timer.start();
		world.mp3Music.play();
	}

	public void activateIndex(int index) {
		System.out.println("enemy activated");
		float difference = timings.get(index);
		for (int i = index; i < patternPacks.size(); i++) {
			patternPacks.get(i).activateAll(world, timings.get(i) - difference);
			patternPacks.get(i)
					.deactivateAll(timingsDecativation.get(i) - difference);
			System.out.println(timings.get(i) - timingsDecativation.get(i));
		}
		active = true;
		time = timings.get(index);
		timer.start();
		world.mp3Music.play();
		world.mp3Music.setPosition(time);
	}

	public void deactivateAll() {
		System.out.println("enemy deactivated");
		for (int i = 0; i < patternPacks.size(); i++) {
			patternPacks.get(i).deactivateAll();;
		}
		active = false;
		timer.stop();
		world.mp3Music.stop();
	}

	@Override
	public void update(float delta) {
		move(delta);
	}

	public void move(float delta) {
		x += vx * delta;
		y += vy * delta;
		sprite.setPosition(x, y);
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	@Override
	public void debug() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause(boolean b) {
		// TODO Auto-generated method stub

	}

	public void editor(EnemyEditorUI enemyEditor) {
		this.enemyEditorUI = enemyEditor;
		timer = new Timer();
		timer.scheduleTask(new Task() {
			@Override
			public void run() {
				time += 0.05f;
				if (world != null)
					enemyEditorUI.setTime(world.mp3Music.getPosition()-time);
			}
		}, 0, 0.05f);
	}

	@Override
	public void reverseCheck() {

		System.out.println("no reverseCheck enemy base");
	}
}
