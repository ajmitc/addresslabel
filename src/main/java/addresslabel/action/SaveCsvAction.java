package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.FileDialog;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.util.Logger;
import addresslabel.view.View;

public class SaveCsvAction extends AbstractAction
{
    private static Logger logger = Logger.getLogger( SaveCsvAction.class );
    private Model _model;
    private View _view;
    private boolean _saveas;

    public SaveCsvAction( Model model, View view, boolean saveas )
    {
        super( saveas? "Save CSV As": "Save CSV" );
        _model = model;
        _view  = view;
        _saveas = saveas;
        putValue( Action.SHORT_DESCRIPTION, "Save CSV File" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_S ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        if (_model.getLoadedFilepath() != null && !_saveas)
            _model.writeCsv();
        else
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
}

