package zyzxdev.swtch.util;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by aaron on 5/30/17.
 */

public class RectangleAccessor implements TweenAccessor<Rectangle> {
	public static final int TYPE_XYWH = 1, TYPE_SIZE = 2;

	@Override
	public int getValues(Rectangle target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case TYPE_XYWH:
				returnValues[0] = target.x;
				returnValues[1] = target.y;
				returnValues[2] = target.width;
				returnValues[3] = target.height;
				return 4;
			case TYPE_SIZE:
				returnValues[0] = target.width;
				return 1;
			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Rectangle target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case TYPE_XYWH:
				target.x = newValues[0];
				target.y = newValues[1];
				target.width = newValues[2];
				target.height = newValues[3];
				break;
			case TYPE_SIZE:
				target.width = newValues[0];
				target.height = newValues[0];
				break;
			default:
				assert false;
				break;
		}
	}
}
