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
import addresslabel.view.GridBagLayoutHelper;

public class RecordDiffPanel extends JPanel {
    private static final Color COLOR_SAME      = Color.WHITE;
    private static final Color COLOR_DIFFERENT = Color.RED;
    private static final Color COLOR_IGNORE    = Color.WHITE;

    public RecordDiff recordDiff;

    private static class Row {
        public JTextField valentry1;
        public JTextField valentry2;
    }

    public Map<String, Row> rows = new HashMap<>();

    public RecordDiffPanel(RecordDiff rd) {
        super(new BorderLayout());
        this.recordDiff = rd;
        setBorder(BorderFactory.createLineBorder(Color.BLACK));

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
                    (recordDiff.getRecord1() != null && recordDiff.getRecord1().getData().containsKey(lbl) && !recordDiff.getRecord1().getData().get(lbl).trim().equals("")) ||
                    (recordDiff.getRecord2() != null && recordDiff.getRecord2().getData().containsKey(lbl) && !recordDiff.getRecord2().getData().get(lbl).trim().equals(""))) {
                keys.add(lbl);
            }
        }

        JPanel fieldspanel = new JPanel();
        GridBagLayoutHelper layoutHelper = new GridBagLayoutHelper(fieldspanel);
        for (int i = 0; i < keys.size(); ++i) {
            String key = keys.get(i);
            if (Record.LABEL_IGNORE.contains(key))
                continue;

            Row row = new Row();

            // Add Key Textfield
            JTextField keyentry = new JTextField(key);
            keyentry.setEditable(false);
            layoutHelper.add(keyentry);

            // Add Record 1 Textfield
            row.valentry1 = new JTextField();
            row.valentry1.setPreferredSize(new Dimension(250, row.valentry1.getHeight()));
            if (recordDiff.getRecord1().getData().containsKey(key)) {
                row.valentry1.setText(recordDiff.getRecord1().getData().get(key));
            }
            layoutHelper.add(row.valentry1);

            // Add Copy button
            JButton btnCopy = new JButton("<--");
            layoutHelper.add(btnCopy);

            // Add Record 2 Textfield
            row.valentry2 = new JTextField();
            row.valentry2.setPreferredSize(new Dimension(250, row.valentry2.getHeight()));
            if (recordDiff.getRecord2().getData().containsKey(key)) {
                row.valentry2.setText(recordDiff.getRecord2().getData().get(key));
            }
            layoutHelper.add(row.valentry2);

            btnCopy.addActionListener(new CopyFieldActionListener(key, recordDiff.getRecord1(), recordDiff.getRecord2(), row.valentry1, row.valentry2));

            // Add Ignore button
            JButton btnIgnore = new JButton("X");
            btnIgnore.addActionListener(new IgnoreFieldActionListener(row.valentry1, row.valentry2));
            layoutHelper.add(btnIgnore);

            if (row.valentry1.getText().equals(row.valentry2.getText())){
                row.valentry2.setBackground(COLOR_SAME);
            }
            else {
                row.valentry2.setBackground(COLOR_DIFFERENT);
            }

            rows.put(key, row);

            layoutHelper.nextRow();
        }

        add(fieldspanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JLabel lblScore = new JLabel((recordDiff.getComparisonScore() * 100.0) + "%");
        bottomPanel.add(lblScore, BorderLayout.WEST);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCopyAll = new JButton("Copy All");
        btnCopyAll.addActionListener(new CopyAllActionListener(this));
        btnPanel.add(btnCopyAll);
        JButton btnSkipRecord = new JButton("Ignore Record");
        btnSkipRecord.addActionListener(new IgnoreAllActionListener(this));
        btnPanel.add(btnSkipRecord);
        bottomPanel.add(btnPanel, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.NORTH);
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
            valentry2.setBackground(COLOR_SAME);
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
            // Change color for both JTextFields
            this.valentry1.setBackground(COLOR_IGNORE);
            this.valentry2.setBackground(COLOR_IGNORE);
        }
    }

    private static class CopyAllActionListener implements ActionListener {
        private RecordDiffPanel parent;
        public CopyAllActionListener(RecordDiffPanel parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent e) {
            parent.copyUpdateFieldsToOriginal();
        }
    }

    private void copyUpdateFieldsToOriginal(){
        for (String key: recordDiff.getRecord2().getData().keySet()) {
            String value2 = recordDiff.getRecord2().getData().get(key);
            recordDiff.getRecord1().getData().put(key, value2);

            rows.get(key).valentry1.setText(value2);
            rows.get(key).valentry2.setBackground(COLOR_SAME);
        }
    }

    private static class IgnoreAllActionListener implements ActionListener {
        private RecordDiffPanel parent;
        public IgnoreAllActionListener(RecordDiffPanel parent) {
            this.parent = parent;
        }

        public void actionPerformed(ActionEvent e) {
            for (Row row: parent.rows.values()) {
                row.valentry2.setBackground(COLOR_SAME);
            }
        }
    }
}
