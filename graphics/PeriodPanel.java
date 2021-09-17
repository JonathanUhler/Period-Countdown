// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PeriodPanel.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/9/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Calendar;


// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// public class PeriodPanel extends JPanel
//
// Class containing the contents of the Period Countdown app
//
public class PeriodPanel extends JPanel {

    private final int width; // Panel width
    private final int height; // Panel height

    private final JLabel periodStatus; // Label holding the status of the period
    private final JLabel periodTime; // Label holding the time left in the period
    private final JLabel nextUp; // Label holding the list of periods up next

    private SchoolDisplay schoolDisplay; // Class SchoolDisplay object


    // ----------------------------------------------------------------------------------------------------
    // public PeriodPanel
    //
    // PeriodPanel constructor
    //
    // Arguments--
    //
    // width:   width of the panel
    //
    // height:  height of the panel
    //
    public PeriodPanel(int width, int height) throws Exception {
        // Set the dimensions of the panel
        this.width = width;
        this.height = height;

        // Initialize the information labels
        this.periodStatus = new JLabel();
        this.periodTime = new JLabel();
        this.nextUp = new JLabel();

        // Initialize the school display object
        this.schoolDisplay = new SchoolDisplay();

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Set the layout manager of the app
        // Set the borders of each label
        this.periodStatus.setBorder(new EmptyBorder(10, 15, 0, 0));
        this.periodTime.setBorder(new EmptyBorder(0, 15, 0, 0));
        this.nextUp.setBorder(new EmptyBorder(0, 15, 0, 0));
    }
    // end: public PeriodPanel


    // ====================================================================================================
    // public Dimension getPreferredSize
    //
    // Return the size of the panel
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // The size of the panel as a Dimension object
    //
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(this.width, this.height);
    }
    // end: public Dimension getPreferredSize


    // ====================================================================================================
    // public void paintComponent
    //
    // Paint the components of the panel
    //
    // Arguments--
    //
    // g:   the Graphics object to use to paint
    //
    // Returns--
    //
    // None
    //
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Add all of the labels
        this.add(this.periodStatus);
        this.add(this.periodTime);
        this.add(this.nextUp);
    }
    // end: public void paintComponent


    // ====================================================================================================
    // public void animate
    //
    // Animate the labels
    //
    // Arguments--
    //
    // None
    //
    // Returns--
    //
    // None
    //
    public void animate() throws Exception {
        // Loop the animation forever
        while (true) {
            Calendar currentDateTime = Calendar.getInstance(); // Get the current time and date

            this.setSize(this.getParent().getSize()); // Set the size of the panel

            this.periodStatus.setText(schoolDisplay.getPeriodStatus(currentDateTime)); // Set the period status
            this.periodStatus.setFont(GraphicsHelper.fitTextIntoWidth( // Set the font size of the period status depending on the size of the panel
                    new Font("Arial", Font.PLAIN, 35),
                    this.periodStatus.getText(),
                    this.periodStatus.getX(),
                    this.getSize().width - 25)
            );

            this.periodTime.setText(schoolDisplay.getRemainingTime(currentDateTime)); // Set the period time left
            this.periodTime.setFont(GraphicsHelper.fitTextIntoWidth( // Set the font size of the period time depending on the size of the panel
                    new Font("Arial", Font.PLAIN, 60),
                    this.periodTime.getText(),
                    this.periodTime.getX(),
                    this.getSize().width - 25)
            );

            NextUp nextUpObject = new NextUp(); // Initialize a NextUp object to get next up data
            this.nextUp.setText(nextUpObject.getNextUpPanel(this.schoolDisplay.getSchoolCalendar().getSchoolYear().getUserNextUp(), currentDateTime)); // Set the next up text
            this.nextUp.setFont( // Set the font size of the next up text depending on the size of the panel
                    GraphicsHelper.fitTextIntoWidth(
                        new Font("Arial", Font.PLAIN, 20),
                        this.nextUp.getText(),
                        this.nextUp.getX(),
                        this.getSize().width,
                        "<br>"
                    )
            );

            Thread.sleep(1000); // Sleep for 1 second
            repaint(); // Redraw the screen
        }
    }
    // end: public void animate
}
// end: public class Periodpanel extends JPanel