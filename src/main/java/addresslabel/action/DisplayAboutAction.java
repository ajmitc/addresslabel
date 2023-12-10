package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.view.AboutDialog;

public class DisplayAboutAction extends AbstractAction {
    private Model model;
    private View view;

    public DisplayAboutAction(Model model, View view) {
        super("About");
        putValue(Action.SHORT_DESCRIPTION, "Display About dialog");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        this.model = model;
        this.view = view;
    }

    public void actionPerformed(ActionEvent e) {
        AboutDialog d = new AboutDialog(view.getFrame());
        d.setVisible(true);
    }
}

