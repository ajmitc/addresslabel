package addresslabel.action;

import addresslabel.Model;
import addresslabel.util.Logger;
import addresslabel.view.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class OpenProjectAction extends AbstractAction
{
    private static Logger _logger = Logger.getLogger( OpenProjectAction.class );
    private Model _model;
    private View _view;

    public OpenProjectAction( Model model, View view )
    {
        super( "Open Project" );
        putValue( Action.SHORT_DESCRIPTION, "Open project" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        FileDialog fd = _view.getLoadProjectFileDialog();
        fd.setVisible( true );
        String filepath = fd.getFile();
        if( filepath != null )
        {
            if( !_model.loadProject( filepath ) )
            {
                _logger.error( "Project failed to load" );
            }
            else
            {
                _logger.info( "Loaded project: " + filepath );
                _model.setPage( 0 );
                //_view.refresh();
                _view.displayPage();
            }
        }
    }
}

