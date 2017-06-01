package zyzxdev.swtch.util;

import com.badlogic.gdx.math.Vector3;

import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by aaron on 5/29/17.
 */

public class Vector3Accessor implements TweenAccessor<Vector3> {
	public static final int TYPE_XYZ = 1;

	@Override
	public int getValues(Vector3 target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case TYPE_XYZ:
				returnValues[0] = target.x;
				returnValues[1] = target.y;
				returnValues[2] = target.z;
				return 3;

			default:
				assert false;
				return -1;
		}
	}

	@Override
	public void setValues(Vector3 target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case TYPE_XYZ:
				target.x = newValues[0];
				target.y = newValues[1];
				target.z = newValues[2];
				break;
			default:
				assert false;
				break;
		}
	}
}