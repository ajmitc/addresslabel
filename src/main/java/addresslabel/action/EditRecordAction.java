package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;
import addresslabel.view.EditRecordDialog;

public class EditRecordAction extends AbstractAction
{
    private Model _model;
    private View _view;
    private Record _record;

    public EditRecordAction( Model model, View view, Record record )
    {
        super( "Edit Record" );
        putValue( Action.SHORT_DESCRIPTION, "Edit Record" );
        _model = model;
        _view = view;
        _record = record;
    }

    public void actionPerformed( ActionEvent e )
    {
        EditRecordDialog d = new EditRecordDialog( _view.getFrame(), _record );
        d.setVisible( true );
    }
}

