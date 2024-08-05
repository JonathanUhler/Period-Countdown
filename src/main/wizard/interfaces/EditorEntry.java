package wizard.interfaces;


import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Graphics;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import wizard.WizardManager;


/**
 * Stores information that can be edited or viewed graphically about a particular type of data
 * used by the generator.
 *
 * @author Jonathan Uhler
 */
public abstract class EditorEntry extends JPanel {
    
    /** Command opcode for an {@code EditorEntry} to be deleted by its parent list. */
    public static final String DELETE = "DELETE";
    /** Command opcode for an {@code EditorEntry} to be moved up in its parent list. */
    public static final String MOVE_UP = "MOVE_UP";
    /** Command opcode for an {@code EditorEntry} to be moved down in its parent list. */
    public static final String MOVE_DN = "MOVE_DN";
    
    
    /** Whether this entry is mutable. */
    private boolean mutable;
    /** Listeners of this entry that are notified when a movement/delete command is created. */
    private List<ActionListener> actionListeners;
    
    /** Layout constraints used by this entry. */
    private GridBagConstraints gbc;
    /** A button to request that this entry be moved up in its parent list. */
    private JButton upButton;
    /** A button to request that this entry be moved down in its parent list. */
    private JButton downButton;
    /** A button to request that this entry be removed from its parent list. */
    private JButton delButton;
    
    
    /**
     * Constructs a new mutable {@code EditorEntry}.
     * <p>
     * Mutable entries allow the user to define information for an entry of a specific type.
     * Immutable entries allow the user to choose from one of the previously defined entries
     * of that type, which then absorbs the information used to define that mutable entry.
     * <p>
     * Each editor entry has a 3-button control panel on the left to move it up or down in its
     * parent list, or remove it entirely from the parent list.
     */
    public EditorEntry() {
        this(true);
    }
    
    
    /**
     * Constructs a new {@code EditorEntry} with the specified mutability.
     * <p>
     * Mutable entries allow the user to define information for an entry of a specific type.
     * Immutable entries allow the user to choose from one of the previously defined entries
     * of that type, which then absorbs the information used to define that mutable entry.
     * <p>
     * Each editor entry has a 3-button control panel on the left to move it up or down in its
     * parent list, or remove it entirely from the parent list.
     *
     * @param mutable  whether this editor entry is mutable.
     */
    public EditorEntry(boolean mutable) {
        this.mutable = mutable;
        this.actionListeners = new ArrayList<>();
        
        this.setBorder(new EmptyBorder(10, 5, 10, 5));
        
        this.setLayout(new GridBagLayout());
        this.gbc = new GridBagConstraints();
        this.upButton = new JButton("[/\\] ");
        this.delButton = new JButton("[-] ");
        this.downButton = new JButton("[\\/] ");
        
        this.upButton.setToolTipText("Move entry up");
        this.downButton.setToolTipText("Move entry down");
        this.delButton.setToolTipText("Delete entry");
        this.upButton.setBorder(null);
        this.downButton.setBorder(null);
        this.delButton.setBorder(null);
        this.upButton.addActionListener(e -> this.notifyActionListeners(EditorEntry.MOVE_UP));
        this.downButton.addActionListener(e -> this.notifyActionListeners(EditorEntry.MOVE_DN));
        this.delButton.addActionListener(e -> this.notifyActionListeners(EditorEntry.DELETE));
        
        WizardManager.registerEditorEntry(this);
        
        this.initComponentControls();
        this.preinitComponent();
        if (this.mutable)
            this.initMutableComponent();
        else
            this.initImmutableComponent();
    }
    
    
    /**
     * Returns the layout constraints used by this entry. Upon being returned, the constraints
     * point to the vertical center of the entry to the right of the control panel.
     *
     * @return the layout constraints used by this entry.
     */
    public GridBagConstraints getLayoutConstraints() {
        this.gbc.gridy = 0;
        this.gbc.gridx = 1;
        this.gbc.gridwidth = 1;
        this.gbc.gridheight = 1;
        return this.gbc;
    }
    
    
    /**
     * Adds an action listener to this entry. Action listeners are notified when a button on
     * the control panel is pressed. The only required action listener is the parent
     * {@code EditorList} of this entry.
     *
     * @param l  the action listener to add.
     */
    public void addActionListener(ActionListener l) {
        this.actionListeners.add(l);
    }
    
    
    /**
     * Removes an action listener to this entry.
     *
     * @param l  the action listener to remove.
     */
    public void removeActionListener(ActionListener l) {
        this.actionListeners.remove(l);
    }
    
    
    /**
     * Notifies all action listeners of a control panel action.
     *
     * @param command  the command to send, which should be one of the opcode strings defined
     *                 in this class.
     */
    private void notifyActionListeners(String command) {
        ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
        for (ActionListener l : this.actionListeners)
            l.actionPerformed(e);
    }
    
    
    /**
     * Paints the graphical context of this entry. This method is responsible for painting
     * a black border around the child components of the entry.
     *
     * @param g  the {@code Graphics} object to paint with.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = this.getSize().width;
        int h = this.getSize().height;
        g.setColor(new Color(0, 0, 0));
        g.drawRect(0, 0, w, h);
    }
    
    
    /**
     * Reinitializes this entry.
     * <p>
     * The reinitialization process involes removing all existing components, initializing the 
     * control panel, and initializing the mutable or immutable state of the component. The 
     * {@code preinitComponent} method is only ever called once by the {@code EditorEntry} 
     * constructor.
     */
    public void reinitComponent() {
        this.removeAll();
        this.initComponentControls();
        if (this.mutable)
            this.initMutableComponent();
        else
            this.initImmutableComponent();
        this.revalidate();
        this.repaint();
    }
    
    
    /**
     * Displays the control panel for this entry.
     * <p>
     * The controls includes buttons to move the entry up or down in its parent list and a button
     * to remove the entry from its parent list. This method should only be called after all
     * components are removed from the entry. The strongly recommended way to reinitialize the
     * controls is through {@code reinitComponent}, which automatically calls
     * {@code initComponentControls} as part of the reinit process.
     */
    public void initComponentControls() {
        this.gbc = this.getLayoutConstraints(); // This method does some normaliziation of gbc
        this.gbc.gridx = 0;
        this.add(this.upButton, this.gbc);
        
        this.gbc.gridy++;
        this.add(this.delButton, this.gbc);
        
        this.gbc.gridy++;
        this.add(this.downButton, this.gbc);
        
        this.gbc.gridx++;
        this.gbc.gridy = 0;
	
        this.revalidate();
        this.repaint();
    }
    
    
    /**
     * Performs final steps of the construction of a specific {@code EditorEntry} class.
     * <p>
     * This method is only ever called once at the end of the {@code EditorEntry} constructor, 
     * after the controls are initialized, but before the component is initialized as mutable or 
     * immutable.
     * <p>
     * The intent of this method is to initialize instance variables and perform other one-time
     * setup that should either a) not be repeated, or b) must be performed by both the
     * {@code initMutableComponent} and {@code initImmutableComponent} methods (thus reducing
     * code duplication).
     * <p>
     * This method should <b>not</b> call any other initialization methods of the
     * {@code EditorEntry} calss. The {@code initComponentControls}, {@code initMutableComponent},
     * and {@code initImmutableComponent} methods are called automatically by the
     * {@code EditorEntry} constructor or {@code reinitComponent} method.
     */
    public abstract void preinitComponent();
    
    /**
     * Initializes the graphical context of a mutable instance of a specific {@code EditorEntry}
     * class.
     * <p>
     * This method is called during the construction and reinit routines of {@code EditorEntry}
     * after the component controls have been added.
     * <p>
     * Components and instance variables used by this method should be initialized in the
     * {@code preinitComponent} method to avoid unwanted repeated initializations.
     * <p>
     * If the specific {@code EditorEntry} child does not have a mutable state, this method
     * can be left with no logic in its body.
     */
    public abstract void initMutableComponent();
    
    /**
     * Initializes the graphical context of an immutable instance of a specific {@code EditorEntry}
     * class.
     * <p>
     * This method is called during the construction and reinit routines of {@code EditorEntry}
     * after the component controls have been added.
     * <p>
     * Components and instance variables used by this method should be initialized in the
     * {@code preinitComponent} method to avoid unwanted repeated initializations.
     * <p>
     * If the specific {@code EditorEntry} child does not have a mutable state, this method
     * can be left with no logic in its body.
     */
    public abstract void initImmutableComponent();
    
    /**
     * Collects and returns all relevant data associated with a specific {@code EditorEntry}
     * class.
     * <p>
     * The data should be returned as a hashmap, where the keys are strings that represent
     * the data being returned, and the values are arbitrary objects holding the contents
     * of the returned data.
     * <p>
     * This method should <b>not</b> return {@code null}. If the specific {@code EditorEntry}
     * class does not have any data associated with it, return an empty map (e.g.
     * {@code return new HashMap<>();}).
     *
     * @return a map of all relevant data associated with a specific {@code EditorEntry} class.
     */
    public abstract Map<String, Object> collectFromMutableComponent();
    
}
