package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.view.ManualDialog;

public class DisplayManualAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public DisplayManualAction( Model model, View view )
    {
        super( "Manual" );
        putValue( Action.SHORT_DESCRIPTION, "Display the manual" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        ManualDialog d = new ManualDialog( _view.getFrame() );
        d.setVisible( true );
    }
}

