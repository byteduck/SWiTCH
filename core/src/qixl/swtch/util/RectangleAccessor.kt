package qixl.swtch.util

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2

import aurelienribon.tweenengine.TweenAccessor

/**
 * Created by aaron on 5/30/17.
 */

class RectangleAccessor : TweenAccessor<Rectangle> {

    override fun getValues(target: Rectangle, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            TYPE_XYWH -> {
                returnValues[0] = target.x
                returnValues[1] = target.y
                returnValues[2] = target.width
                returnValues[3] = target.height
                return 4
            }
            TYPE_SIZE -> {
                returnValues[0] = target.width
                return 1
            }
            else -> {
                assert(false)
                return -1
            }
        }
    }

    override fun setValues(target: Rectangle, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            TYPE_XYWH -> {
                target.x = newValues[0]
                target.y = newValues[1]
                target.width = newValues[2]
                target.height = newValues[3]
            }
            TYPE_SIZE -> {
                target.width = newValues[0]
                target.height = newValues[0]
            }
            else -> assert(false)
        }
    }

    companion object {
        val TYPE_XYWH = 1
        val TYPE_SIZE = 2
    }
}
