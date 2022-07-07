package com.Editors;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXB;

import com.Levels.BossBase;
import com.UtilityAndOptimization.Utility;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.mygdx.game.BulletTester;
import com.mygdx.game.PatternPack;

public class MarshallClass {

	public MarshallClass() {
	}

	// enemy
	public String toStr(BossBase eb) {
		StringWriter sw = new StringWriter();
		JAXB.marshal(eb, sw);
		return sw.toString();
	}

	public BossBase toEnemy(String str) {
		BossBase eb = JAXB.unmarshal(new StringReader(str), BossBase.class);
		System.out.println("enemy successfully unmarshalled");
		return eb;
	}

	// patternPack
	public String toStr(PatternPack pp) {
		StringWriter sw = new StringWriter();
		JAXB.marshal(pp, sw);
		return sw.toString();
	}

	public PatternPack toPatternPack(String str) {
		PatternPack pp = JAXB.unmarshal(new StringReader(str),
				PatternPack.class);
		System.out.println("PatternPack successfully unmarshalled");
		return pp;
	}

	// pattern
	public String toStr(BulletTester bt) {
		StringWriter sw = new StringWriter();
		JAXB.marshal(bt, sw);
		return sw.toString();
	}

	public BulletTester toPattern(String str) {
		BulletTester bt = JAXB.unmarshal(new StringReader(str),
				BulletTester.class);
		System.out.println("pattern successfully unmarshalled");
		return bt;
	}

	public String readFile(String fileName) {
		FileHandle file = Gdx.files.internal("XML/" + fileName + ".xml");
		return file.readString();
	}

	public String readExternalFile(String filepath) {
		FileHandle file = Gdx.files.external(filepath);
		return file.readString();
	}

	public void writeFile(String fileName, String xmlString) {
		FileHandle file = Gdx.files
				.external(Utility.filePath + fileName + ".xml");
		System.out.println(fileName);
		System.out.println(Utility.filePath + fileName + ".xml");
		file.writeString(xmlString, false);
	}
}
