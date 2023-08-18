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


/**
 * Stores a list of {@code EditorEntry}s that can be edited by the user.
 * <p>
 * The list should be added to a {@code JScrollPane} to allow an unknown number of entries to be
 * added while preserving the graphical integrity of the component.
 * <p>
 * The list adapts to the width of the widest entry added, and is fixed at 500 pixels in height.
 *
 * @author Jonathan Uhler
 */
public class EditorList<E extends EditorEntry>
	extends JPanel
	implements ActionListener, Scrollable
{

	/** The list of {@code EditorEntry}s. */
	private List<EditorEntry> entries;
	

	/**
	 * Constructs a new {@code EditorList} object.
	 */
	public EditorList() {
		this.entries = new ArrayList<>();
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	}


	/**
	 * Adds a new entry to this list.
	 *
	 * @param entry  the entry to add.
	 */
	public void addEntry(EditorEntry entry) {
		entry.addActionListener(this);

		this.entries.add(entry);
		this.add(entry);
		
		this.revalidate();
		this.repaint();
	}


	/**
	 * Returns all the entries in this list.
	 *
	 * @return all the entries in this list.
	 */
	public List<EditorEntry> getEntries() {
		return this.entries;
	}


	/**
	 * Readds all the entries in the internal {@code List} to the graphical context after
	 * the position of one of the entries changes.
	 */
	private void orderChanged() {
		this.removeAll();

		for (EditorEntry entry : this.entries)
			this.add(entry);
	}


	/**
	 * Performs an {@code EditorEntry} action by either repositioning or removing an entry from
	 * this list.
	 *
	 * @param e  the event describing the action that was performed. The command string of the
	 *           event should be one of the string opcodes of the {@code EditorEntry} class.
	 */
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


	/**
	 * Returns the preferred size of this list when in a scrollable viewport.
	 * <p>
	 * The width is set to the preferred width of the list (e.g. the width of the widest entry
	 * added to the list), and the height is set to 500 pixels.
	 *
	 * @return the preferred size of this list when in a scrollable viewport.
	 */
	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return new Dimension(this.getPreferredSize().width, 500);
	}


	/**
	 * Returns the pixel increment for scrolling.
	 * <p>
	 * Only vertical scrolling is allowed by an editor list. The value {@code 1} is always
	 * returned if {@code orientation == SwingConstants.HORIZONTAL}.
	 *
	 * @param visibleRect  the view area visible within the viewport.
	 * @param orientation  either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction    less than zero to scroll up/left, greater than zero for down/right.
	 *
	 * @return the "unit" increment for scrolling in the specified direction. This value will
	 *         always be positive.
	 */
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

	/**
	 * Returns the value of {@code getScrollableUnitIncrement(visibleRect, orientation, direction)}.
	 *
	 * @param visibleRect  the view area visible within the viewport.
	 * @param orientation  either SwingConstants.VERTICAL or SwingConstants.HORIZONTAL.
	 * @param direction    less than zero to scroll up/left, greater than zero for down/right.
	 *
	 * @return the "unit" increment for scrolling in the specified direction. This value will
	 *         always be positive.
	 *
	 * @see getScrollableUnitIncrement
	 */
	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
										   int orientation,
										   int direction)
	{
		return this.getScrollableUnitIncrement(visibleRect, orientation, direction);
	}


	/**
	 * Returns {@code true} to request that the editor list matches the width of the parent
	 * viewport, thus disabling horizontal scrolling.
	 *
	 * @return {@code true}.
	 */
	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true; // Disable horizontal scrolling functionality for JScrollPane
	}


	/**
	 * Returns {@code false} to indicate no preference for matching height with the parent
	 * viewport, thus allowing vertical scrolling.
	 *
	 * @return {@code false}.
	 */
	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false; // Force vertical scrolling functionality for JScrollPane
	}

}
