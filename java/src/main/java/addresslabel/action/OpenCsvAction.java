package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.util.Logger;

public class OpenCsvAction extends AbstractAction
{
    private Logger _logger = Logger.getLogger( OpenCsvAction.class );
    private Model _model;
    private View _view;

    public OpenCsvAction( Model model, View view )
    {
        super( "Open CSV" );
        _model = model;
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Open a CSV file and import the contacts" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_O ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        FileDialog fd = _view.getLoadCsvFileDialog();
        fd.setVisible( true );
        String filepath = fd.getFile();

        if( filepath != null )
        {
            if( !_model.loadContactsFromFile( fd.getDirectory() + "/" + filepath ) )
            {
                _logger.error( "Contacts failed to load" );
            }
            else
            {
                _logger.info( "Loaded contacts: " + filepath );
                _model.setPage( 0 );
                //_view.refresh();
                _view.displayPage();
            }
        }
    }
}

