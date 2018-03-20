package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class SaveAsAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public SaveAsAction( Model model, View view )
    {
        super( "Save As" );
        putValue( Action.SHORT_DESCRIPTION, "Save this project in a new file" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {

    }
}

