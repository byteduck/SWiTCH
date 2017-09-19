package qixl.swtch.util

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.GlyphLayout

import java.util.HashMap

/**
 * Created by aaron on 5/29/17.
 */

object FontLayoutManager {
    private val map = HashMap<StringAndFont, GlyphLayout>()

    fun getFontLayout(str: String, font: BitmapFont): GlyphLayout? {
        val saf = StringAndFont(str, font)
        if (!map.containsKey(saf)) {
            println("NEW GLYPHLAYOUT FOR STR " + str)
            val l = GlyphLayout()
            l.setText(font, str)
            map.put(saf, l)
            return l
        }
        return map[saf]
    }

    private class StringAndFont constructor(private val str: String, private val font: BitmapFont) {

        override fun hashCode(): Int {
            return str.hashCode() * font.hashCode()
        }

        override fun equals(o: Any?): Boolean {
            return o is StringAndFont && o.font === font && o.str == str
        }
    }
}
