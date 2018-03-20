package addresslabel.view;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import addresslabel.Record;
import addresslabel.util.Logger;

public class LabelPanel extends JPanel
{
    private Logger _logger = Logger.getLogger( LabelPanel.class );
    private JTextArea _taDisplay;
    private Record _record;

    public LabelPanel()
    {
        super();

        _record = null;

        _taDisplay = new JTextArea( 5, 35 );
        _taDisplay.setWrapStyleWord( true );
        _taDisplay.setEnabled( false );

        _taDisplay.addMouseListener( new MouseAdapter(){
            public void mouseClicked( MouseEvent e )
            {
                if( e.getButton() == MouseEvent.BUTTON3 )
                {
                    rightClick();
                }
            }
        });

        _taDisplay.addFocusListener( new FocusListener(){
            public void focusGained( FocusEvent e )
            {
            }

            public void focusLost( FocusEvent e )
            {
                save();
            }
        });

        // TODO create a popup menu
        /*
        self.popup = Menu( self, tearoff=0 )
        self.popup.add_command( label="Edit Record",   command=self.edit_record )
        self.popup.add_command( label="Edit Template", command=self.edit_label_template )
        self.popup.add_command( label="Edit Text",     command=self.edit_display )
        self.popup.add_command( label="Refresh",       command=self.refresh )
        self.popup.add_separator()
        self.popup.add_command( label="Remove",        command=self.remove_record )
        */
    }

    public void rightClick()
    {
        if( _record != null )
        {
            // TODO Display popup menu
        }
    }


    public void editLabelTemplate()
    {
        /*
        if self._record is not None:
            EditTemplateDialog( self.app, self.app, Record.LABELS, self._record )
            self.refresh( True )
            */
    }


    /*
    def edit_record( self, event=None ):
        if self._record is not None:
            EditRecordDialog( self.app, self.app, self._record )
            self.refresh( True )


    def edit_display( self, event=None ):
        self.txtlabel.config( state=NORMAL )


    def remove_record( self, event=None ):
        if self._record is not None:
            self.app.records.remove( self._record )
            self.app.display_page( self.app.page )
            */


    public Record getRecord()
    {
        return _record;
    }


    public void setRecord( Record r )
    {
        _record = r;
        refresh();
    }

    public void refresh()
    {
        refresh( false );
    }

    public void refresh( boolean clearDisplay )
    {
        //_taDisplay.setEnabled( true );
        _taDisplay.setText( "" );
        if( _record != null )
        {
            if( clearDisplay )
                _record.setDisplay( null );
            _taDisplay.append( _record.getDisplay() );
        }
        //_taDisplay.setEnabled( false );
    }


    public void save()
    {
        if( _record != null )
        {
            _record.setDisplay( _taDisplay.getText() );
            _taDisplay.setEnabled( false );
        }
    }
}

