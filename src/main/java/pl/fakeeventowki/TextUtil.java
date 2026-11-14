package pl.fakeeventowki;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/** Zamienia kody w stylu "&4&lTekst" na Text z kolorami i formatowaniem. */
public final class TextUtil {
	private TextUtil() {}

	public static MutableText parse(String raw) {
		MutableText root = Text.empty().setStyle(Style.EMPTY.withItalic(false));
		Style style = Style.EMPTY;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < raw.length(); i++) {
			char c = raw.charAt(i);
			if (c == '&' && i + 1 < raw.length()) {
				Formatting f = Formatting.byCode(raw.charAt(i + 1));
				if (f != null) {
					flush(root, sb, style);
					if (f == Formatting.RESET) {
						style = Style.EMPTY;
					} else if (f.isColor()) {
						style = Style.EMPTY.withColor(f);
					} else {
						style = style.withFormatting(f);
					}
					i++;
					continue;
				}
			}
			sb.append(c);
		}
		flush(root, sb, style);
		return root;
	}

	private static void flush(MutableText root, StringBuilder sb, Style style) {
		if (sb.length() > 0) {
			root.append(Text.literal(sb.toString()).setStyle(style));
			sb.setLength(0);
		}
	}
}
