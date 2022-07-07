package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.UtilityAndOptimization.BulletInterface;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class MainGame extends ApplicationAdapter {
	public final int WIDTH_CAMERA = 2560;
	private float MouseFactor;

	private SpriteBatch batch;
	private OrthographicCamera camera;
	private Stage stage;

	public ArrayList<DynamicObject> hintergrund = new ArrayList<DynamicObject>();
	public ArrayList<DynamicObject> bulletRenderListe = new ArrayList<DynamicObject>();
	public ArrayList<DynamicObject> vordergrund = new ArrayList<DynamicObject>();
	public ArrayList<ParticleEffect> effects = new ArrayList<ParticleEffect>();

	private GameWorld world;

	public stateEnum state = stateEnum.Running;
	// debugging stuff
	public ShapeRenderer shapeRenderer;
	public boolean debugging = false;

	public enum stateEnum {
		Running, Paused
	}

	@Override
	public void create() {
		// run once the game begins
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(true, WIDTH_CAMERA, (WIDTH_CAMERA / 16) * 9);
		Gdx.graphics.setWindowedMode(Gdx.graphics.getDisplayMode().width,
				Gdx.graphics.getDisplayMode().height);
		//Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		camera.translate(new Vector2(128, 128));
		camera.update();

		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		// for debug
		shapeRenderer = new ShapeRenderer();

		// create the world
		MouseFactor = ((float) WIDTH_CAMERA) / (float) Gdx.graphics.getWidth();
		world = new GameWorld(this, MouseFactor);
	}

	@Override
	public void render() {
		stage.setDebugAll(debugging);

		if (state == stateEnum.Running) {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			batch.setProjectionMatrix(camera.combined);
			batch.begin();

			for (Iterator<ParticleEffect> iterator = effects
					.iterator(); iterator.hasNext();) {
				ParticleEffect p = iterator.next();
				p.update(Gdx.graphics.getDeltaTime());
				p.draw(batch);
				if (p.isComplete())
					iterator.remove();
			}

			float realDelta = Gdx.graphics.getDeltaTime();
			if (Gdx.graphics.getDeltaTime() > 0.2f) {
				realDelta = 0.01f;
				state = stateEnum.Paused;

				for (DynamicObject o : hintergrund) {
					o.pause(true);
				}
			}
			realDelta *= world.timeFactor;
			if (Gdx.input.isKeyJustPressed(Keys.B)) {
				world.timeFactor *= -1;
			}

			world.update(realDelta);

			// iterators, so removing them dynamically is possible
			for (Iterator<DynamicObject> iterator = hintergrund
					.iterator(); iterator.hasNext();) {
				DynamicObject o = iterator.next();
				o.update(realDelta);
			}

			for (Iterator<DynamicObject> iterator = bulletRenderListe
					.iterator(); iterator.hasNext();) {
				DynamicObject o = iterator.next();
				o.update(realDelta);
				
				if (((BulletInterface) o).isFree()) {
					iterator.remove();
				}else
				{//o.sprite.draw(batch);
					}
			}

			for (Iterator<DynamicObject> iterator = vordergrund
					.iterator(); iterator.hasNext();) {
				DynamicObject o = iterator.next();
				o.update(realDelta);
			}

			if (world.timeFactor < 0) {
				for (DynamicObject o : hintergrund) {
					o.reverseCheck();
				}
				for (DynamicObject o : bulletRenderListe) {
					o.reverseCheck();
				}
				for (DynamicObject o : vordergrund) {
					o.reverseCheck();
				}
			}

			// rendering
			for (DynamicObject o : hintergrund) {
				o.sprite.draw(batch);
			}
			for (DynamicObject o : bulletRenderListe) {
				o.sprite.draw(batch);
			}
			for (DynamicObject o : vordergrund) {
				o.sprite.draw(batch);
			}

			batch.end();
			// render the UI
			stage.act(realDelta);
			stage.draw();

			if (debugging) {
				debug();
			}
		} else {
			switch (state) {
				default :
					System.out.println("default state reached");
					break;
				case Paused :
					if (Gdx.graphics.getDeltaTime() < 0.2f) {
						state = stateEnum.Running;
						for (DynamicObject o : hintergrund) {
							o.pause(false);
						}
					}
					System.out.println("paused");
					break;
			}
		}
	}

	private void debug() {
		// shapes for debugging
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.RED);

		for (DynamicObject o : vordergrund) {
			o.debug();
		}
		for (DynamicObject o : bulletRenderListe) {
			o.debug();
		}
		for (DynamicObject o : hintergrund) {
			o.debug();
		}
		// debug shapes end
		shapeRenderer.end();

		System.out.println(Gdx.graphics.getFramesPerSecond());
	}

	@Override
	public void dispose() {
		// run once the game is closed
		batch.dispose();
		stage.dispose();
	}

	public Stage getStage() {
		return stage;
	}
}
