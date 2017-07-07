package zyzxdev.swtch.util

import aurelienribon.tweenengine.BaseTween
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera

import java.util.HashMap
import java.util.Random

import aurelienribon.tweenengine.Tween
import aurelienribon.tweenengine.TweenEquation
import aurelienribon.tweenengine.TweenManager

/**
 * Created by aaron on 5/29/17.
 */

object Util {
    enum class Direction {
        UP, LEFT, RIGHT, DOWN, CENTER
    }

    var rand = Random()

    val colorChanges = arrayOf(intArrayOf(1, 2), intArrayOf(0, 2), intArrayOf(0, 1))
    val reverseColorChanges = HashMap<String, Int>()
    val colors = arrayOf(Color(0xAA3333FF.toInt()), Color(0x33AA33FF), Color(0x3333AAFF))

    init {
        reverseColorChanges.put("0,1", 2)
        reverseColorChanges.put("1,0", 2)
        reverseColorChanges.put("0,2", 1)
        reverseColorChanges.put("2,0", 1)
        reverseColorChanges.put("1,2", 0)
        reverseColorChanges.put("2,1", 0)
    }

    fun slideCamera(cam: OrthographicCamera, dir: Direction, manager: TweenManager, widthMeasurement: Float, heightMeasurement: Float, ease: TweenEquation, delay: Float, callback: ((Int, BaseTween<*>) -> Unit)?): Tween {
        val t = Tween.to(cam.position, Vector3Accessor.TYPE_XYZ, 0.5f).ease(ease).delay(delay)
        if (callback != null)
            t.setCallback(callback)
        when (dir) {
            Util.Direction.RIGHT -> t.target(-widthMeasurement / 2, heightMeasurement / 2, 0f)
            Util.Direction.LEFT -> t.target(widthMeasurement * 1.5f, heightMeasurement / 2, 0f)
            Util.Direction.CENTER -> t.target(widthMeasurement / 2, heightMeasurement / 2, 0f)
            else -> t.target(widthMeasurement / 2, heightMeasurement / 2, 0f)
        }
        return t.start(manager)
    }
}
