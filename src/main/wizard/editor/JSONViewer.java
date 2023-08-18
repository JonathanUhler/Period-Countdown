package wizard.editor;


import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;


public class JSONViewer extends JPanel {

	private JTextArea jsonTextArea;


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


	public void setJson(String json) {
		this.jsonTextArea.setText(json);
	}

}
