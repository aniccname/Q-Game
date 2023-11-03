package Map.Tile;

import java.awt.Color;
import java.awt.image.LookupTable;

public class ColorMapperNonWhite extends LookupTable {
	private final Color targetColor;

	public ColorMapperNonWhite(Color targetColor) {
		// 0 means no offset
		// 4 means 4 components: red, green, blue, alpha
		super(0, 3);
		this.targetColor = targetColor;
	}

	@Override
	public int[] lookupPixel(int[] src, int[] dest) {
		if (dest == null) {
			dest = new int[src.length];
		}
		if (src[0] != 255 || src[1] != 255 || src[2] != 255) {
			dest[0] = targetColor.getRed();
			dest[1] = targetColor.getGreen();
			dest[2] = targetColor.getBlue();
		} else {
			dest[0] = src[0];
			dest[1] = src[1];
			dest[2] = src[2];
		}
		return dest;
	}
}
