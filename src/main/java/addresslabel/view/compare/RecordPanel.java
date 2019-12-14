package addresslabel.view.compare;

import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.compare.DiffType;
import addresslabel.compare.RecordDiff;

public class RecordPanel extends JPanel {
    private Model model;
    private Record record;
    private JPanel parent;

    public RecordPanel( int number, Record record, DiffType diffType, Model model, JPanel parent ) {
        super( new BorderLayout() );
        setBorder( BorderFactory.createLineBorder( Color.BLACK, 3 ) );
        this.model = model;
        this.parent = parent;

        JPanel northpanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        JLabel lblNumber = new JLabel( "" + number );
        northpanel.add( lblNumber );
        add( northpanel, BorderLayout.NORTH );

        List<String> keys = new ArrayList<>();
        for( String lbl: Record.LABELS ) {
            keys.add( lbl );
        }

        Set<String> recordKeysSet = new HashSet<>();
        recordKeysSet.addAll( record.getData().keySet() );

        List<String> recordKeys = new ArrayList<>( recordKeysSet );
        Collections.sort( recordKeys );
        for( String lbl: recordKeys )
        {
            // Only add lbl/keys that are not blank
            if( !keys.contains( lbl ) &&
                    (record.getData().containsKey( lbl ) && !record.getData().get( lbl ).trim().equals( "" )) )
            {
                keys.add( lbl );
            }
        }

        JPanel fieldspanel = new JPanel( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        for( int i = 0; i < keys.size(); ++i )
        {
            String key = keys.get( i );
            if( Record.LABEL_IGNORE.contains( key ) )
                continue;

            // Add Key Textfield
            JTextField keyentry = new JTextField( key );
            c.gridx = 0;
            c.gridy = i;
            fieldspanel.add( keyentry, c );

            // Add Record Textfield
            JTextField valentry = new JTextField();
            if( record.getData().containsKey( key ) ) {
                valentry.setText( record.getData().get( key ) );
            }
            c.gridx = 1;
            fieldspanel.add( valentry, c );
        }

        add( fieldspanel, BorderLayout.WEST );

        JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        if( diffType == DiffType.Added ) {
            JButton btnAdd = new JButton("Add Record");
            btnAdd.addActionListener( new AddRecordActionListener( record, this, model, parent ) );
            btnPanel.add(btnAdd);
        }
        if( diffType == DiffType.Removed ) {
            JButton btnRemove = new JButton("Remove Record");
            btnRemove.addActionListener( new RemoveRecordActionListener( record, this, model, parent ) );
            btnPanel.add(btnRemove);
        }
        add(btnPanel, BorderLayout.SOUTH);
    }

    private static class AddRecordActionListener implements ActionListener {
        private Record record;
        private Model model;
        private JPanel parent;
        private RecordPanel me;

        public AddRecordActionListener( Record record, RecordPanel me, Model model, JPanel parent ) {
            this.record = record;
            this.model = model;
            this.parent = parent;
            this.me = me;
        }

        public void actionPerformed( ActionEvent e ) {
            model.getRecords().add( record );
            parent.remove( this.me );
            parent.revalidate();
            parent.repaint();
        }
    }

    private static class RemoveRecordActionListener implements ActionListener {
        private Record record;
        private Model model;
        private JPanel parent;
        private RecordPanel me;

        public RemoveRecordActionListener( Record record, RecordPanel me, Model model, JPanel parent ) {
            this.record = record;
            this.model = model;
            this.parent = parent;
            this.me = me;
        }

        public void actionPerformed( ActionEvent e ) {
            model.getRecords().remove( record );
            parent.remove( this.me );
            parent.revalidate();
            parent.repaint();
        }
    }
}

