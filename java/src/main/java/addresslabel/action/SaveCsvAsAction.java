package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class SaveCsvAsAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public SaveCsvAsAction( Model model, View view )
    {
        super( "Save CSV As" );
        putValue( Action.SHORT_DESCRIPTION, "Save CSV in a new file" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        FileDialog fd = _view.getSaveCsvFileDialog();
        fd.setVisible( true );
        String filepath = fd.getFile();
        if( filepath != null )
        {
            _model.setLoadedFilepath( filepath );
            _model.writeCsv();
        }
    }
}

