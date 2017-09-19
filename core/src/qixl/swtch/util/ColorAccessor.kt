package qixl.swtch.util

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector3

import aurelienribon.tweenengine.TweenAccessor

/**
 * Created by aaron on 5/30/17.
 */

class ColorAccessor : TweenAccessor<Color> {

    override fun getValues(target: Color, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            TYPE_RGB -> {
                returnValues[0] = target.r
                returnValues[1] = target.g
                returnValues[2] = target.b
                return 3
            }
            else -> {
                assert(false)
                return -1
            }
        }
    }

    override fun setValues(target: Color, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            TYPE_RGB -> {
                target.r = newValues[0]
                target.g = newValues[1]
                target.b = newValues[2]
            }
            else -> assert(false)
        }
    }

    companion object {
        val TYPE_RGB = 1
    }
}
