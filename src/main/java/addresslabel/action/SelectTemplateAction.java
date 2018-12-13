package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;
import addresslabel.template.Template;


public class SelectTemplateAction extends AbstractAction
{
    private Model _model;
    private View _view;
    private Template _template;

    public SelectTemplateAction( Model model, View view, Template templ )
    {
        super( templ.getName() );
        putValue( Action.SHORT_DESCRIPTION, "Select Label Template" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_T ) );
        _model = model;
        _view = view;
        _template = templ;
    }

    public void actionPerformed( ActionEvent e )
    {
        if( _template == _model.getTemplate() )
            return;
        _model.setTemplate( _template );
        _view.getSheetPanel().reset();
        _model.setPage( 0 );
        _view.displayPage();
    }
}

