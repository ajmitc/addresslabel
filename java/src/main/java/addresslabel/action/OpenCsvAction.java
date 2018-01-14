package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class OpenCsvAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public OpenCsvAction( Model model, View view )
    {
        super( "Open CSV" );
        _model = model;
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Open CSV File" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_O ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        FileDialog fd = _view.getFileDialog();
        fd.setMode( FileDialog.LOAD );
        fd.setTitle( "Open CSV File" );

        fd.setVisible( true );

        String filename = fd.getFile();

        if( filename != null )
        {
            if( _model.loadContactsFromFile( fd.getDirectory() + "/" + filename ) )
            {
                _view.refresh();
            }
        }
    }
}

