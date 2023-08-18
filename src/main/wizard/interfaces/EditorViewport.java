package wizard.interfaces;


import java.util.List;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;


public abstract class EditorViewport<E extends EditorEntry> extends JPanel {

	private boolean mutable;
	private EditorList<E> list;
	private JButton addEntryButton;


	public EditorViewport() {
		this(true);
	}


	public EditorViewport(boolean mutable) {
		this.setLayout(new GridBagLayout());
		
		this.mutable = mutable;
		this.list = new EditorList<>();
		this.addEntryButton = new JButton("New Entry");
		
		this.addEntryButton.addActionListener(e -> this.addManualEntry(this.entryFactory(mutable)));

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.gridx = 0;
		gbc.gridy = 0;
		this.add(new JScrollPane(this.list), gbc);

		gbc.gridy++;
		this.add(this.addEntryButton, gbc);
	}


	public void addManualEntry(EditorEntry entry) {
		this.list.addEntry(entry);
		this.list.revalidate();
		this.list.repaint();
		this.revalidate();
		this.repaint();
	}


	public EditorList<E> getEditorList() {
		return this.list;
	}


	public abstract E entryFactory(boolean mutable);

}
