package zyzxdev.swtch.util;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

import java.util.HashMap;

/**
 * Created by aaron on 5/29/17.
 */

public class FontLayoutManager{
	private static HashMap<StringAndFont, GlyphLayout> map = new HashMap<StringAndFont, GlyphLayout>();

	public static GlyphLayout getFontLayout(String str, BitmapFont font){
		StringAndFont saf = new StringAndFont(str, font);
		if(!map.containsKey(saf)) {
			System.out.println("NEW GLYPHLAYOUT FOR STR "+str);
			GlyphLayout l = new GlyphLayout();
			l.setText(font, str);
			map.put(saf, l);
			return l;
		}
		return map.get(saf);
	}

	private static class StringAndFont{
		private String str;
		private BitmapFont font;

		private StringAndFont(String str, BitmapFont font){
			this.str = str;
			this.font = font;
		}

		@Override
		public int hashCode(){
			return str.hashCode()*font.hashCode();
		}

		public boolean equals(Object o){
			return o instanceof StringAndFont && ((StringAndFont) o).font == font && ((StringAndFont) o).str.equals(str);
		}
	}
}
