package com.Editors;

import java.util.Iterator;
import com.badlogic.gdx.Application.ApplicationType;
import com.UtilityAndOptimization.BulletInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.BulletTester;
import com.mygdx.game.GameWorld;
import com.mygdx.game.PatternPack;
import com.mygdx.game.DynamicObject;

public class PatternEditorUI {

	private Skin skin;
	private Group group;
	private TextButton btnConvert, btnAdd, btnDelete, btnLoad, btnEnemy,
			btnPack;
	private TextButton btnTmp;
	private TextField txtFileName;
	private BulletTester bt;
	private Array<BulletTester> btList = new Array<BulletTester>();
	private GameWorld world;

	private final int sliders = 20;
	private final int checkCount = 12;
	private Slider valueSlider[] = new Slider[sliders];
	private Label values[] = new Label[sliders];
	private CheckBox checkboxes[] = new CheckBox[checkCount];
	private Label labels[] = new Label[sliders];
	private int[] minMax = {-800, 800, 1, 200, 0, 200, 0, 360, 1, 10, -300, 300,
			0, 360, 1, 100, 1, 100, 1, 1200, -200, 200, 0, Gdx.graphics.getWidth(), 0, Gdx.graphics.getHeight(),
			-800, 800, -800, 800, 0, 700, 0, 100, 0, 360, 0, 360, 1, 100};
	public int iListeners;
	private String chkStrings[] = {"debug", "trail", "destroy on return",
			"relative", "slowmo", "circular", "keepCenter", "BPM",
			"rotation Change", "smooth rotation", "spray change",
			"smooth spray"};
	private String labelStrings[] = {"speed", "loop time", "break time",
			"spray degrees", "cones", "maxRotationRate", "total spray",
			"bullets per cone", "rotation change", "interval", "acceleration",
			"X emitter", "Y emitter", "bulletRotationFactor", "vOut",
			"initialRadius", "randomness", "minSpray", "maxSpray",
			"spray change"};
	// scroll list variables
	private Table table, container, tablePatterns, containerPatterns;
	private ScrollPane scrollpane, scrollpanePatterns;
	private boolean loading = true;
	private Stage stage;
	private Array<CheckBox> patternCheckboxes = new Array<CheckBox>();
	private ButtonGroup<CheckBox> buttonGroupPatterns;
	private PatternPack pp;
	private boolean bpm;
	private int sliderSize = 40;

	public PatternEditorUI(Stage stage, GameWorld worldp) {
		this.world = worldp;
		this.stage = stage;

		skin = new Skin(Gdx.files.internal(Utility.skinLocation));
		group = new Group();
		group.setBounds(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());

		addComponents();
		groupComponents();
		addListeners();
		setIndividualSettings();
		loading = false;

		pp = new PatternPack();
	}

	private void groupComponents() {
		// add all the components to the group to be rendered
		group.addActor(btnConvert);
		group.addActor(btnAdd);
		group.addActor(txtFileName);
		group.addActor(btnDelete);
		group.addActor(btnLoad);
		group.addActor(btnEnemy);
		group.addActor(btnPack);
		group.addActor(container);
		group.addActor(containerPatterns);
		for (int i = 0; i < sliders; i++) {
			group.addActor(labels[i]);
			group.addActor(values[i]);
			group.addActor(valueSlider[i]);
		}
		for (int i = 0; i < checkCount; i++) {
			group.addActor(checkboxes[i]);
		}
	}

	private void setIndividualSettings() {
		// slider specific settings
		valueSlider[11].setSnapToValues(
				new float[]{-200, -100, -50, 0, 50, 100, 200}, 20);
		valueSlider[12].setSnapToValues(
				new float[]{-200, -100, -50, 0, 50, 100, 200}, 20);
		valueSlider[4].setSnapToValues(new float[]{90, 180, 360}, 30);
		valueSlider[6].setSnapToValues(new float[]{90, 180, 360}, 30);
		valueSlider[7].setSnapToValues(
				new float[]{1, 2, 3, 4, 5, 20, 50, 75, 100}, 5);
		valueSlider[9].setSnapToValues(new float[]{1, 5, 10, 20, 50, 100, 150,
				200, 300, 400, 500, 600, 700, 800, 900, 1000, 1100, 1200}, 30);
		valueSlider[0].setSnapToValues(new float[]{-800, -600, -400, -200, -100,
				-50, -25, 0, 25, 50, 100, 200, 400, 600, 800}, 30);
		valueSlider[14].setSnapToValues(new float[]{-800, -600, -400, -200,
				-100, -50, -25, 0, 25, 50, 100, 200, 400, 600, 800}, 30);
		valueSlider[15].setSnapToValues(
				new float[]{0, 50, 100, 200, 300, 400, 500, 600, 700}, 30);
		valueSlider[13].setSnapToValues(new float[]{-800, -600, -400, -200,
				-100, -50, -25, 0, 25, 50, 100, 200, 400, 600, 800}, 30);
		valueSlider[10].setSnapToValues(new float[]{-200, -150, -100, -50, -25,
				-10, -5, 0, 5, 10, 25, 50, 100, 150, 200}, 30);
		checkboxes[2].setChecked(true);

		patternCheckboxes.add(new CheckBox("current Pattern", skin));
		tablePatterns.add(patternCheckboxes.get(0)).fillX();
	}

	private void addComponents() {
		// create all the components and set their positions
		for (int i = 0; i < sliders; i++) {
			valueSlider[i] = new Slider(minMax[2 * i], minMax[2 * i + 1], 1,
					false, skin);
			valueSlider[i].setPosition(200,
					Gdx.graphics.getHeight() - 150 - i * sliderSize);
			valueSlider[i].setAnimateDuration(0.125f);
			values[i] = new Label("1", skin);
			values[i].setPosition(350,
					Gdx.graphics.getHeight() - 150 - i * sliderSize);
			labels[i] = new Label(labelStrings[i], skin);
			labels[i].setPosition(50,
					Gdx.graphics.getHeight() - 150 - i * sliderSize);
		}
		// create all checkboxes
		for (int i = 0; i < checkCount; i++) {
			checkboxes[i] = new CheckBox(chkStrings[i], skin);
			checkboxes[i].setPosition(Gdx.graphics.getWidth() - 150,
					Gdx.graphics.getHeight() - 100 - i * 50);
			checkboxes[i].align(Align.left);
		}
		btnConvert = new TextButton("convert", skin);
		btnConvert.setPosition(150,
				Gdx.graphics.getHeight() - 200 - sliderSize * sliders);
		btnEnemy = new TextButton("enemy", skin);
		btnEnemy.setPosition(Gdx.graphics.getWidth() - 100,
				Gdx.graphics.getHeight() - 200 - sliderSize * sliders);
		btnAdd = new TextButton("add", skin);
		btnAdd.setPosition(50,
				Gdx.graphics.getHeight() - 200 - sliderSize * sliders);
		btnDelete = new TextButton("delete", skin);
		btnDelete.setPosition(Gdx.graphics.getWidth() - 150,
				Gdx.graphics.getHeight() - 50);
		btnLoad = new TextButton("load", skin);
		btnLoad.setPosition(Gdx.graphics.getWidth() - 50,
				Gdx.graphics.getHeight() - 50);
		txtFileName = new TextField("untitled", skin);
		txtFileName.setPosition(225,
				Gdx.graphics.getHeight() - 200 - sliderSize * sliders);
		btnPack = new TextButton("pack", skin);
		btnPack.setPosition(100,
				Gdx.graphics.getHeight() - 200 - sliderSize * sliders);
		// scrollPane settings
		table = new Table(skin);
		container = new Table(skin);
		scrollpane = new ScrollPane(table, skin);
		container.add(scrollpane).width(200f)
				.height(Gdx.graphics.getHeight() - 300 - sliderSize * sliders);
		container.row();
		container.align(Align.topLeft);
		container.setPosition(50,
				Gdx.graphics.getHeight() - 250 - sliderSize * sliders);
		// same thing for the pattern List
		tablePatterns = new Table(skin);
		containerPatterns = new Table(skin);
		scrollpanePatterns = new ScrollPane(tablePatterns, skin);
		containerPatterns.add(scrollpanePatterns).width(200f)
				.height(Gdx.graphics.getHeight() - 300 - sliderSize * sliders);
		containerPatterns.row();
		containerPatterns.align(Align.topRight);
		containerPatterns.setPosition(Gdx.graphics.getWidth() - 50,
				Gdx.graphics.getHeight() - 250 - sliderSize * sliders);
		buttonGroupPatterns = new ButtonGroup<CheckBox>();
	}

	private void addListeners() {
		btnAdd.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("added");
				if (bt.getName().equals("current pattern")) {
					bt.setName(txtFileName.getText());
				}
				bt = new BulletTester();
				bt.activate(world, 0);
				btList.add(bt);
				select();
				updateSliderValues();
				createPatternList();
				String newText = "";
				newText = txtFileName.getText().replaceAll("[0-9]", "");
				newText = newText.replace("_", "");
				txtFileName.setText(newText + "_" + btList.size);
			}
		});

		btnPack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("packed");
				marshallPatternPack();
			}
		});

		btnEnemy.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("enemy");
				close();
				world.openEnemyUI();
			}
		});

		btnDelete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("deleted");
				deleteAllBullets();
			}
		});

		btnConvert.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("converted");
				MarshallClass m = new MarshallClass();
				String str = m.toStr(bt);
				if (txtFileName.getText() != null) {
					m.writeFile(Utility.filePrefixPatterns + txtFileName.getText(),
							str);
				} else {
					System.out.println(
							"failed to write file " + txtFileName.getText());
				}
				loadFiles();
			}
		});

		btnLoad.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				loadFiles();
				if (world.mp3Music.isPlaying()) {
					world.mp3Music.stop();
				} else {
					world.mp3Music.play();
				}
			}
		});

		// add all the listeners for the value sliders
		for (iListeners = 0; iListeners < valueSlider.length; iListeners++) {
			valueSlider[iListeners].addListener(new ChangeListener() {
				int tmp = iListeners;

				@Override
				public void changed(ChangeEvent event, Actor actor) {
					values[tmp].setText(valueSlider[tmp].getValue() + "");
					writeValuesToGenerator(tmp);
				}
			});
		}

		for (iListeners = 0; iListeners < checkboxes.length; iListeners++) {
			checkboxes[iListeners].addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					writeCheckboxes();
				}
			});
		}
	}

	public void writeValuesToGenerator(int tmp) {
		if (!loading) {
			if(tmp==9)
			{
				if (bpm) {
					bt.interval = ((60f / valueSlider[9].getValue()));
				} else {
					bt.interval = valueSlider[9].getValue() / 1000;
				}
				bt.setTimer(0.1f);
			}
			bt.speed = valueSlider[0].getValue();
			bt.loopTime = (int) valueSlider[1].getValue();
			bt.breakTime = (int) valueSlider[2].getValue();
			bt.sprayDegrees = valueSlider[3].getValue();
			bt.cones = (int) (valueSlider[4].getValue());
			bt.maxRotationRate = valueSlider[5].getValue();
			bt.setRotationRate(valueSlider[5].getValue());
			bt.totalSpray = valueSlider[6].getValue();
			bt.bulletsPerCone = (int) valueSlider[7].getValue();
			bt.rotationChangeDuration = valueSlider[8].getValue();
			bt.acc = valueSlider[10].getValue() / 10;
			bt.x = valueSlider[11].getValue();
			bt.y = valueSlider[12].getValue();
			bt.rotationFactor = valueSlider[13].getValue() / 100;
			bt.vOut = valueSlider[14].getValue();
			bt.initialPathRadius = valueSlider[15].getValue();
			bt.randomness = valueSlider[16].getValue();
			bt.minSpray = valueSlider[17].getValue();
			bt.maxSpray = valueSlider[18].getValue();
			bt.sprayChangeDuration = valueSlider[19].getValue();
		}
	}

	private void writeCheckboxes() {
		if (!loading) {
			world.gameInstance.debugging = checkboxes[0].isChecked();
			bt.trail = checkboxes[1].isChecked();
			bt.destroy = checkboxes[2].isChecked();
			bt.relative = checkboxes[3].isChecked();
			if (checkboxes[4].isChecked()) {
				world.timeFactor = 0.075f;
			} else {
				world.timeFactor = 1;
			}
			bt.circular = checkboxes[5].isChecked();
			bt.keepCenter = checkboxes[6].isChecked();
			bpm = checkboxes[7].isChecked();

			if (bpm) {
				labels[9].setText("BPM");
				valueSlider[9].setSnapToValues(new float[]{60, 80, 105, 120,
						160, 210, 240, 315, 420, 480, 960, 1050, 1200}, 20);
				valueSlider[9].setValue(60 / bt.interval);
			} else {
				labels[9].setText("interval");
				valueSlider[9].setSnapToValues(
						new float[]{1, 5, 10, 20, 50, 100, 150, 200, 300, 400,
								500, 600, 700, 800, 900, 1000, 1100, 1200},
						30);
				valueSlider[9].setValue(bt.interval * 1000);
			}
			bt.rotChange = checkboxes[8].isChecked();
			bt.smoothRot = checkboxes[9].isChecked();
			bt.sprayChange = checkboxes[10].isChecked();
			bt.smoothSpray = checkboxes[11].isChecked();
		}
	}

	private void loadFiles() {
		table.clear();
		FileHandle dirHandle;
		if (Gdx.app.getType() == ApplicationType.Android) {
			dirHandle = Gdx.files.internal("some/directory");
		} else {
			// ApplicationType.Desktop
			/*
			 * links directly to the assets directory dirHandle =
			 * Gdx.files.internal("./bin/effects/");
			 */
			dirHandle = Gdx.files
					.external(Utility.filePath + Utility.filePrefixPatterns);
		}

		for (iListeners = 0; iListeners < dirHandle
				.list().length; iListeners++) {
			FileHandle entry = dirHandle.list()[iListeners];
			System.out.println("file found: " + entry.name());
			btnTmp = new TextButton(entry.name().replace(".xml", ""), skin);
			table.add(btnTmp).fillX();
			table.row();
			// load button listeners
			btnTmp.addListener(new ClickListener() {
				int tmp = iListeners;
				@Override
				public void clicked(InputEvent event, float x, float y) {
					String str = ((TextButton) table.getChildren().get(tmp))
							.getText() + ".xml";
					MarshallClass m = new MarshallClass();
					BulletTester b = m.toPattern(m.readExternalFile(
							Utility.filePath + Utility.filePrefixPatterns + str));
					bt.deactivate();
					b.activate(world, 0);
					bt = b;
					bt.setName(str);
					// remove the deactivated tester, add self
					btList.removeIndex(btList.size - 1);
					btList.add(bt);
					select();
					updateSliderValues();
					txtFileName.setText(str.replace(".xml", ""));
				}
			});
		}
	}

	private void updateSliderValues() {
		loading = true;
		valueSlider[0].setValue(bt.speed);
		valueSlider[1].setValue(bt.loopTime);
		valueSlider[2].setValue(bt.breakTime);
		valueSlider[3].setValue(bt.sprayDegrees);
		valueSlider[4].setValue(bt.cones);
		valueSlider[5].setValue(bt.maxRotationRate);
		valueSlider[6].setValue(bt.totalSpray);
		valueSlider[7].setValue(bt.bulletsPerCone);
		valueSlider[8].setValue(bt.rotationChangeDuration);
		//always show interval in seconds when loading
		checkboxes[7].setChecked(false);
		bpm = false;
		valueSlider[9].setValue(bt.interval * 1000);
		valueSlider[10].setValue(bt.acc * 10);
		valueSlider[11].setValue(bt.x);
		valueSlider[12].setValue(bt.y);
		valueSlider[13].setValue(bt.rotationFactor * 100);
		valueSlider[14].setValue(bt.vOut);
		valueSlider[15].setValue(bt.initialPathRadius);
		valueSlider[16].setValue(bt.randomness);
		valueSlider[17].setValue(bt.minSpray);
		valueSlider[18].setValue(bt.maxSpray);
		valueSlider[19].setValue(bt.sprayChangeDuration);
		checkboxes[0].setChecked(world.gameInstance.debugging);
		checkboxes[1].setChecked(bt.trail);
		checkboxes[2].setChecked(bt.destroy);
		checkboxes[3].setChecked(bt.relative);
		checkboxes[5].setChecked(bt.circular);
		checkboxes[6].setChecked(bt.keepCenter);
		checkboxes[8].setChecked(bt.rotChange);
		checkboxes[9].setChecked(bt.smoothRot);
		checkboxes[10].setChecked(bt.sprayChange);
		checkboxes[11].setChecked(bt.smoothSpray);
		loading = false;
	}

	public void close() {
		group.remove();
		for (BulletTester bulletTester : btList) {
			bulletTester.deactivate();
		}
		deleteAllBullets();
	}

	public void open() {
		stage.addActor(group);
		stage.getCamera().position.set(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2, 0);

		if (btList.size <= 0) {
			bt = new BulletTester();
			bt.activate(world, 0);
			btList.add(bt);
		} else {
			for (BulletTester bulletTester : btList) {
				bulletTester.activate(world, 0);
			}
		}

		select();
		loadFiles();
		updateSliderValues();
	}

	private void createPatternList() {
		patternCheckboxes.clear();
		tablePatterns.clear();
		buttonGroupPatterns.clear();
		buttonGroupPatterns.setMaxCheckCount(1);
		for (int i = 0; i < btList.size; i++) {
			patternCheckboxes.add(new CheckBox(btList.get(i).getName(), skin));
			tablePatterns.add(patternCheckboxes.get(i)).fillX();
			tablePatterns.row();
			patternCheckboxes.get(i).align(Align.left);
			buttonGroupPatterns.add(patternCheckboxes.get(i));
		}
		patternCheckboxes.get(patternCheckboxes.size - 1).setChecked(true);
		for (iListeners = 0; iListeners < patternCheckboxes.size; iListeners++) {
			patternCheckboxes.get(iListeners).addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					setPattern();
				}
			});
		}
	}

	private void setPattern() {
		for (int i = 0; i < patternCheckboxes.size; i++) {
			if (bt.getName().equals("current pattern")) {
				bt.setName(txtFileName.getText());
			}
			if (patternCheckboxes.get(i).isChecked()) {
				bt = btList.get(i);
				updateSliderValues();
			}
			patternCheckboxes.get(i).setText(btList.get(i).getName());
		}
		select();
	}

	private void select() {
		for (BulletTester bulletTester : btList) {
			bulletTester.setSelected(false);
		}
		bt.setSelected(true);
	}

	private void deleteAllBullets() {
		for (Iterator<DynamicObject> iterator = world.gameInstance.bulletRenderListe
				.iterator(); iterator.hasNext();) {
			DynamicObject o = iterator.next();
			((BulletInterface) o).free();
			iterator.remove();
		}
	}

	private void marshallPatternPack() {
		pp.patterns.clear();
		final String filePrefix = "/Packs/";
		for (BulletTester bulletTester : btList) {
			pp.patterns.add(bulletTester);
		}
		MarshallClass m = new MarshallClass();
		String str = m.toStr(pp);
		if (txtFileName.getText() != null) {
			m.writeFile(filePrefix + txtFileName.getText() + "_package", str);
		} else {
			System.out.println("failed to write file " + filePrefix
					+ txtFileName.getText());
		}
		loadFiles();
	}
}
