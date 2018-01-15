package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

import addresslabel.Model;
import addresslabel.view.View;

import org.apache.pdfbox.pdmodel.PDDocument;

public class ExportToPdfAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public ExportToPdfAction( Model model, View view )
    {
        super( "Export PDF" );
        _model = model;
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Export to PDF File" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_X ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        if( _model.getUsedRecords().size() == 0 )
        {
            System.out.println( "No records found, nothing to export!" );
            return;
        }

        _view.getSheetPanel().saveLabels();
        PDDocument doc = _model.getTemplate().toPDF( _model.getUsedRecords() );
        String fn = __getTempFilename();
        try
        {
            doc.save( fn );
        }
        catch( IOException ioe )
        {
            ioe.printStackTrace();
        }

        if( Desktop.isDesktopSupported() )
        {
            try 
            {
                File myFile = new File( fn );
                Desktop.getDesktop().open( myFile );
            }
            catch( IOException ex ) 
            {
                // no application registered for PDFs
                System.err.println( "No application registered to open PDFs, file save at " + fn );
            }
        }
        else
        {
            try
            {
                Runtime.getRuntime().exec( "cmd " + fn );
            }
            catch( Exception exc )
            {
                exc.printStackTrace();
            }

            //if sys.platform.startswith( 'linux' ):
            //subprocess.call( [ "xdg-open", fn ] )
        }
    }

    private String __getTempFilename()
    {
        //if( sys.platform == "windows":
        //return "C:\\temp\\%s.pdf" % uuid.uuid4()
        return String.format( "/tmp/contact-labels-%s.pdf", UUID.randomUUID().toString() );
    }

}
