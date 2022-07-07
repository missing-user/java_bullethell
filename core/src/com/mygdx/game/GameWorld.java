package com.mygdx.game;

import java.nio.channels.FileLockInterruptionException;
import java.util.Iterator;

import com.Editors.EnemyEditorUI;
import com.Editors.PatternEditorUI;
import com.Levels.StaticBulletPattern;
import com.UtilityAndOptimization.BulletInterface;
import com.UtilityAndOptimization.DebugUI;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;

public class GameWorld {

	// public variables
	public Player p;
	public MainGame gameInstance;
	public Texture bulletTexture;

	public float time;
	public float timeFactor = 1;
	public Color gameColor = Color.GOLD;
	private PatternEditorUI patternUI;
	private EnemyEditorUI enemyUI;
	private DebugUI debugUI;
	public Music mp3Music;

	public GameWorld(MainGame gameInstance, float mouseFactor) {
		bulletTexture = new Texture(Gdx.files.internal(Utility.textureString));
		mp3Music = Gdx.audio
				.newMusic(Gdx.files.internal("music/omfg-hello.mp3"));
		this.gameInstance = gameInstance;
		p = new Player(this, mouseFactor);

		// <><><><><><><><><><><><><><><><><><><><><><><><><><>
		// WARNING: NO CLEAN CODE IN THE FOLLOWING AREA!!!

		patternUI = new PatternEditorUI(gameInstance.getStage(), this);
		enemyUI = new EnemyEditorUI(gameInstance.getStage(), this);
		debugUI = new DebugUI(gameInstance.getStage(), this);
		openPatternUI();

		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

		 //new StandardMovingEnemyFinn(this);
		 //new StaticBulletPattern(this);
		 //new PassiveBulletTester().activate(this, 0);;

		// ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

	}
	
	public void resetLevel() {
		for (Iterator<DynamicObject> iterator = gameInstance.bulletRenderListe.iterator(); iterator.hasNext();) {
			DynamicObject o = iterator.next();
			((BulletInterface) o).free();
			iterator.remove();
		}
		for (Iterator<ParticleEffect> iterator = gameInstance.effects.iterator(); iterator.hasNext();) {
			ParticleEffect o = iterator.next();
			o.dispose();
			iterator.remove();
		}
	}

	public void openEnemyUI() {
		enemyUI.open();
	}

	public void openPatternUI() {
		patternUI.open();
	}

	public void update(float delta) {
		time += delta;
		gameColor = new Color(Utility.HSVtoRGB(time, 1, 1));
		debugUI.update();
	}
}
