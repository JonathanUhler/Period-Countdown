import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;


public class GenDataPanel extends JPanel {

    private ArrayList<JPanel> screens = new ArrayList<>();
    private int currentScreen = 0;


    public GenDataPanel() {
        this.screens.add(this.getWelcome());
        this.screens.add(this.getAcceptPreface());
    }


    private JPanel getWelcome() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));

        // General information
        // Terms
        // Preface warning to accept
        String welcomeMessage = """
                Welcome to the Period Countdown Data Generator
                =====================================================
                Data Generation and Usage Process--
                Follow the instructions given by this python script and enter information when prompted
                Using this script requires:
                    Basic knowledge of your school's daily bell schedule for each "type" of day
                    Basic knowledge of your school's yearly calendar schedule (including weeks that are different such as 4-day weeks with Monday holidays)
                \nUseful Terminology and Information--
                    Number - an integer or decimal number, positive or negative (ex: 4, 3.249, -23.0093)
                    String - a sequence of any characters (ex: "foo", "bar@!.foo*", "test123")
                \nHow do you specify a passing period/lunch/other non-class period? - Use the number -1 or -2 for so called "fake" periods
                    -1 indicates to Period-Countdown that the period is very unimportant (ex: passing period). If multiple periods with -1 are places back-to-back the times in them will be merged
                    -2 indicates to Period-Countdown that the period is somewhat unimportant (ex: lunch, tutorial/studyhall). -2 and -1 periods will not have their times merged
                \nHow do you specify times for periods? - Use 24-hour/military time
                    The entire scope of the day is between 00:00 (the first millisecond of the day) to 23:59 (the last millisecond of the day)
                \nPreface Warning - READ CAREFULLY--
                Before proceeding, please understand and accept that:
                    1) There is no guarantee Period-Countdown will have an accurate schedule. Always check with teachers and schools about changes in schedules. Do not blame Period-Countdown or its developers for tardies or absences!
                    2) While the Period-Countdown developers try to make it as foolproof as possible, there is no guarantee that all the code will work perfectly. If you encounter issues or have a feature request, please see the Github page online
                """;

        JTextArea responseTextArea = new JTextArea(30, 40);
        responseTextArea.setText(welcomeMessage);
        responseTextArea.setEditable(false);
//        JLabel welcomeLabel = new JLabel(welcomeMessage);
//        welcomeLabel.setPreferredSize(this.getPreferredSize());
//        welcomeLabel.setBorder(new EmptyBorder(15, 15, 0, 0));

        welcomePanel.add(new JScrollPane(responseTextArea));

        return welcomePanel;
    }


    private JPanel getAcceptPreface() {
        JPanel acceptPanel = new JPanel();
        acceptPanel.setLayout(new BoxLayout(acceptPanel, BoxLayout.Y_AXIS));

        JLabel acceptLabel = new JLabel("Do you accept the Preface Warning from the previous screen? [Y/n]");
        acceptLabel.setPreferredSize(this.getPreferredSize());
        acceptLabel.setBorder(new EmptyBorder(15, 15, 0, 0));

        JTextField acceptField = new JTextField();

        JButton acceptContinueButton = new JButton("Submit");
        acceptContinueButton.addActionListener(e -> {
//            this.getData(acceptField.getText(), String.class);
        });

        acceptPanel.add(acceptContinueButton);
        acceptPanel.add(acceptLabel);
        acceptPanel.add(acceptField);

        return acceptPanel;
    }


    private JButton getPreviousButton() {
        JButton previous = new JButton("Go Back");

        previous.addActionListener(e -> {
            this.currentScreen = (this.currentScreen > 0) ? this.currentScreen - 1 : this.currentScreen;
            this.reload();
        });

        return previous;
    }


    private JButton getNextButton(boolean validData) {
        JButton next = new JButton("Continue");

        next.addActionListener(e -> {

            if (validData) {
                this.currentScreen = (this.currentScreen < this.screens.size() - 1) ? this.currentScreen + 1 : this.currentScreen;
                this.reload();
            }
        });

        return next;
    }


    public void genData() {
        this.add(this.getNextButton(true));
        this.add(this.getPreviousButton());

        this.add(this.screens.get(this.currentScreen));
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, 700);
    }


    private void reload() {
        this.removeAll();
        this.revalidate();
        this.repaint();
        this.genData();
    }

}
