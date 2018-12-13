package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class ExitAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public ExitAction( Model model, View view )
    {
        super( "Exit" );
        putValue( Action.SHORT_DESCRIPTION, "Exit this application" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        System.exit( 0 );
    }
}

