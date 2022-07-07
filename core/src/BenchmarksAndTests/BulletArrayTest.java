package BenchmarksAndTests;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class BulletArrayTest {

	private final int num = 50000;
	private float[] x = new float[num], y = new float[num];
	private float[] vx = new float[num], vy = new float[num];
	public Sprite[] sprite = new Sprite[num];
	private int index = 0;
	private Texture t;

	/*
	 * @50000 ullets rendered on screen, moving, the array implementation
	 * performed worse than the individual objects, bt within margin of error
	 * (35FPS, vs 36FPS) --> Objects are simpler to implement and use, without a
	 * large performance drawback
	 */
	public BulletArrayTest(Texture t) {
		this.t = t;
		for (int i = 0; i < num; i++) {
			sprite[i] = new Sprite(t);
			x[i] = i % 2560;
			y[i] = (2 * i) % 1440;
			vx[i] = i % 10;
			vy[i] = (i * 8) % 30;
		}
	}
	public void update(float delta) {
		for (int i = 0; i < num; i++) {
			x[i] += vx[i];
			y[i] += vy[i];

			y[i] = y[i] % 1440;
			x[i] = x[i] % 2560;
			sprite[i].setPosition(x[i], y[i]);
		}
	}

}
