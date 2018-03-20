package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;

import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class SaveCsvAction extends AbstractAction
{
    private Model _model;
    private View _view;

    public SaveCsvAction( Model model, View view )
    {
        super( "Save CSV" );
        putValue( Action.SHORT_DESCRIPTION, "Save CSV file" );
        _model = model;
        _view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        if( _model.getLoadedFilepath() != null )
            _model.writeCsv();
        else
        {
            new SaveCsvAsAction( _model, _view ).actionPerformed( e );
        }
    }
}

