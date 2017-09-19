package qixl.swtch.util

import com.badlogic.gdx.math.Vector2

import aurelienribon.tweenengine.TweenAccessor

/**
 * Created by aaron on 5/29/17.
 */

class Vector2Accessor : TweenAccessor<Vector2> {

    override fun getValues(target: Vector2, tweenType: Int, returnValues: FloatArray): Int {
        when (tweenType) {
            TYPE_XY -> {
                returnValues[0] = target.x
                returnValues[1] = target.y
                return 2
            }

            else -> {
                assert(false)
                return -1
            }
        }
    }

    override fun setValues(target: Vector2, tweenType: Int, newValues: FloatArray) {
        when (tweenType) {
            TYPE_XY -> {
                target.x = newValues[0]
                target.y = newValues[1]
            }
            else -> assert(false)
        }
    }

    companion object {
        val TYPE_XY = 1
    }
}