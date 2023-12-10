package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class AddPageAction extends AbstractAction {
    private Model model;
    private View view;

    public AddPageAction(Model model, View view) {
        super("Add Page");
        putValue(Action.SHORT_DESCRIPTION, "Add a page");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        this.model = model;
        this.view = view;
    }


    public void actionPerformed(ActionEvent e) {
        model.addEmptyRecords(model.getRecordsPerPage());
        view.refresh();
    }
}

