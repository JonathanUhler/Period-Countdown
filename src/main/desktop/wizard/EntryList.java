package desktop.wizard;


import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.JComponent;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Dimension;
import java.awt.Rectangle;


/**
 * A generic graphic list, similar to classes like {@code javax.swing.JList} that allows displaying
 * arbitrary {@code JComponent}s in its list items.
 *
 * The list supports iteration with an enhanced for-loop, but currently does not support the full
 * {@code List} interface.
 *
 * This class implements all basic user iteration, including:
 * - Scrolling if added to a {@code JScrollPane} or similar content pane
 * - Mouse actions to select list items
 * - Keyboard actions to delete list items
 *
 * @author Jonathan Uhler
 */
public abstract class EntryList<E extends JComponent>
    extends JPanel
    implements Iterable<E>, Scrollable, MouseListener, KeyListener
{

    /** The maximum height that an {@code EntryList} can have before scrolling in a content pane. */
    public static final int MAX_HEIGHT = 500;


    private boolean mutable;
    private int selectedIndex;
    private List<E> entries;
    private JButton addEntryButton;


    /**
     * Constructs a new mutable {@code EntryList}.
     */
    public EntryList() {
        this(true);
    }


    /**
     * Constructs a new {@code EntryList} with the specified mutability.
     *
     * @param mutable  whether graphical interations are able to modify this entry list. If false,
     *                 the list may still be mutated programmatically with {@code addEntry}, but
     *                 will not display the "+" button, and will disable keyboard and mouse inputs.
     */
    public EntryList(boolean mutable) {
        this.mutable = mutable;
        this.selectedIndex = -1;
        this.entries = new ArrayList<>();
        this.addEntryButton = new JButton("+");

        if (this.mutable) {
            this.addEntryButton.addActionListener(e -> this.addEntry(this.entryFactory()));
            this.add(this.addEntryButton);
        }

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.addMouseListener(this);
        this.addKeyListener(this);
    }


    /**
     * Adds a new entry to the end of this list.
     *
     * @param entry  the entry to add.
     */
    public void addEntry(E entry) {
        this.entries.add(entry);
        this.add(entry);

        this.revalidate();
        this.repaint();
    }


    /**
     * Constructs and returns a new, non-null entry of the type held by this list.
     *
     * This factory method must be implemented by all {@code EntryList} objects, as constructing
     * the generic type {@code E} is not legal. When the "+" button is pressed by the end-user,
     * {@code addEntry} will be called with the return value of this method.
     */
    public abstract E entryFactory();


    /**
     * Returns the iterator of the underlying {@code List} maintained by this entry list.
     *
     * @return the iterator for this entry list.
     */
    @Override
    public Iterator<E> iterator() {
        return this.entries.iterator();
    }


    /**
     * Returns the preferred size of this list when in a scrollable viewport.
     *
     * The width is set to the preferred width of the list (e.g. the width of the widest entry
     * added to the list), and the height is set to 500 pixels.
     *
     * @return the preferred size of this list when in a scrollable viewport.
     */
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        int width = Math.max(EntryList.MAX_HEIGHT, this.getPreferredSize().width);
        return new Dimension(width, EntryList.MAX_HEIGHT);
    }


    /**
     * Returns the pixel increment for scrolling.
     *
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
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 5;
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
    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
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
        return true;
    }
    
    
    /**
     * Returns {@code false} to indicate no preference for matching height with the parent
     * viewport, thus allowing vertical scrolling.
     *
     * @return {@code false}.
     */
    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }


    /**
     * Responds to mouse interactions on this editor list.
     *
     * When an item of this entry list is clicked, it will be highlighted, and future mouse or
     * keyboard events may then be applied to that entry.
     *
     * @param e  the mouse event.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (!this.mutable) {
            return;
        }

        E entry = (E) this.getComponentAt(e.getPoint());
        if (this.selectedIndex != -1) {
            this.entries.get(this.selectedIndex).setBackground(new Color(255, 255, 255));
        }
        if (entry == this) {
            return;
        }

        this.selectedIndex = this.entries.indexOf(entry);
        entry.setBackground(new Color(100, 100, 255));
        this.requestFocus();

        this.repaint();
        this.revalidate();
    }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }


    /**
     * Responds to keyboard interactions on this editor list.
     *
     * If an item of this entry list is selected, then it may be interacted with.
     *
     * @param e  the keyboard event.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (this.selectedIndex == -1 || !this.mutable) {
            return;
        }
        if (e.getKeyChar() != KeyEvent.VK_DELETE && e.getKeyChar() != KeyEvent.VK_BACK_SPACE) {
            return;
        }

        E removed = this.entries.remove(this.selectedIndex);
        this.remove(removed);
        this.selectedIndex = -1;

        this.repaint();
        this.revalidate();
    }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }
    
}
