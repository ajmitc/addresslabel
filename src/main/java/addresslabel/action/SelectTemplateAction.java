package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.template.LabelSheetTemplate;


public class SelectTemplateAction extends AbstractAction {
    private Model model;
    private View view;
    private LabelSheetTemplate template;

    public SelectTemplateAction(Model model, View view, LabelSheetTemplate templ) {
        super(templ.getName());
        putValue(Action.SHORT_DESCRIPTION, "Select Label Template");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
        this.model = model;
        this.view = view;
        template = templ;
    }

    public void actionPerformed(ActionEvent e) {
        if (template == model.getTemplate())
            return;
        model.setTemplate(template);
        view.getSheetPanel().reset();
        model.setPage(0);
        view.displayPage();
    }
}

