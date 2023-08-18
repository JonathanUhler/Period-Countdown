package wizard.interfaces;


import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;


public class EditorList<E extends EditorEntry>
	extends JPanel
	implements ActionListener, Scrollable
{

	private List<EditorEntry> entries;
	

	public EditorList() {
		this.entries = new ArrayList<>();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}


	public void addEntry(EditorEntry entry) {
		entry.addActionListener(this);

		this.entries.add(entry);
		this.add(entry);
		
		this.revalidate();
		this.repaint();
	}


	public List<EditorEntry> getEntries() {
		return this.entries;
	}


	private void orderChanged() {
		this.removeAll();

		for (EditorEntry entry : this.entries)
			this.add(entry);
	}


	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		E source = (E) e.getSource();
		String command = e.getActionCommand();
		switch (command) {
		case EditorEntry.DELETE -> {
			this.entries.remove(source);
			this.remove(source);
		}
		case EditorEntry.MOVE_UP -> {
			int i = this.entries.indexOf(source);
			if (i > 0)
				Collections.swap(this.entries, i, i - 1);
			this.orderChanged();
		}
		case EditorEntry.MOVE_DN -> {
			int i = this.entries.indexOf(source);
			if (i < this.entries.size() - 1)
				Collections.swap(this.entries, i, i + 1);
			this.orderChanged();
		}
		}
		
		this.revalidate();
		this.repaint();
	}


	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(this.getPreferredSize().width, 500);
	}


	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
										  int orientation,
										  int direction)
	{
		if (orientation == SwingConstants.HORIZONTAL)
			return 1;

		int height = Integer.MAX_VALUE;

		for (EditorEntry entry : this.entries) {
			Dimension entrySize = entry.getSize();
			if (entrySize.height < height)
				height = entrySize.height;
		}

		return height / 4;
	}

	
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
										   int orientation,
										   int direction)
	{
		return this.getScrollableUnitIncrement(visibleRect, orientation, direction);
	}


	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true; // Disable horizontal scrolling functionality for JScrollPane
	}


	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false; // Force vertical scrolling functionality for JScrollPane
	}

}


/*
package wizard;


import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JButton;


public abstract class Editor<E extends EditorEntry> extends JPanel implements ActionListener {

	private boolean mutable;
	private List<EditorEntry> elements;
	private List<EditorEntry> populateData;

	private JPanel panel;
	private JButton addButton;
	

	public Editor(int height) {
		this.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;

		this.mutable = true;
		this.elements = new ArrayList<>();
		this.populateData = new ArrayList<>();
		this.panel = new JPanel();
		this.addButton = new JButton("New Entry");

		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		this.addButton.addActionListener(e -> this.addAction());

		JScrollPane scrollPane = new JScrollPane(this.panel,
												 JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
												 JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(Wizard.WIDTH - 25, height));
		this.add(scrollPane, gbc);
		gbc.gridy++;
		this.add(addButton, gbc);
	}


	public abstract E newEntry(boolean mutable);


	private void addAction() {
		E element = this.newEntry(this.mutable);
		element.addActionListener(this);
		element.populate(this.populateData);
		
		this.elements.add(element);
		this.panel.add(element);
		
		this.revalidate();
		this.repaint();
	}


	public List<EditorEntry> collect() {
		return this.elements;
	}


	public void populate(List<EditorEntry> l) {
		this.populateData = l;
		for (EditorEntry element : this.elements)
			element.populate(l);
	}


	public void setMutable(boolean mutable) {
		this.mutable = mutable;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e) {
		E source = (E) e.getSource();
		this.elements.remove(source);
		this.panel.remove(source);
		this.revalidate();
		this.repaint();
	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Wizard.WIDTH, Wizard.HEIGHT);
	}

}
*/
