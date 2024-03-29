package addresslabel.action;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImportGoogleContactsAction extends AbstractAction
{
    private static final Logger logger = Logger.getLogger(ImportGoogleContactsAction.class.getName());

    private Model model;
    private View view;

    public ImportGoogleContactsAction(Model model, View view )
    {
        super( "Import Contacts" );
        putValue( Action.SHORT_DESCRIPTION, "Import Google Contacts" );
        this.model = model;
        this.view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        String selectedGroup = null;
        try {
            // Group Name -> ResourceName
            Map<String, String> contactGroups = model.getGoogleApi().requestContactGroups();
            view.setGoogleUser("Logged in");
            logger.info("Contact Groups: " + contactGroups.keySet().stream().collect(Collectors.joining(", ")));
            JComboBox comboBox = new JComboBox(contactGroups.keySet().toArray(new String[0]));
            comboBox.setSelectedIndex(0);
            JOptionPane.showMessageDialog(view.getFrame(), comboBox, "Select Contact Group", JOptionPane.QUESTION_MESSAGE);
            String selectedKey = "" + comboBox.getSelectedItem();
            selectedGroup = contactGroups.get(selectedKey);
        } catch (IOException | ExecutionException | InterruptedException | TimeoutException | GeneralSecurityException ex) {
            ex.printStackTrace();
        }
        List<Record> records = model.getGoogleApi().pullGoogleContacts(selectedGroup);
        logger.info("Received " + records.size() + " contacts from Google");
        view.setStatus("Received " + records.size() + " contacts from Google Contacts");

        if (!records.isEmpty()) {
            if (JOptionPane.showConfirmDialog(view.getFrame(), records.size() + " Records found.  Replace existing records?") == JOptionPane.YES_OPTION) {
                model.getRecords().clear();
            }
            model.getRecords().addAll(records);
            model.fillPageWithEmptyRecords();
            model.setPage(0);
            view.refresh();
        }
        else {
            JOptionPane.showMessageDialog(view.getFrame(), "No records retrieved from Google Contacts", "Import Google Contacts", JOptionPane.WARNING_MESSAGE);
        }
    }
}

