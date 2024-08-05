package desktop;


import java.awt.Color;
import java.awt.Font;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultListCellRenderer;
import javax.swing.UIManager;


/**
 * Renders custom fonts for cells in a {@code JComboBox}.
 *
 * @author Jonathan Uhler.
 */
public class FontRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Create JLabel that renders the font, if applicable
        float fontSize = 14;
        JLabel label = new JLabel();
        if (value instanceof String) {
            String fontName = (String) value;
            Font font = new Font(fontName, Font.PLAIN, (int) fontSize);
            label.setFont(font);
            label.setText(fontName);
        }
        else if (value instanceof Font) {
            Font font = (Font) value;
            label.setFont(font.deriveFont(fontSize));
            label.setText(font.getFontName());
        }
        else {
            String valueString = (value != null) ? value.toString() : "null";
            label.setText(valueString);
        }

        // Set cell focus properties
        label.setOpaque(true);
        if (isSelected) {
            Color selectionBackground = UIManager.getColor("ComboBox.selectionBackground");
            if (selectionBackground != null)
                label.setBackground(selectionBackground);

            Color selectionForeground = UIManager.getColor("ComboBox.selectionForeground");
            if (selectionForeground != null)
                label.setForeground(selectionForeground);
        }

        // Return label
        return label;
    }

}
