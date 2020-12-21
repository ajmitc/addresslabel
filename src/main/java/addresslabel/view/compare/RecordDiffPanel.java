package addresslabel.view.compare;

import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.compare.DiffType;
import addresslabel.compare.RecordDiff;

public class RecordDiffPanel extends JPanel {
    public RecordDiff recordDiff;
    //public Map<String, JTextField> _keyFields;
    //public Map<String, JTextField> _record1Fields;
    //public Map<String, JTextField> _record2Fields;

    public RecordDiffPanel(RecordDiff rd) {
        super(new BorderLayout());
        this.recordDiff = rd;
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //_keyFields = new HashMap<String, JTextField>();
        //_record1Fields = new HashMap<String, JTextField>();
        //_record2Fields = new HashMap<String, JTextField>();

        List<String> keys = new ArrayList<>();
        for (String lbl : Record.LABELS) {
            keys.add(lbl);
        }

        Set<String> recordKeysSet = new HashSet<>();
        if (recordDiff.getRecord1() != null)
            recordKeysSet.addAll(recordDiff.getRecord1().getData().keySet());
        if (recordDiff.getRecord2() != null)
            recordKeysSet.addAll(recordDiff.getRecord2().getData().keySet());

        List<String> recordKeys = new ArrayList<>(recordKeysSet);
        Collections.sort(recordKeys);
        for (String lbl : recordKeys) {
            // Only add lbl/keys that are not blank
            if (!keys.contains(lbl) &&
                    (recordDiff.getRecord1() != null &&
                            recordDiff.getRecord1().getData().containsKey(lbl) &&
                            recordDiff.getRecord1().getData().get(lbl) != null &&
                            !recordDiff.getRecord1().getData().get(lbl).trim().equals("")) ||
                    (recordDiff.getRecord2() != null &&
                            recordDiff.getRecord2().getData().containsKey(lbl) &&
                            recordDiff.getRecord2().getData().get(lbl) != null &&
                            !recordDiff.getRecord2().getData().get(lbl).trim().equals(""))) {
                    keys.add(lbl);
            }
        }

        JPanel fieldspanel = new JPanel(new GridLayout(keys.size(), 5));
        for (int i = 0; i < keys.size(); ++i) {
            String key = keys.get(i);
            if (Record.LABEL_IGNORE.contains(key))
                continue;
            // Add Key Textfield
            JTextField keyentry = new JTextField(key);
            fieldspanel.add(keyentry);

            // Add Record 1 Textfield
            JTextField valentry1 = new JTextField();
            if (recordDiff.getRecord1().getData().containsKey(key)) {
                valentry1.setText(recordDiff.getRecord1().getData().get(key));
            }
            fieldspanel.add(valentry1);

            // Add Copy button
            JButton btnCopy = new JButton("<--");
            fieldspanel.add(btnCopy);

            // Add Record 2 Textfield
            JTextField valentry2 = new JTextField();
            if (recordDiff.getRecord2().getData().containsKey(key)) {
                valentry2.setText(recordDiff.getRecord2().getData().get(key));
            }
            fieldspanel.add(valentry2);

            btnCopy.addActionListener(new CopyFieldActionListener(key, recordDiff.getRecord1(), recordDiff.getRecord2(), valentry1, valentry2));

            // Add Ignore button
            JButton btnIgnore = new JButton("X");
            btnIgnore.addActionListener(new IgnoreFieldActionListener(valentry1, valentry2));
            fieldspanel.add(btnIgnore);

            //_keyFields.put( key, keyentry );
            //_record1Fields.put( key, valentry1 );
            //_record2Fields.put( key, valentry2 );
        }

        add(fieldspanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel lblScore = new JLabel((recordDiff.getComparisonScore() * 100.0) + "%");
        bottomPanel.add(lblScore, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCopyAll = new JButton("Copy All");
        btnPanel.add(btnCopyAll);
        JButton btnSkipRecord = new JButton("Skip Record");
        btnPanel.add(btnSkipRecord);
        bottomPanel.add(btnPanel, BorderLayout.EAST);
    }


    private static class CopyFieldActionListener implements ActionListener {
        private String key;
        private Record record1;
        private Record record2;
        private JTextField valentry1;
        private JTextField valentry2;

        public CopyFieldActionListener(String key, Record record1, Record record2, JTextField valentry1, JTextField valentry2) {
            this.key = key;
            this.record1 = record1;
            this.record2 = record2;
            this.valentry1 = valentry1;
            this.valentry2 = valentry2;
        }

        public void actionPerformed(ActionEvent e) {
            String value2 = record2.getData().containsKey(key) ? record2.getData().get(key) : null;
            record1.getData().put(key, value2);

            valentry1.setText(value2);
            // TODO Change color to black for both JTextFields
        }
    }

    private static class IgnoreFieldActionListener implements ActionListener {
        private JTextField valentry1;
        private JTextField valentry2;

        public IgnoreFieldActionListener(JTextField valentry1, JTextField valentry2) {
            this.valentry1 = valentry1;
            this.valentry2 = valentry2;
        }

        public void actionPerformed(ActionEvent e) {
            // TODO Change color to black for both JTextFields
        }
    }
}
