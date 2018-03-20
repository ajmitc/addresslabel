package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class AddPageAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public AddPageAction( Model model, View view )
    {
        super( "Add Page" );
        putValue( Action.SHORT_DESCRIPTION, "Add a page" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_A ) );
        _model = model;
        _view  = view;
    }


    public void actionPerformed( ActionEvent e )
    {
        _model.addEmptyRecords( _model.getRecordsPerPage() );
        _view.refresh();
    }
}

