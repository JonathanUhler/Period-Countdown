package wizard.interfaces;


import wizard.WizardManager;
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


public abstract class EditorEntry extends JPanel {

	public static final String DELETE = "DELETE";
	public static final String MOVE_UP = "MOVE_UP";
	public static final String MOVE_DN = "MOVE_DN";


	private boolean mutable;
	private List<ActionListener> actionListeners;

	private GridBagConstraints gbc;
	private JButton upButton;
	private JButton downButton;
	private JButton delButton;


	public EditorEntry() {
		this(true);
	}


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


	public GridBagConstraints getLayoutConstraints() {
		this.gbc.gridy = 0;
		this.gbc.gridx = 1;
		this.gbc.gridwidth = 1;
		this.gbc.gridheight = 1;
		return this.gbc;
	}


	public void addActionListener(ActionListener l) {
		this.actionListeners.add(l);
	}


	public void removeActionListener(ActionListener l) {
		this.actionListeners.remove(l);
	}


	private void notifyActionListeners(String command) {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
		for (ActionListener l : this.actionListeners)
			l.actionPerformed(e);
	}


	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		int w = this.getSize().width;
		int h = this.getSize().height;
		g.setColor(new Color(0, 0, 0));
		g.drawRect(0, 0, w, h);
	}


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


	public abstract void preinitComponent();
	public abstract void initMutableComponent();
	public abstract void initImmutableComponent();
	public abstract Map<String, Object> collectFromMutableComponent();

}


/*
package wizard;


import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JPanel;


public abstract class EditorEntry extends JPanel {

	private boolean mutable;
	private boolean initialized;

	private GridBagConstraints gbc;
	private List<ActionListener> actionListeners;
	private JButton upButton;
	private JButton downButton;
	private JButton delButton;
	

	public EditorEntry(boolean mutable) {
		this.setLayout(new GridBagLayout());
		this.gbc = new GridBagConstraints();
		this.gbc.gridx = 0;
		this.gbc.gridy = 0;
		
		this.mutable = mutable;
		this.initialized = false;
		
		this.actionListeners = new ArrayList<>();

		this.upButton = new JButton("[/\\] ");
		this.delButton = new JButton("[-] ");
		this.downButton = new JButton("[\\/] ");

		this.upButton.setBorder(null);
		this.downButton.setBorder(null);
		this.delButton.setBorder(null);
		this.delButton.addActionListener(e -> this.delAction());

		this.add(this.upButton, this.gbc);
		this.gbc.gridy++;
		this.add(this.delButton, this.gbc);
		this.gbc.gridy++;
		this.add(this.downButton, this.gbc);
		this.gbc.gridy = 0;
		this.gbc.gridx++;

		this.preinitComponent();
		if (this.mutable)
			this.initMutableComponent();
		else
			this.initImmutableComponent();
		this.initialized = true;
	}


	public boolean isMutable() {
		return this.mutable;
	}


	public boolean isInitialized() {
		return this.initialized;
	}


	public void addActionListener(ActionListener l) {
		this.actionListeners.add(l);
	}


	public void removeActionListener(ActionListener l) {
		this.actionListeners.remove(l);
	}


	private void delAction() {
		ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
		for (ActionListener l : this.actionListeners)
			l.actionPerformed(e);
	}


	public void reinitComponent() {
		this.initialized = false;
		this.removeAll();
		this.preinitComponent();
		if (this.mutable)
			this.initMutableComponent();
		else
			this.initImmutableComponent();
		this.initialized = true;
		this.revalidate();
		this.repaint();
	}


	@Override
	public void paintComponent(Graphics g) {
		int w = this.getParent().getSize().width;
		int h = this.getSize().height;

		g.setColor(new Color(255, 255, 255));
		g.fillRect(0, 0, w, h);
		g.setColor(new Color(0, 0, 0));
		g.drawRect(0, 0, w, h);
	}


	public abstract Map<String, Object> collect();
	public abstract void populate(List<EditorEntry> l);
	public abstract void preinitComponent();
	public abstract void initMutableComponent();
	public abstract void initImmutableComponent();

}
*/
