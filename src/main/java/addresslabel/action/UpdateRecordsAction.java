package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.List;

import addresslabel.compare.RecordComparator;
import addresslabel.compare.RecordDiff;
import addresslabel.Model;
import addresslabel.Record;
import addresslabel.util.Logger;
import addresslabel.view.compare.CompareRecordsDialog;
import addresslabel.view.View;

/**
 * This Action enables the user to update the current record set with records
 * from another CSV file (ie. update the 2017 address set with an updated 2018 address
 * set).
 * When initiated, a diff is generated between the two sets and a dialog is displayed asking
 * which records should be updated.
 */
public class UpdateRecordsAction extends AbstractAction {
    private Logger logger = Logger.getLogger(UpdateRecordsAction.class);
    private Model model;
    private View view;

    public UpdateRecordsAction(Model model, View view) {
        super("Update Records With CSV");
        putValue(Action.SHORT_DESCRIPTION, "Update Records");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_U);
        this.model = model;
        this.view = view;
    }


    public void actionPerformed(ActionEvent e) {
        // Get other CSV to load
        FileDialog fd = view.getLoadCsvFileDialog();
        fd.setVisible(true);
        String filepath = fd.getFile();

        if (filepath != null) {
            try {
                List<Record> records = model.getRecordsFromCsv(fd.getDirectory() + "/" + filepath);
                if (records == null) {
                    logger.error("Contacts failed to load");
                } else {
                    logger.info("Loaded contacts from " + filepath);
                    logger.info("Getting diffs");
                    List<RecordDiff> diffs = RecordComparator.getDiffs(model.getRecords(), records);
                    // Display diffs in dialog and let user select which ones to copy over
                    CompareRecordsDialog d = new CompareRecordsDialog(view.getFrame(), model, diffs);
                    d.setVisible(true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        view.refresh();
    }
}

