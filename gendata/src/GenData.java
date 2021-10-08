import javax.swing.*;


public class GenData {

    public static void main(String[] args) {
        JFrame genDataApplet = new JFrame("Period Countdown - Generate Data");
        GenDataPanel genDataContent = new GenDataPanel();

        genDataApplet.add(genDataContent);

        genDataApplet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        genDataApplet.pack();
        genDataApplet.setVisible(true);

        genDataContent.genData();
    }

}
