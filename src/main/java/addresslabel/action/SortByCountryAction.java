package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;

public class SortByCountryAction extends AbstractAction {
    private Model model;
    private View view;

    public SortByCountryAction(Model model, View view) {
        super("Sort By Country");
        this.model = model;
        this.view = view;
        putValue(Action.SHORT_DESCRIPTION, "Sort Records by Country");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_C);
    }


    public void actionPerformed(ActionEvent e) {
        model.sortRecords(Record.ADDRESS_COUNTRY);
        view.refresh();
    }
}
