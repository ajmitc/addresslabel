package addresslabel.view;

import javax.swing.*;
import java.awt.*;

public class RecordComparatorProgressBarDialog extends JDialog {
    private JProgressBar progressBar;

    public RecordComparatorProgressBarDialog(JFrame parent){
        super(parent, "Comparing Records", true);
        setSize(200, 100);
        setLocationRelativeTo(null);

        progressBar = new JProgressBar();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(progressBar);
    }

    public void prepare(int maxValue){
        progressBar.setMaximum(maxValue);
    }

    public void incProgress(int amount){
        progressBar.setValue(Math.min(100, progressBar.getValue() + amount));
    }
}
