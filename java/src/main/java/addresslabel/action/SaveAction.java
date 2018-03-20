package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class SaveAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public SaveAction( Model model, View view )
    {
        super( "Save" );
        putValue( Action.SHORT_DESCRIPTION, "Save this project" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {

    }
}

