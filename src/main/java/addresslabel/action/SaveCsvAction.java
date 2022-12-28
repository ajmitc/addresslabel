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
    private Model model;
    private View view;
    private boolean saveas;

    public SaveCsvAction( Model model, View view, boolean saveas )
    {
        super(saveas? "Save CSV As": "Save CSV");
        this.model = model;
        this.view = view;
        this.saveas = saveas;
        putValue(Action.SHORT_DESCRIPTION, "Save CSV File");
        putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
    }


    public void actionPerformed( ActionEvent e )
    {
        if (model.getLoadedFilepath() != null && !saveas) {
            if (model.writeCsv())
                view.setStatus("Saved labels to " + model.getLoadedFilepath());
            else
                view.setStatus("Failed to save labels to " + model.getLoadedFilepath());
        }
        else
        {
            FileDialog fd = view.getSaveCsvFileDialog();
            fd.setVisible( true );
            String filepath = fd.getFile();
            if( filepath != null )
            {
                model.setLoadedFilepath( filepath );
                if (model.writeCsv())
                    view.setStatus("Saved labels to " + model.getLoadedFilepath());
                else
                    view.setStatus("Failed to save labels to " + model.getLoadedFilepath());
            }
        }
    }
}

