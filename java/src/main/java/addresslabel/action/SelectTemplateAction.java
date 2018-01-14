package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.view.HelpDialog;

public class SelectTemplateAction extends AbstractAction
{
    private View _view;
    private int _index;

    public SelectTemplateAction( View view, String templateName, int index )
    {
        super( templateName );
        _view  = view;
        _index = index;
        putValue( Action.SHORT_DESCRIPTION, "Select Template '" + templateName + "'" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_T ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        _view.selectTemplate( _index );
    }
}
