// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GraphicsHelper.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/10/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// GraphicsHelper.java
// Class Diagram
/*

+-----------------------------------------------------------------------------------------------------+
|                                                GraphicsHelper                                       |
+-----------------------------------------------------------------------------------------------------+
| +getTextWidth(Font, String): int                                                                    |
| +getWidestElement(Font, String, String): int                                                        |
| +fitTextIntoWidth(Font, String, int, int): Font                                                     |
| +fitTextIntoWidth(Font, String, int, int, String): Font                                             |
+-----------------------------------------------------------------------------------------------------+

*/
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
        // +----> FontRenderContext: a builtin font renderer that defines the size and style of the font the text is in
        return (int) (font.getStringBounds(text, new FontRenderContext(new AffineTransform(), true, true)).getWidth());
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
        String[] textElements = text.split(newLineCharacter); // Split the text into its elements by the newline char
        int longest = 0; // Initialize a variable to hold the size of the widest element

        for (String textElem : textElements) {
            int width = (int) (font.getStringBounds(textElem, new FontRenderContext(new AffineTransform(), true, true)).getWidth()); // Get the width of the element
            longest = Math.max(width, longest); // Set the longest to the max between itself and the element just analyzed
        }

        return longest; // Return the longest element
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
        int textWidth = maxWidth + 1; // Initial text width

        // Loop forever until the text width is within the correct scale
        while (textWidth > maxWidth - xOffset) {
            textWidth = getTextWidth(font, text); // Update the text width
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1); // Update the width of the font object
        }

        return font; // Return the edited font object
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
    public static Font fitTextIntoWidth(Font font, String text, int xOffset, int maxWidth, String newLineCharacter) {
        int textWidth = maxWidth + 1; // Initial text width

        // Loop forever until the text width is within the correct scale
        while (textWidth > maxWidth - xOffset) {
            textWidth = getWidestElement(font, text, newLineCharacter); // Update the text width
            font = new Font(font.getName(), font.getStyle(), font.getSize() - 1); // Update the width of the font object
        }

        return font; // Return the edited font object
    }
    // end: public static Font fitTextIntoWidth

}
// end: public class Graphicshelper