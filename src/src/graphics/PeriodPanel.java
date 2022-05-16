// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=
// PeriodPanel.java
// Period-Countdown
//
// Created by Jonathan Uhler on 9/9/21
// +=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=+=


package graphics;


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
    // Arguments--
    //
    // width:   width of the panel
    //
    // height:  height of the panel
    //
    public PeriodPanel(int width, int height) throws Exception {
        this.width = width;
        this.height = height;

        this.periodStatus = new JLabel();
        this.periodTime = new JLabel();
        this.nextUp = new JLabel();

        this.schoolDisplay = new SchoolDisplay();

        // In order to prevent graphical components from stacking vertically on top of each other in the
        // center of the app, a BoxLayout is used. This stacks elements from the top down on top of each
        // other. To provide finer control over layout, EmptyBorders are used which add transparent
        // spacers on the 4 sides of the component.
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
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
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.add(this.periodStatus);
        this.add(this.periodTime);
        this.add(this.nextUp);
    }
    // end: public void paintComponent


    // ====================================================================================================
    // public void animate
    //
    // Animate the changing values/information within the labels every "frame"
    //
    public void animate() throws Exception {
        // Loop the animation forever with a 1-second delay at the very end to avoid high CPU usage
        while (true) {
            // Step 1: get the current time from the computer's clock/timezone in order to use for
            //         displaying and computing the differences between periods
            Calendar currentDateTime = Calendar.getInstance();

            // Step 2: update pretty graphical elements such as the background color and window size.
            //         The window size is used later on to change the size of the text if the window is
            //         shrunk below a certain threshold
            this.setBackground(Color.decode("0x" + schoolDisplay
                    .getSchoolCalendar()
                    .getSchoolYear()
                    .getUserTheme()));
            this.setSize(this.getParent().getSize());

            // Step 3: display the three major components (status, time remaining, and next up data)
            //         The status is a message about what the period is such as "Period 4: Chemistry"
            //         The time remaining is the number of hours/min/sec in the period, ex "1:08:23"
            //         The next up panel is a list of the next periods (if any) in the day
            this.periodStatus.setText(schoolDisplay.getPeriodStatus(currentDateTime));
            this.periodStatus.setFont(GraphicsHelper.fitTextIntoWidth(
                    new Font("Arial", Font.PLAIN, 35),
                    this.periodStatus.getText(),
                    this.periodStatus.getX(),
                    this.getSize().width - 25)
            );

            this.periodTime.setText(schoolDisplay.getRemainingTime(currentDateTime));
            this.periodTime.setFont(GraphicsHelper.fitTextIntoWidth(
                    new Font("Arial", Font.PLAIN, 60),
                    this.periodTime.getText(),
                    this.periodTime.getX(),
                    this.getSize().width - 25)
            );

            NextUp nextUpObject = new NextUp();
            this.nextUp.setText(nextUpObject.getNextUpPanel(this.schoolDisplay
                    .getSchoolCalendar()
                    .getSchoolYear()
                    .getUserNextUp(), currentDateTime));
            this.nextUp.setFont(
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
// end: public class PeriodPanel extends JPanel