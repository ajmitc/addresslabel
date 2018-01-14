package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.view.AboutDialog;

public class DisplayAboutAction extends AbstractAction
{
    private View _view;

    public DisplayAboutAction( View view )
    {
        super( "About" );
        _view  = view;
        putValue( Action.SHORT_DESCRIPTION, "Display About Dialog" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_A ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        AboutDialog d = new AboutDialog( _view.getFrame() );
        d.setVisible( true );
    }
}
