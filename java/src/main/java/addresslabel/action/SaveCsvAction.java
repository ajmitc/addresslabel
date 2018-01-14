package addresslabel.action;

import javax.swing.Action;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.view.View;

public class SaveCsvAction extends AbstractAction
{
    private Model _model;
    private View _view;
    private boolean _saveas;

    public SaveCsvAction( Model model, View view, boolean saveas )
    {
        super( saveas? "Save As": "Save" );
        _model = model;
        _view  = view;
        _saveas = saveas;
        putValue( Action.SHORT_DESCRIPTION, "Save CSV File" );
        putValue( Action.MNEMONIC_KEY, new Integer( KeyEvent.VK_S ) );
    }


    public void actionPerformed( ActionEvent e )
    {
        /*
        if self.loaded_filepath is not None:
            self.write_csv( self.loaded_filepath )
            */
    }


    /*
    def saveas( self ):
        filepath = filedialog.asksaveasfilename( parent=self, defaultextension="csv", initialdir=".", title="Save Contact List", filetypes=(("Comma-Separated-Values", "*.csv"), ("All Files", "*.*")) )
        if filepath:
            self.write_csv( filepath )
            self.loaded_filepath = filepath


    def write_csv( self, filepath ):
        self.log.debug( "Saving csv to %s" % filepath )
        with open( filepath, 'wb' ) as csvfile:
            csvwriter = csv.writer( csvfile, delimiter=',' )
            header = None
            for record in self.records:
                if header is None:
                    header = record.data.keys()
                    csvwriter.writerow( header )
                    //self.log.debug( str(header) )
                row = [ record.data[ h ] if h in record.data.keys() else "" for h in header ]
                csvwriter.writerow( row )
                //self.log.debug( str(row) )
    */
}

