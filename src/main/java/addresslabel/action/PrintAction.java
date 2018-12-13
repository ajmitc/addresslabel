package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class PrintAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public PrintAction( Model model, View view )
    {
        super( "Print" );
        putValue( Action.SHORT_DESCRIPTION, "Print labels" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        _view.getSheetPanel().saveLabels();
        if( _model.getRecords().size() == 0 )
            return;
        //printer = Printer( self )
        //printer.print_labels()
    }
}

