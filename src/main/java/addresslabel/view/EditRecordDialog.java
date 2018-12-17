package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import addresslabel.Record;

public class EditRecordDialog extends BaseDialog
{
    private Record _record;
    private Map<JTextField, JTextField> _fields;

    public EditRecordDialog( JFrame parent, Record record )
    {
        super( parent, "Edit Record", true, 500, 400 );
        _record = record;

        JPanel content = new JPanel( new BorderLayout() );

        JLabel lblHelpTxt = new JLabel( "Edit the record below." );
        content.add( lblHelpTxt, BorderLayout.NORTH );

        List<String> keys = new ArrayList<>();
        for( String lbl: Record.LABELS )
            keys.add( lbl );

        List<String> recordKeys = new ArrayList<>( _record.getData().keySet() );
        Collections.sort( recordKeys );
        for( String lbl: recordKeys )
        {
            if( !keys.contains( lbl ) )
            {
                keys.add( lbl );
            }
        }

        _fields = new HashMap<>();
        //JPanel fieldspanel = new JPanel( new GridLayout( keys.size(), 2 ) );
        JPanel fieldspanel = new JPanel( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.PAGE_START;
        c.ipadx = 4;
        c.ipady = 4;
        c.weightx = 1.0;
        c.weighty = 1.0;
        for( int i = 0; i < keys.size(); ++i )
        {
            String key = keys.get( i );
            if( Record.LABEL_IGNORE.contains( key ) )
                continue;
            JTextField keyentry = new JTextField( key );
            keyentry.setEditable( false );
            keyentry.setFocusable( false );
            c.gridx = 0;
            c.gridy = i;
            fieldspanel.add( keyentry, c );
            JTextField valentry = new JTextField( _record.getData().get( key ) );
            c.gridx = 1;
            fieldspanel.add( valentry, c );
            _fields.put( keyentry, valentry );
        }

        content.add( new JScrollPane( fieldspanel ), BorderLayout.CENTER );
        setContent( content );
        
        addCloseActionListener( "Apply", new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                apply();
            }
        });
    }


    public void apply()
    {
        _record.clearData();
        for( JTextField keyfield: _fields.keySet() )
        {
            JTextField valfield = _fields.get( keyfield );
            _record.getData().put( keyfield.getText(), valfield.getText() );
        }
    }
}

