package com.Editors;

import java.util.Iterator;

import com.Levels.BossBase;
import com.UtilityAndOptimization.BulletInterface;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.Bullet;
import com.mygdx.game.GameWorld;
import com.mygdx.game.PatternPack;
import com.mygdx.game.DynamicObject;

public class EnemyEditorUI {

	private Skin skin;
	private Group group;
	private GameWorld world;
	private TextButton btnBack, btnTmp, btnActivate, btnDelete, btnDeletePack, btnConvert;
	private TextField txtStartTime, txtEndTime, txtFileName;
	private Label lblStartTime, lblEndTime, lblTime;
	private Stage stage;
	private int listenerLoopCount;
	private Table table, tableAdded;
	private Table container, containerAdded;
	private ScrollPane scrollpane, scrollpaneAdded;

	private int selectedPack = -1;
	private Array<String> checkboxNames = new Array<String>();

	// checkbox list
	private Array<CheckBox> patternCheckboxes = new Array<CheckBox>();
	private ButtonGroup<CheckBox> buttonGroupPatterns;

	private BossBase eb;

	public EnemyEditorUI(Stage stage, GameWorld worldp) {
		this.world = worldp;
		eb = new BossBase();
		eb.editor(this);
		buttonGroupPatterns = new ButtonGroup<CheckBox>();
		buttonGroupPatterns.setMinCheckCount(0);
		buttonGroupPatterns.setMaxCheckCount(1);

		skin = new Skin(Gdx.files.internal(Utility.skinLocation));
		group = new Group();
		group.setBounds(0, 0, Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight());
		this.stage = stage;

		addComponents();
		groupComponents();
		addListeners();
		setIndividualSettings();
	}

	public void open() {
		loadFiles();
		stage.addActor(group);
		stage.getCamera().position.set(Gdx.graphics.getWidth() / 2,
				Gdx.graphics.getHeight() / 2, 0);
	}

	public void close() {
		group.remove();
	}

	private void groupComponents() {
		group.addActor(btnBack);
		group.addActor(btnActivate);
		group.addActor(btnDelete);
		group.addActor(btnConvert);
		group.addActor(btnDeletePack);
		group.addActor(container);
		group.addActor(lblEndTime);
		group.addActor(lblStartTime);
		group.addActor(lblTime);
		group.addActor(txtEndTime);
		group.addActor(txtFileName);
		group.addActor(txtStartTime);
		group.addActor(containerAdded);
	}

	private void setIndividualSettings() {

		btnBack.setPosition(50, 50);
		btnActivate.setPosition(150, 50);
		btnDelete.setPosition(Gdx.graphics.getWidth() - 150,
				Gdx.graphics.getHeight() - 50);
		btnDeletePack.setPosition(Gdx.graphics.getWidth() - 150,
				Gdx.graphics.getHeight() - 100);
		lblStartTime.setPosition(50, 350 + 50 * 10);
		txtStartTime.setPosition(50, 300 + 50 * 10);
		lblEndTime.setPosition(250, 350 + 50 * 10);
		txtEndTime.setPosition(250, 300 + 50 * 10);
		txtFileName.setPosition(100, Gdx.graphics.getHeight()-50);
		btnConvert.setPosition(50, Gdx.graphics.getHeight()-50);
		txtEndTime.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		txtStartTime.setTextFieldFilter(new TextFieldFilter.DigitsOnlyFilter());
		lblTime.setPosition(100,Gdx.graphics.getHeight()-100);
		lblTime.setFontScale(5);

		patternCheckboxes.add(new CheckBox("no Pack selected", skin));
		tableAdded.add(patternCheckboxes.get(0)).fillX();
	}

	private void loadFiles() {
		table.clear();
		FileHandle dirHandle;
		final String filePath = Utility.filePath + "/Packs/";
		if (Gdx.app.getType() == ApplicationType.Android) {
			dirHandle = Gdx.files.internal("some/directory");
		} else {
			// ApplicationType.Desktop
			/*
			 * links directly to the assets directory dirHandle =
			 * Gdx.files.internal("./bin/effects/");
			 */
			dirHandle = Gdx.files.external(filePath);
		}
		// add all the button (same as pattern editor)
		for (listenerLoopCount = 0; listenerLoopCount < dirHandle
				.list().length; listenerLoopCount++) {
			FileHandle entry = dirHandle.list()[listenerLoopCount];
			System.out.println("file found: " + entry.name());
			btnTmp = new TextButton(entry.name(), skin);
			table.add(btnTmp).fillX();
			table.row();
			// load button listeners
			btnTmp.addListener(new ClickListener() {
				int tmp = listenerLoopCount;
				@Override
				public void clicked(InputEvent event, float x, float y) {
					System.out.println(filePath
							+ ((TextButton) table.getChildren().get(tmp))
									.getText());
					MarshallClass m = new MarshallClass();
					PatternPack p = m.toPatternPack(m.readExternalFile(filePath
							+ ((TextButton) table.getChildren().get(tmp))
									.getText()));
					// activate the patterns properly
					if (eb.isActive())
						eb.deactivateAll();
					deleteAllBullets();
					eb.patternPacks.add(p);
					// add slots for the new timings, assign default value, set
					// text
					eb.timings.add(0f);
					eb.timingsDecativation.add(5f);
					if (eb.timingsDecativation.size() > 1) {
						eb.timingsDecativation.set(
								eb.timingsDecativation.size() - 1,
								(eb.timingsDecativation
										.get(eb.timingsDecativation.size() - 2)
										+ 5f));
						if (eb.timings.size() > 1)
							eb.timings.set(eb.timings.size() - 1,
									eb.timingsDecativation.get(
											eb.timingsDecativation.size() - 2));
					}
					checkboxNames.add(eb.timings.get(eb.timings.size() - 1)
							+ " - " + eb.timingsDecativation
									.get(eb.timingsDecativation.size() - 1));
					eb.activateAll(world);
					createPatternList();

					// select the new pattern Pack
					selectedPack = eb.patternPacks.size() - 1;
				}
			});
		}
	}

	private void addComponents() {
		btnBack = new TextButton("back", skin);
		btnActivate = new TextButton("deactivate", skin);
		btnDeletePack = new TextButton("remove", skin);
		btnDelete = new TextButton("delete", skin);
		btnConvert = new TextButton("save", skin);
		lblEndTime = new Label("N/A", skin);
		lblStartTime = new Label("N/A", skin);
		lblTime = new Label("time", skin);
		txtEndTime = new TextField("500", skin);
		txtStartTime = new TextField("0", skin);
		txtFileName = new TextField("file name", skin);

		// scrollPane settings
		table = new Table(skin);
		container = new Table(skin);
		scrollpane = new ScrollPane(table, skin);
		container.add(scrollpane).width(350f)
				.height(Gdx.graphics.getHeight() - 350 - 50 * 10);
		container.row();
		container.align(Align.topLeft);
		container.setPosition(50, Gdx.graphics.getHeight() - 250 - 50 * 10);

		// other scrollpane
		tableAdded = new Table(skin);
		containerAdded = new Table(skin);
		scrollpaneAdded = new ScrollPane(tableAdded, skin);
		containerAdded.add(scrollpaneAdded).width(200f)
				.height((Gdx.graphics.getHeight() / 2) - 100);
		containerAdded.row();
		containerAdded.align(Align.topRight);
		containerAdded.setPosition(Gdx.graphics.getWidth() - 50,
				Gdx.graphics.getHeight() - 100);
	}

	private void addListeners() {
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("back");
				close();
				world.openPatternUI();
			}
		});

		btnDeletePack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (eb.patternPacks.size() > 1) {
					System.out.println("pack removed");
					eb.patternPacks.get(selectedPack).deactivateAll();
					eb.patternPacks.remove(selectedPack);
					checkboxNames.removeIndex(selectedPack);
					createPatternList();
					selectedPack = eb.patternPacks.size() - 1;
				}
			}
		});

		btnDelete.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("deleted");
				deleteAllBullets();
			}
		});

		btnActivate.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (eb.isActive()) {
					eb.deactivateAll();
				} else {
					eb.activateAll(world);
				}
				btnActivate.setText(isActive());
				deleteAllBullets();
			}
		});
		
		btnConvert.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				System.out.println("converted");
				MarshallClass m = new MarshallClass();
				String str = m.toStr(eb);
				if (txtFileName.getText() != null) {
					m.writeFile(Utility.filePrefixEnemies + txtFileName.getText(),
							str);
				} else {
					System.out.println(
							"failed to write file " + txtFileName.getText());
				}
			}
		});

		txtEndTime.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// everything is handeled in 1/100 s

				if (!txtEndTime.getText().equals("") && selectedPack >= 0) {
					if (eb.timingsDecativation.size() >= selectedPack) {
						eb.timingsDecativation.set(selectedPack,
								Float.parseFloat(txtEndTime.getText()) / 100);
					} else {
						eb.timingsDecativation.add(
								Float.parseFloat(txtEndTime.getText()) / 100);
						System.out
								.println("timingsDeactivation just got longer");
					}
					if (eb.timingsDecativation.size() > 1 && selectedPack >= 0)
						lblEndTime.setText(String.format("%.2f",
								eb.timingsDecativation.get(selectedPack))
								+ "s");
					updateNames();
				}
			}
		});

		txtStartTime.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// everything is handeled in 1/100 s
				if (!txtStartTime.getText().equals("") && selectedPack >= 0) {
					if (eb.timings.size() >= selectedPack) {
						eb.timings.set(selectedPack,
								Float.parseFloat(txtStartTime.getText()) / 100);
					} else {
						eb.timings.add(
								Float.parseFloat(txtStartTime.getText()) / 100);
						System.out.println("timings just got longer");
					}
					if (eb.timings.size() > 1 && selectedPack >= 0)
						lblStartTime.setText(String.format("%.2f",
								eb.timings.get(selectedPack)) + "s");
					updateNames();
				}
			}
		});
	}

	private String isActive() {
		if (eb.isActive()) {
			return "active";
		}
		return "idle";
	}

	private void createPatternList() {
		patternCheckboxes.clear();
		tableAdded.clear();
		buttonGroupPatterns.clear();
		for (int i = 0; i < eb.patternPacks.size(); i++) {
			patternCheckboxes.add(new CheckBox(checkboxNames.get(i), skin));
			tableAdded.add(patternCheckboxes.get(i)).fillX();
			tableAdded.row();
			patternCheckboxes.get(i).align(Align.left);
			buttonGroupPatterns.add(patternCheckboxes.get(i));
		}
		patternCheckboxes.get(patternCheckboxes.size - 1).setChecked(true);
		for (listenerLoopCount = 0; listenerLoopCount < patternCheckboxes.size; listenerLoopCount++) {
			patternCheckboxes.get(listenerLoopCount)
					.addListener(new ChangeListener() {
						@Override
						public void changed(ChangeEvent event, Actor actor) {
							setPatternPack();
						}
					});
		}
	}

	private void setPatternPack() {
		deleteAllBullets();
		eb.deactivateAll();
		for (int i = 0; i < patternCheckboxes.size; i++) {
			if (patternCheckboxes.get(i).isChecked()) {
				eb.activateIndex(i);
				btnActivate.setText(isActive());
				selectedPack = i;
				updateNames();
				break;
			}
		}
	}

	private void deleteAllBullets() {
		for (Iterator<DynamicObject> iterator = world.gameInstance.bulletRenderListe
				.iterator(); iterator.hasNext();) {
			DynamicObject o = iterator.next();
			((BulletInterface) o).free();
			iterator.remove();
		}
	}

	private void updateNames() {
		String start = eb.timings.get(selectedPack) + "s";
		String end = eb.timingsDecativation.get(selectedPack) + "s";
		if (eb.timings.get(selectedPack) > 100) {
			// start to minutes
			start = (String.format("%.2f", eb.timings.get(selectedPack) / 60f))
					+ "min";
		}
		if (eb.timingsDecativation.get(selectedPack) > 100) {
			// end to minutes
			end = (String.format("%.2f",
					eb.timingsDecativation.get(selectedPack) / 60f)) + "min";
		}
		checkboxNames.set(selectedPack, start + " - " + end);
		patternCheckboxes.get(selectedPack)
				.setText(checkboxNames.get(selectedPack));
	}
	
	public void setTime(float f)
	{
		lblTime.setText(String.format("%.2f", f));
	}
}
