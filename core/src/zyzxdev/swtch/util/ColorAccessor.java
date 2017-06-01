package zyzxdev.swtch.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by aaron on 5/30/17.
 */

public class ColorAccessor implements TweenAccessor<Color>{
	public static final int TYPE_RGB = 1;

	@Override
	public int getValues(Color target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case TYPE_RGB:
				returnValues[0] = target.r;
				returnValues[1] = target.g;
				returnValues[2] = target.b;
				return 3;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Color target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case TYPE_RGB:
				target.r = newValues[0];
				target.g = newValues[1];
				target.b = newValues[2];
				break;
			default:
				assert false;
				break;
		}
	}
}
