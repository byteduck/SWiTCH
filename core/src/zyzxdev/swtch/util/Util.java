package zyzxdev.swtch.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashMap;
import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by aaron on 5/29/17.
 */

public class Util{
	public enum Direction{
		UP, LEFT, RIGHT, DOWN, CENTER
	}
	public static Random rand = new Random();

	public static final int[][] colorChanges = {{1,2},{0,2},{0,1}};
	public static final HashMap<String, Integer> reverseColorChanges = new HashMap<String, Integer>();
	public static final Color[] colors = {new Color(0xAA3333FF), new Color(0x33AA33FF), new Color(0x3333AAFF)};
	static{
		reverseColorChanges.put("0,1", 2);
		reverseColorChanges.put("1,0", 2);
		reverseColorChanges.put("0,2", 1);
		reverseColorChanges.put("2,0", 1);
		reverseColorChanges.put("1,2", 0);
		reverseColorChanges.put("2,1", 0);
	}

	public static Tween slideCamera(OrthographicCamera cam, Direction dir, TweenManager manager, float widthMeasurement, float heightMeasurement, TweenEquation ease, float delay, TweenCallback callback){
		Tween t = Tween.to(cam.position, Vector3Accessor.TYPE_XYZ, 0.5f).ease(ease).delay(delay);
		if(callback != null)
			t.setCallback(callback);
		switch(dir){
			case RIGHT:
				t.target(-widthMeasurement/2, heightMeasurement/2, 0);
				break;
			case LEFT:
				t.target(widthMeasurement*1.5f, heightMeasurement/2, 0);
				break;
			case CENTER:
			default:
				t.target(widthMeasurement/2, heightMeasurement/2, 0);
		}
		return t.start(manager);
	}
}
