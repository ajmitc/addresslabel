package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.view.View;
import addresslabel.view.HelpDialog;

public class DisplayHelpAction extends AbstractAction
{
    private View view;

    public DisplayHelpAction( View view )
    {
        super("Help");
        this.view = view;
        putValue(Action.SHORT_DESCRIPTION, "Display Help Dialog");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
    }


    public void actionPerformed( ActionEvent e )
    {
        HelpDialog d = new HelpDialog( view.getFrame() );
        d.setVisible( true );
    }
}
