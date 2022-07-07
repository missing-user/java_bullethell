package com.UtilityAndOptimization;

import com.badlogic.gdx.graphics.Color;

public class Utility {

	public final static String textureString = "sprite/whiteBullet50.png";
	//public final static String textureString = "sprite/doge.png";
	public final static String skinLocation = "skin/visui/uiskin.json";
	public final static String filePath = "/Desktop/XML";
	public final static String filePrefixPatterns = "/Patterns/";
	public static final String filePrefixEnemies = "/Enemies/";
	public static final boolean invulnerable = false;

	public Utility() {
	}

	public static float triangleWave(float x, float dur, float offset) {
		// duration of an interval
		// float dur = 1f/frequency;
		if (dur == 0)
			return offset;
		return Math.abs(((x + offset) / dur) % 4 - 2) - 1;
	}

	public static float sineWave(float xIn, float dur, float offset) {
		final float B = (float) (4 / Math.PI);
		final float C = (float) (-4 / (Math.PI * Math.PI));
		final float pi2 = (float) (2 * Math.PI);
		if (dur == 0)
			return offset;
		float x = mod((xIn + offset) / dur, pi2) - pi2 / 2;
		return B * x + C * x * Math.abs(x);
	}

	public static float mod(float x, float modBy) {
		// safe modulo, correct for negative numbers
		return ((x % modBy) + modBy) % modBy;
	}

	// somewhere, over the rainbow.... daaaa daaaaa..... somewhere over the
	// rainbow over the rainbow now...
	public static Color HSVtoRGB(float h, float s, float v) {
		// H is given on [0->6] or -1. S and V are given on [0->1].
		// RGB are each returned on [0->1].
		float m, n, f;
		int i;

		float[] hsv = new float[3];
		Color rgb = new Color(1, 1, 1, 1);

		hsv[0] = (h % 6 + 6) % 6;
		hsv[1] = s;
		hsv[2] = v;

		if (hsv[0] == -1) {
			rgb.r = rgb.g = rgb.b = hsv[2];
			return rgb;
		}
		i = (int) (Math.floor(hsv[0]));
		f = hsv[0] - i;
		if (i % 2 == 0) {
			f = 1 - f; // if i is even
		}
		m = hsv[2] * (1 - hsv[1]);
		n = hsv[2] * (1 - hsv[1] * f);
		switch (i) {
			case 6 :
			case 0 :
				rgb.r = hsv[2];
				rgb.g = n;
				rgb.b = m;
				break;
			case 1 :
				rgb.r = n;
				rgb.g = hsv[2];
				rgb.b = m;
				break;
			case 2 :
				rgb.r = m;
				rgb.g = hsv[2];
				rgb.b = n;
				break;
			case 3 :
				rgb.r = m;
				rgb.g = n;
				rgb.b = hsv[2];
				break;
			case 4 :
				rgb.r = n;
				rgb.g = m;
				rgb.b = hsv[2];
				break;
			case 5 :
				rgb.r = hsv[2];
				rgb.g = m;
				rgb.b = n;
				break;
		}

		return rgb;
	}

	public static Color distToColor(float distance, Color gameColor) {
		// performance lost by this "shading" is neglegible
		// (21FPS ->20FPS@125000bullets)

		// different hihglight curves
		// float v = 1f - (float) (1.3 * Math.log(distance/30 + 0.85));
		 float v = 1f / (distance / 10f);
		// float v = (distance/30)%1;
		// float v = distance<25?1:0;

		Color min = new Color(0.3f, 0.3f, 0.3f, 1);

		Color c = new Color((float) Math.max(min.r, gameColor.r * v),
				(float) Math.max(min.g, gameColor.g * v),
				(float) Math.max(min.b, gameColor.b * v), 1);
		// c = new Color(Utility.HSVtoRGB(distance/10, 1, 1));
		 c = gameColor;
		return c;
	}

	public static float sin(float rad) {
		/*final float B = (float) (4 / Math.PI);
		final float C = (float) (-4 / (Math.PI * Math.PI));
		final float pi2 = (float) (2 * Math.PI);
		float x = mod(rad, pi2) - pi2 / 2;
		return -(B * x + C * x * Math.abs(x));*/
		return (float) Math.sin(rad);
	}

	public static float cos(float rad) {
		/*final float B = (float) (4 / Math.PI);
		final float C = (float) (-4 / (Math.PI * Math.PI));
		final float pi2 = (float) (2 * Math.PI);
		final float pi = (float) Math.PI;
		float x = mod(rad + pi / 2, pi2) - pi;
		return -(B * x + C * x * Math.abs(x));*/
		return (float) Math.cos(rad);
	}

	public static float toRadians(float deg) {
		final float factor = (float) Math.toRadians(1);
		return deg * factor;
	}

	public static float asin(float rad) {
		return (float) Math.asin(rad);
	}

	public static float acos(float rad) {
		return (float) Math.acos(rad);
	}

	public static float sqrt(float f) {
		return (float) Math.sqrt(f);
	}

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// pseudocode from unity forums, not mine!!!!!!!!!!!!!!!
	public static float CalcShortestRot(float from, float to) {
		float pi = (float) Math.PI;
		// If from or to is a negative, we have to recalculate them.
		// For an example, if from = -45 then from(-45) + 360 = 315.
		if (from < 0) {
			from += 2 * pi;
		}

		if (to < 0) {
			to += 2 * pi;
		}

		// Do not rotate if from == to.
		if (from == to || from == 0 && to == 2 * pi
				|| from == 2 * pi && to == 0) {
			return 0;
		}

		// Pre-calculate left and right.
		float left = (2 * pi - from) + to;
		float right = from - to;
		// If from < to, re-calculate left and right.
		if (from < to) {
			if (to > 0) {
				left = to - from;
				right = (2 * pi - to) + from;
			} else {
				left = (2 * pi - to) + from;
				right = to - from;
			}
		}

		// Determine the shortest direction.
		return ((left <= right) ? left : (right * -1));
	}

	// If CalcShortestRot returns a positive value, then this function
	// will return true for left. Else, false for right.
	public static boolean CalcShortestRotDirection(float from, float to) {
		// If the value is positive, return true (left).
		if (CalcShortestRot(from, to) >= 0) {
			return true;
		}
		return false; // right
	}

	public static float AngleDifference(float radFrom, float radTo) {
		float pi = (float) Math.PI;
		float diff = (radTo - radFrom + pi) % (2 * pi) - pi;
		return diff < -pi ? diff + (2 * pi) : diff;
	}
}
