package BenchmarksAndTests;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.mygdx.game.MainGame;

public class BulletObjectTest {

	
	private float x=2,y=3;
	private float vx=4,vy=10;
	public Sprite sprite;

	public BulletObjectTest(Texture t, int i) {
		sprite = new Sprite(t);
		x=i%2560;
		y=(2*i)%1440;
		vx=i%10;
		vy=(i*8)%30;
	}

	public void update(float delta)
	{
		x += vx*delta;
		y += vy*delta;
		y=y%1440;
		x=x%2560;
		sprite.setPosition(x, y);
	}
}
