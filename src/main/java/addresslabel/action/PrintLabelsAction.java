package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.print.*;
import javax.print.attribute.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.*;

import addresslabel.Model;
import addresslabel.view.View;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

public class PrintLabelsAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public PrintLabelsAction( Model model, View view )
    {
        super( "Print Labels" );
        _model = model;
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Print Labels" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_P ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName( "Address Labels" );
        boolean ok = job.printDialog();
        if( ok ) 
        {
            doPrint( job );
        }
    }


    private void doPrint( PrinterJob job )
    {
        PDDocument document = _model.getTemplate().toPDF( _model.getUsedRecords() );
        job.setPageable( new PDFPageable( document ) );

        try 
        {
            job.print();
        } 
        catch( PrinterException ex ) 
        {
            // The job did not successfully complete
            ex.printStackTrace();
        }
    }
}
