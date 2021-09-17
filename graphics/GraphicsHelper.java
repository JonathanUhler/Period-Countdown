import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;


public class GraphicsHelper {

    public static int getTextWidth(Font font, String text) {
        return (int) (font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true)).getWidth());
    }


    public static int getWidestElement(Font font, String text, String newLineCharacter) {
        String[] textElements = text.split(newLineCharacter);
        int longest = 0;

        for (String textElem : textElements) {
            int width = (int) (font.getStringBounds(textElem, new FontRenderContext(new AffineTransform(), true, true)).getWidth());
            longest = Math.max(width, longest);
        }

        return longest;
    }


    public static Font fitTextIntoWidth(Font font, String text, int xOffset, int maxWidth) {
        int textWidth = maxWidth + 1;

        while (textWidth > maxWidth - xOffset) {
            textWidth = getTextWidth(font, text);
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        }

        return font;
    }


    public static Font fitTextIntoWidth(Font font, String text, int xOffset, int maxWidth, String newLineCharacter) {
        int textWidth = maxWidth + 1;

        while (textWidth > maxWidth - xOffset) {
            textWidth = getWidestElement(font, text, newLineCharacter);
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        }

        return font;
    }

}
