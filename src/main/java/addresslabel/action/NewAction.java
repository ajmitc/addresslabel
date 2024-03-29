package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class NewAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public NewAction( Model model, View view )
    {
        super( "New" );
        putValue( Action.SHORT_DESCRIPTION, "Start a new labels project" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        _model.clearRecords();
        _model.addPageOfEmptyRecords();
        _view.setStatus("Load a CSV File or Import your Google Contacts");
        _view.refresh();
    }
}

