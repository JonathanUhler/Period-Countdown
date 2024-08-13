package desktop.wizard;


import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import school.SchoolJson;


public class Builder extends JPanel {

    private InfoEditor infoEditor;
    private PeriodsEditor periodsEditor;
    private DaysEditor daysEditor;
    private WeeksEditor weeksEditor;
    private ExceptionsEditor exceptionsEditor;
    private JTextArea jsonTextArea;


    public Builder(InfoEditor infoEditor,
                   PeriodsEditor periodsEditor,
                   DaysEditor daysEditor,
                   WeeksEditor weeksEditor,
                   ExceptionsEditor exceptionsEditor)
    {
        this.infoEditor = infoEditor;
        this.periodsEditor = periodsEditor;
        this.daysEditor = daysEditor;
        this.weeksEditor = weeksEditor;
        this.exceptionsEditor = exceptionsEditor;

        this.setLayout(new GridBagLayout());
        
        this.jsonTextArea = new JTextArea("", 30, 45);
        this.jsonTextArea.setEditable(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
	
        gbc.gridx = 0;
        gbc.gridy = 0;
        JScrollPane jsonScrollPane = new JScrollPane(this.jsonTextArea);
        jsonScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.add(jsonScrollPane, gbc);
    }


    public void update() {
        this.jsonTextArea.setText(null);
        
        List<String> errors = new ArrayList<>();
        int numAcademicPeriods = this.periodsEditor.getNumAcademicPeriods();
        Map<String, String> info = this.infoEditor.collect(numAcademicPeriods, errors);
        Map<String, Map<String, String>> periods = this.periodsEditor.collect(errors);
        Map<String, List<Map<String, String>>> days = this.daysEditor.collect(periods, errors);
        Map<String, List<String>> weeks = this.weeksEditor.collect(errors);
        List<Map<String, String>> exceptions = this.exceptionsEditor.collect(errors);

        if (errors.size() > 0) {
            this.jsonTextArea.append("Errors exist in school information:\n\n");
            for (String error : errors) {
                this.jsonTextArea.append(error + "\n");
            }
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("Info", info);
        data.put("Days", days);
        data.put("Weeks", weeks);
        data.put("Exceptions", exceptions);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(data);
        this.jsonTextArea.setText(json);
    }
    
}
