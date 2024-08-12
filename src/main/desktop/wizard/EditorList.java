package desktop.wizard;


import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
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


public class EditorList<E extends JComponent>
    extends JPanel
    implements Iterable<E>, Scrollable, MouseListener, KeyListener
{

    public static final int MAX_HEIGHT = 500;


    private int selectedIndex;
    private List<E> entries;


    public EditorList() {
        this.selectedIndex = -1;
        this.entries = new ArrayList<>();

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        this.addMouseListener(this);
        this.addKeyListener(this);
    }


    public void addEntry(E entry) {
        this.entries.add(entry);
        this.add(entry);

        this.revalidate();
        this.repaint();
    }


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
        int width = Math.max(EditorList.MAX_HEIGHT, this.getPreferredSize().width);
        return new Dimension(width, EditorList.MAX_HEIGHT);
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

    @Override
    public void mousePressed(MouseEvent e) {
        E entry = (E) this.getComponentAt(e.getPoint());
        if (this.selectedIndex != -1) {
            this.entries.get(this.selectedIndex).setBackground(new Color(255, 255, 255));
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


    @Override
    public void keyPressed(KeyEvent e) {
        if (this.selectedIndex == -1) {
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
