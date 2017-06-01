package zyzxdev.swtch;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import aurelienribon.tweenengine.Tween;
import zyzxdev.swtch.util.ColorAccessor;
import zyzxdev.swtch.util.RectangleAccessor;
import zyzxdev.swtch.util.Util;

/**
 * Created by aaron on 5/30/17.
 */

public class Square {
	public Color color;
	public Rectangle touchRectangle, renderRectangle;
	public int colorID;
	public Tween tweenA, tweenB;

	public void setColor(int color) {
		this.colorID = color;
		this.color = new Color(Util.colors[colorID]);
	}

	public Tween setColorWithTween(int colorID){
		this.colorID = colorID;
		return Tween
			.to(color, ColorAccessor.TYPE_RGB, 0.2f)
			.target(Util.colors[colorID].r, Util.colors[colorID].g, Util.colors[colorID].b);
	}

	public Tween setSizeWithTween(float size, float time){
	return Tween
		.to(renderRectangle, RectangleAccessor.TYPE_SIZE, time)
		.target(size);
	}
}
