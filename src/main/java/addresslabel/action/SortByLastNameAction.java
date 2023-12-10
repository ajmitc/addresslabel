package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;

public class SortByLastNameAction extends AbstractAction {
    private Model model;
    private View view;

    public SortByLastNameAction(Model model, View view) {
        super("Sort By Last Name");
        this.model = model;
        this.view = view;
        putValue(Action.SHORT_DESCRIPTION, "Sort Records by Last Name");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);
    }


    public void actionPerformed(ActionEvent e) {
        model.sortRecords(Record.LAST_NAME);
        view.refresh();
    }
}
