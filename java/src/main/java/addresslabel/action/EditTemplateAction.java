package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;
import addresslabel.view.EditTemplateDialog;

public class EditTemplateAction extends AbstractAction
{
    private Model _model;
    private View _view;
    private Record _record;

    public EditTemplateAction( Model model, View view, Record record )
    {
        super( "Edit Template" );
        putValue( Action.SHORT_DESCRIPTION, "Edit Template" );
        _model = model;
        _view = view;
        _record = record;
    }

    public void actionPerformed( ActionEvent e )
    {
        EditTemplateDialog d = new EditTemplateDialog( _model, _view, _record );
        d.setVisible( true );
    }
}

