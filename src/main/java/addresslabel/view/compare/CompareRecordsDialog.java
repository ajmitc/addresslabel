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
import addresslabel.view.BaseDialog;

public class CompareRecordsDialog extends BaseDialog {
    private Model _model;
    private List<RecordDiff> _recordDiffs;

    private List<RecordPanel> _recordPanels = new ArrayList<>();
    private List<RecordDiffPanel> _recordDiffPanels = new ArrayList<>();

    private JPanel updatedPanel;
    private JPanel removedPanel;
    private JPanel addedPanel;

    public CompareRecordsDialog(JFrame parent, Model model, List<RecordDiff> recordDiffs) {
        super(parent, "Compare Records", false, 1000, 800);
        _model = model;
        _recordDiffs = recordDiffs;

        JPanel content = new JPanel(new BorderLayout());

        JLabel lblHelpTxt = new JLabel("Compare the records below and choose how to handle them.");
        content.add(lblHelpTxt, BorderLayout.NORTH);

        updatedPanel = new JPanel();
        removedPanel = new JPanel();
        addedPanel = new JPanel();
        updatedPanel.setLayout(new BoxLayout(updatedPanel, BoxLayout.PAGE_AXIS));
        removedPanel.setLayout(new BoxLayout(removedPanel, BoxLayout.PAGE_AXIS));
        addedPanel.setLayout(new BoxLayout(addedPanel, BoxLayout.PAGE_AXIS));

        int updatedCnt = 0;
        int addedCnt = 0;
        int removedCnt = 0;
        for (RecordDiff rd : recordDiffs) {
            switch (rd.getDiffType()) {
                case Added: {
                    RecordPanel recordPanel = new RecordPanel(addedCnt + 1, rd.getRecord2(), rd.getDiffType(), _model, addedPanel);
                    addedPanel.add(recordPanel);
                    addedCnt += 1;
                    _recordPanels.add(recordPanel);
                    break;
                }
                case Updated:
                    RecordDiffPanel recordDiffPanel = new RecordDiffPanel(rd);
                    updatedPanel.add(recordDiffPanel);
                    updatedCnt += 1;
                    _recordDiffPanels.add(recordDiffPanel);
                    break;
                case Removed: {
                    RecordPanel recordPanel = new RecordPanel(removedCnt + 1, rd.getRecord1(), rd.getDiffType(), _model, removedPanel);
                    removedPanel.add(recordPanel);
                    removedCnt += 1;
                    _recordPanels.add(recordPanel);
                    break;
                }
            }
        }

        if (updatedCnt == 0) {
            JLabel lbl = new JLabel("No Records Updated");
            updatedPanel.add(lbl);
        }

        if (addedCnt == 0) {
            JLabel lbl = new JLabel("No Records Added");
            addedPanel.add(lbl);
        }

        if (removedCnt == 0) {
            JLabel lbl = new JLabel("No Records Removed");
            removedPanel.add(lbl);
        }


        JPanel updatedTabPanel = new JPanel(new BorderLayout());
        JPanel addedTabPanel = new JPanel(new BorderLayout());
        JPanel removedTabPanel = new JPanel(new BorderLayout());

        JLabel lblUpdatedSummary = new JLabel("<html>Found " + updatedCnt + " Records<br/>These records exist in both the active CSV and the updated CSV, but they contain differences.<br/>For each, choose how to handle the updates.</html>");
        updatedTabPanel.add(lblUpdatedSummary, BorderLayout.NORTH);
        updatedTabPanel.add(new JScrollPane(updatedPanel), BorderLayout.CENTER);

        JLabel lblAddedSummary = new JLabel("<html>These records DO NOT exist in the active CSV, but are found in the updated CSV.  Click Add Record to<br/>add the record to the active CSV.</html>");
        addedTabPanel.add(lblAddedSummary, BorderLayout.NORTH);
        addedTabPanel.add(new JScrollPane(addedPanel), BorderLayout.CENTER);

        JLabel lblRemovedSummary = new JLabel("<html>These records exist in the active CSV, but not in the updated CSV.  Click Remove Record to remove<br/>the record from the active CSV.</html>");
        removedTabPanel.add(lblRemovedSummary, BorderLayout.NORTH);
        removedTabPanel.add(new JScrollPane(removedPanel), BorderLayout.CENTER);

        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.addTab("Updated", null, updatedTabPanel, "Updated Records");
        tabPanel.addTab("Removed", null, removedTabPanel, "Removed Records");
        tabPanel.addTab("Added", null, addedTabPanel, "Added Records");


        content.add(tabPanel, BorderLayout.CENTER);
        setContent(content);

        hideCancel();
    }
}

