package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;
import addresslabel.view.HelpDialog;

public class SortByLastNameAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public SortByLastNameAction( Model model, View view )
    {
        super( "Sort By Last Name" );
        _model = model;
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Sort Records by Last Name" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_L ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        _model.sortRecords( Record.LAST_NAME );
        _view.refresh();
    }
}
