package addresslabel.action;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.api.google.PeopleAPI;
import addresslabel.compare.RecordDiff;
import addresslabel.util.Logger;
import addresslabel.view.RecordComparatorProgressBarDialog;
import addresslabel.view.RecordComparatorProgressBarWorker;
import addresslabel.view.View;
import addresslabel.view.compare.CompareRecordsDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

/**
 * This Action enables the user to update the current record set with records
 * from another CSV file (ie. update the 2017 address set with an updated 2018 address
 * set).
 * When initiated, a diff is generated between the two sets and a dialog is displayed asking
 * which records should be updated.
 */
public class ImportGoogleContactsAction extends AbstractAction {
    private Logger _logger = Logger.getLogger(ImportGoogleContactsAction.class);
    private Model _model;
    private View _view;

    public ImportGoogleContactsAction(Model model, View view) {
        super("Import Contacts from Google");
        putValue(Action.SHORT_DESCRIPTION, "Import Contacts from Google");
        putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_I));
        _model = model;
        _view = view;
    }


    public void actionPerformed(ActionEvent e) {
        String googleUsername = (String)
                JOptionPane.showInputDialog(
                        _view.getFrame(),
                        "Enter Google Username",
                        "Import Contacts from Google",
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        null,
                        "ajmitc");

        String requiredMembershipsStr = (String)
                JOptionPane.showInputDialog(
                        _view.getFrame(),
                        "Enter Required Contact Label(s)",
                        "Import Contacts from Google",
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        null,
                        "Holiday Cards");
        List<String> requiredMemberships = null;
        if (requiredMembershipsStr != null && !requiredMembershipsStr.trim().isEmpty())
            requiredMemberships = Arrays.asList(requiredMembershipsStr.trim().split(","));

        if (googleUsername != null && !googleUsername.isEmpty()) {
            try {
                List<Record> records = PeopleAPI.getContacts(_model, googleUsername, requiredMemberships);
                if (records == null) {
                    _logger.error("Contacts failed to import");
                } else {
                    _logger.info("Loaded contacts from Google ID " + googleUsername);
                    _logger.info("Getting diffs");
                    RecordComparatorProgressBarDialog dialog = new RecordComparatorProgressBarDialog(_view.getFrame());
                    RecordComparatorProgressBarWorker worker = new RecordComparatorProgressBarWorker(_model.getRecords(), records, dialog);
                    worker.execute();
                    dialog.setVisible(true);
                    List<RecordDiff> diffs = worker.get(); //RecordComparator.getDiffs( _model.getRecords(), records );
                    // Display diffs in dialog and let user select which ones to copy over
                    CompareRecordsDialog d = new CompareRecordsDialog(_view.getFrame(), _model, diffs);
                    d.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        _view.refresh();
    }
}

