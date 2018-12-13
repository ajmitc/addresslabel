package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class DisplayPageAction extends AbstractAction
{
    public static final String NEXT = ">";
    public static final String PREV = "<";

    private Model _model;
    private View _view;
    private String _direction;

    public DisplayPageAction( Model model, View view, String dir )
    {
        super( dir );
        putValue( Action.SHORT_DESCRIPTION, "Display " + (dir.equals( NEXT )? "Next": "Prev") + " Page" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_N ) );
        _model = model;
        _view  = view;
        _direction = dir;
    }


    public void actionPerformed( ActionEvent e )
    {
        if( _direction.equals( NEXT ) )
            displayNextPage();
        else
            displayPrevPage();
    }

    public void displayPrevPage()
    {
        if( _model.getPage() == 0 )
            return;
        _model.setPage( _model.getPage() - 1 );
        _view.displayPage();
    }


    public void displayNextPage()
    {
        int recordsPerPage = _model.getRecordsPerPage();
        if( _model.getPage() < (Math.ceil( (float) _model.getRecords().size() / (float) recordsPerPage )) - 1 )
        {
            _model.setPage( _model.getPage() + 1 );
            _view.displayPage();
        }
    }
}

