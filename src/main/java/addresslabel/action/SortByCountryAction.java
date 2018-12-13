package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.view.View;
import addresslabel.view.HelpDialog;

public class SortByCountryAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public SortByCountryAction( Model model, View view )
    {
        super( "Sort By Country" );
        _model = model;
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Sort Records by Country" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_C ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        _model.sortRecords( Record.ADDRESS_COUNTRY );
        _view.refresh();
    }
}
