package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.Desktop;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.IOException;

import addresslabel.Model;
import addresslabel.view.View;

public class ExportToPdfAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public ExportToPdfAction( Model model, View view )
    {
        super( "Export To PDF" );
        putValue( Action.SHORT_DESCRIPTION, "Export labels to PDF file" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        if( _model.getRecords().size() == 0 )
            return;
        _view.getSheetPanel().saveLabels();
        String fn = null;
        try
        {
            fn = _model.getTemplate().getPrintableDoc( _model.getRecords(), "contact-labels.pdf" );
        }
        catch( IOException ioe )
        {
            ioe.printStackTrace();
            return;
        }

        if( fn == null )
            return;
        //if( System.getProperty( "os.name" ).startswith( "linux" ) )
        //{
            //subprocess.call( [ "xdg-open", fn ] )
        //}
        try 
        {
            Desktop.getDesktop().open( new File( fn ) );
        }
        catch( IllegalArgumentException iae ) // if the specified file dosen't exist 
        {
            iae.printStackTrace();
        }
        catch( UnsupportedOperationException uoe ) // if the current platform does not support the Desktop.Action.OPEN action 
        {
            uoe.printStackTrace();
        }
        catch( IOException ioe ) // if the specified file has no associated application or the associated application fails to be launched 
        {
            ioe.printStackTrace();
        }
        catch( SecurityException se ) // insufficient permissions
        {
            se.printStackTrace();
        }
        catch( Exception ex )
        {
            ex.printStackTrace();
        }
    }
}

