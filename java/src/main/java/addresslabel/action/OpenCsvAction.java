package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.util.Logger;

public class OpenCsvAction extends AbstractAction
{
    private Logger _logger;
    private Model _model;
    private View _view;

    public OpenCsvAction( Model model, View view )
    {
        super( "Open CSV" );
        putValue( Action.SHORT_DESCRIPTION, "Open a CSV file and import the contacts" );
        _logger = Logger.getLogger( OpenCsvAction.class );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        FileDialog fd = _view.getLoadCsvFileDialog();
        fd.setVisible( true );
        String filepath = fd.getFile();

        if( filepath != null )
        {
            if( !_model.loadContacts( filepath ) )
            {
                _logger.error( "Contacts failed to load" );
            }
            else
            {
                _logger.info( "Loaded contacts: " + filepath );
                _model.setPage( 0 );
                _view.displayPage();
            }
        }
    }
}

