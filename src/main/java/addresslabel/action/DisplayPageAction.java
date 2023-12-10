package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class DisplayPageAction extends AbstractAction {
    public static final String NEXT = ">";
    public static final String PREV = "<";

    private Model model;
    private View view;
    private String direction;

    public DisplayPageAction(Model model, View view, String dir) {
        super(dir);
        putValue(Action.SHORT_DESCRIPTION, "Display " + (dir.equals(NEXT) ? "Next" : "Prev") + " Page");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
        this.model = model;
        this.view = view;
        direction = dir;
    }


    public void actionPerformed(ActionEvent e) {
        if (direction.equals(NEXT))
            displayNextPage();
        else
            displayPrevPage();
    }

    public void displayPrevPage() {
        if (model.getPage() == 0)
            return;
        model.setPage(model.getPage() - 1);
        view.displayPage();
    }


    public void displayNextPage() {
        int recordsPerPage = model.getRecordsPerPage();
        if (model.getPage() < (Math.ceil((float) model.getRecords().size() / (float) recordsPerPage)) - 1) {
            model.setPage(model.getPage() + 1);
            view.displayPage();
        }
    }
}

