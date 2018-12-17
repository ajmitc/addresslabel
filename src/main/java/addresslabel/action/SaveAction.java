package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.*;
import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class SaveAction extends AbstractAction
{
    private Model _model;
    private View _view;
    private boolean _saveAs;

    public SaveAction( Model model, View view, boolean saveas )
    {
        super( saveas? "Save Project As": "Save Project" );
        putValue( Action.SHORT_DESCRIPTION, "Not yet implemented" );
        //putValue( Action.SHORT_DESCRIPTION, "Save this project" );
        _model = model;
        _view = view;
        _saveAs = saveas;
    }

    public void actionPerformed( ActionEvent e )
    {
        if( _model.getLoadedProjectFilepath() != null )
            _model.writeProject();
        else
        {
            FileDialog fd = _view.getSaveProjectFileDialog();
            fd.setVisible( true );
            String filepath = fd.getFile();
            if( filepath != null )
            {
                _model.setLoadedProjectFilepath( filepath );
                _model.writeProject();
            }
        }
    }
}

