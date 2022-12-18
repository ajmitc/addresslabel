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
    private Logger logger = Logger.getLogger( OpenCsvAction.class );
    private Model model;
    private View view;

    public OpenCsvAction( Model model, View view )
    {
        super( "Open CSV" );
        this.model = model;
        this.view = view;
        putValue( Action.SHORT_DESCRIPTION, "Open a CSV file and import the contacts" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_O ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        FileDialog fd = view.getLoadCsvFileDialog();
        fd.setVisible( true );
        String filepath = fd.getFile();

        if( filepath != null )
        {
            if( !model.loadContactsFromFile( fd.getDirectory() + "/" + filepath ) )
            {
                logger.error( "Contacts failed to load" );
                view.setStatus("Contacts failed to load");
            }
            else
            {
                logger.info( "Loaded contacts: " + filepath );
                view.setStatus("Loaded " + model.getRecords().size() + " contacts from " + filepath);
                model.setPage( 0 );
                //_view.refresh();
                view.displayPage();
            }
        }
    }
}

