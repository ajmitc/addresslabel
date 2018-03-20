package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.view.AboutDialog;

public class DisplayAboutAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public DisplayAboutAction( Model model, View view )
    {
        super( "About" );
        putValue( Action.SHORT_DESCRIPTION, "Display About dialog" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        AboutDialog d = new AboutDialog( _view.getFrame() );
        d.setVisible( true );
    }
}

