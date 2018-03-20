package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;

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
    private Map<JTextField, JTextField> _fieldMap;

    public EditRecordDialog( JFrame parent, Record record )
    {
        super( parent, "Edit Record", true, 400, 400 );
        _record = record;

        JPanel content = new JPanel( new BorderLayout() );

        JLabel lblHelpTxt = new JLabel( "Edit the record below." );
        content.add( lblHelpTxt, BorderLayout.NORTH );

        JPanel fields = new JPanel( new GridLayout( _record.getData().size(), 2 ) );
        content.add( new JScrollPane( fields ), BorderLayout.CENTER );

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

        _fieldMap = new HashMap<JTextField, JTextField>();
        for( int i = 0; i < keys.size(); ++i )
        {
            String key = keys.get( i );
            if( Record.LABEL_IGNORE.contains( key ) )
                continue;
            JTextField keyfield = new JTextField( key );
            fields.add( keyfield );
            JTextField valfield = new JTextField( _record.getData().get( key ) );
            fields.add( valfield );
            _fieldMap.put( keyfield, valfield );
        }

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
        for( JTextField keyfield: _fieldMap.keySet() )
        {
            JTextField valfield = _fieldMap.get( keyfield );
            _record.getData().put( keyfield.getText(), valfield.getText() );
        }
    }
}

