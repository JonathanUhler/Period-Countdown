package wizard.editor;


import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;


/**
 * Graphical viewer for the final JSON output.
 *
 * @author Jonathan Uhler
 */
public class JSONViewer extends JPanel {

    /** Data view form for the JSON output or any error messages. */
    private JTextArea jsonTextArea;


    /**
     * Constructs a new {@code JSONViewer} object.
     */
    public JSONViewer() {
        this.setLayout(new GridBagLayout());

        this.jsonTextArea = new JTextArea("", 45, 60);
        this.jsonTextArea.setEditable(false);

        GridBagConstraints gbc = new GridBagConstraints();
		
        gbc.gridx = 0;
        gbc.gridy = 0;
        JScrollPane jsonScrollPane = new JScrollPane(this.jsonTextArea);
        jsonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jsonScrollPane, gbc);
    }


    /**
     * Updates the contents of the JSON viewer.
     *
     * @param json  the new contents to display in the viewer.
     */
    public void setJson(String json) {
        this.jsonTextArea.setText(json);
    }

}
