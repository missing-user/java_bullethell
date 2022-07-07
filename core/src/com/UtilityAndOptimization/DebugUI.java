package com.UtilityAndOptimization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.mygdx.game.GameWorld;

public class DebugUI {

	private GameWorld world;
	private Skin skin;
	private Group group;
	private Stage stage;
	private Label lblFps;
	private Label lblObjects;
	private Label lblParticles;
	private Label lblBackground;
	private Label lblHealth;

	public DebugUI(Stage stage, GameWorld worldp) {
		this.world = worldp;
		this.stage = stage;

		skin = new Skin(Gdx.files.internal(Utility.skinLocation));
		group = new Group();
		group.setBounds(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		lblFps = new Label("fps", skin);
		lblFps.setFontScale(2);
		lblFps.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - 25);
		lblObjects = new Label("bullets", skin);
		lblObjects.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - 50);
		lblParticles = new Label("particles", skin);
		lblParticles.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - 75);
		lblBackground = new Label("background", skin);
		lblBackground.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - 100);
		lblHealth = new Label("health", skin);
		lblHealth.setPosition(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() - 125);
		group.addActor(lblFps);
		group.addActor(lblObjects);
		group.addActor(lblParticles);
		group.addActor(lblBackground);
		group.addActor(lblHealth);
		stage.addActor(group);
		stage.getCamera().position.set(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2, 0);
	}

	public void update() {
		lblFps.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
		lblObjects.setText(
				"Bullets: " + world.gameInstance.bulletRenderListe.size());
		lblParticles.setText("Particles: " + world.gameInstance.effects.size());
		lblBackground.setText(
				"Background: " + world.gameInstance.hintergrund.size());
		lblHealth.setText("hits: " + world.p.hits);
	}
}
