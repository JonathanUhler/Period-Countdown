// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GraphicsHelper.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/10/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class GraphicsHelper
//
// Helper methods that deal with graphics
//
public class GraphicsHelper {

    // ====================================================================================================
    // public static int getTextWidth
    //
    // Gets the width in pixels of a string in a given font style/size
    //
    // Arguments--
    //
    // font:    the font that the text is in
    //
    // text:    the text to get the size of
    //
    // Returns--
    //
    // The width in pixels of the text
    //
    public static int getTextWidth(Font font, String text) {
        // getStringBounds(String, FontRenderContext): Get the bounds of the string
        // | |
        // | +--> String: the text
        // +----> FontRenderContext: a builtin font renderer that defines the size and style of the font the
        //                           text is in
        return (int) (font.getStringBounds(text,
                new FontRenderContext(new AffineTransform(), true, true))
                .getWidth());
    }
    // end: public static int getTextWidth


    // ====================================================================================================
    // public static int getWidestElement
    //
    // Gets the widest element (in pixels) of a paragraph of text with line breaks
    //
    // Arguments--
    //
    // font:                the font the text is in
    //
    // text:                the text to get the width of the widest part from
    //
    // newLineCharacter:    the character that the next is split into multiple lines by (ex: "\n")
    //
    // Returns--
    //
    // longest:             the width of the widest element
    //
    public static int getWidestElement(Font font, String text, String newLineCharacter) {
        String[] textElements = text.split(newLineCharacter);
        int longest = 0;

        for (String textElem : textElements) {
            int width = (int) (font.getStringBounds(textElem,
                    new FontRenderContext(new AffineTransform(), true, true))
                    .getWidth());
            longest = Math.max(width, longest);
        }

        return longest;
    }
    // end: public static int getWidestElement


    // ====================================================================================================
    // public static Font fitTextIntoWidth
    //
    // Get the Font object that would fit a string into a given maximum width
    //
    // Arguments--
    //
    // font:        the font the text starts with
    //
    // text:        the string to fit to the width
    //
    // xOffset:     a buffer width. If there is no buffer/padding then set this to 0
    //
    // maxWidth:    the maximum width the text is allowed to fit into
    //
    // Returns--
    //
    // font:        the new font that will properly fit the text
    //
    public static Font fitTextIntoWidth(Font font, String text, int xOffset, int maxWidth) {
        // Use a loop to continue shrinking the text width until it fits within the provided maximum.
        // Every time through the loop create a new font with a slightly smaller text size until the width
        // of the text will fit. At that point, return a Font object using that text width
        int textWidth = maxWidth + 1;
        while (textWidth > maxWidth - xOffset) {
            textWidth = getTextWidth(font, text);
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        }
        return font;
    }
    // end: public static Font fitTextIntoWidth


    // ====================================================================================================
    // public static Font fitTextIntoWidth
    //
    // Get the Font object that would fit the widest element of a string into a given maximum width
    //
    // Arguments--
    //
    // font:                the font the text starts with
    //
    // text:                the string to fit to the width
    //
    // xOffset:             a buffer width. If there is no buffer/padding then set this to 0
    //
    // maxWidth:            the maximum width the text is allowed to fit into
    //
    // newLineCharacter:    the newline character that the text is split by
    //
    // Returns--
    //
    // font:                the new font that will properly fit the text
    //
    public static Font fitTextIntoWidth(Font font, String text, int xOffset, int maxWidth,
                                        String newLineCharacter) {
        // Complete the same process as the overloaded method fitTextIntoWidth(Font, String, int, int)
        // from above. With this instance of the method, the text can be split by a newline character
        // and the widest of the elements (widest line) will be the return of the method
        int textWidth = maxWidth + 1;
        while (textWidth > maxWidth - xOffset) {
            textWidth = getWidestElement(font, text, newLineCharacter);
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
        }

        return font;
    }
    // end: public static Font fitTextIntoWidth

}
// end: public class Graphicshelper