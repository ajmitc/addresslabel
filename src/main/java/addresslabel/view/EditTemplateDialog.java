package addresslabel.view;

import javax.swing.*;

import java.awt.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import addresslabel.Model;
import addresslabel.Record;

public class EditTemplateDialog extends BaseDialog
{
    private Model model;
    private View view;
    private Record record;
    private JTextArea txtTemplate;
    private JTextPane tpPreview;

    /**
     * @param model Model
     * @param view View
     * @param record Record
     */
    public EditTemplateDialog( Model model, View view, Record record )
    {
        super( view.getFrame(), "Edit Template", true, 550, 600 );
        this.model = model;
        this.view = view;
        this.record = record;

        JPanel content = new JPanel( new BorderLayout() );

        JLabel lblHelpTxt = new JLabel( "<html><center>Edit the template below.<br/>Words/phrases within curly-braces will be substituted with the value of that field.<br/>Words/phrases outside curly-braces will be visible in the label as is.</html>" );
        content.add( lblHelpTxt, BorderLayout.NORTH );

        JPanel templPanel = new JPanel( new BorderLayout() );
        content.add( templPanel, BorderLayout.CENTER );

        txtTemplate = new JTextArea();
        txtTemplate.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                refreshPreview();
            }
        });
        templPanel.add( new JScrollPane(txtTemplate), BorderLayout.NORTH );

        tpPreview = new JTextPane();
        tpPreview.setEditable( false );
        tpPreview.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ), BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.BLACK ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) ) );
        templPanel.add(new JScrollPane(tpPreview), BorderLayout.CENTER);

        JPanel lblpanel = new JPanel( new GridBagLayout() ); //new GridLayout( Record.LABELS.length, 2 ) );
        lblpanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        templPanel.add( new JScrollPane( lblpanel ), BorderLayout.WEST );

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        //c.weightx = 1.0;
        //c.weighty = 1.0;
        for( int i = 0; i < Record.LABELS.length; ++i )
        {
            final String lbltxt = Record.LABELS[ i ];

            JLabel lbl = new JLabel( "{" + lbltxt + "}" );
            c.gridx = 0;
            c.gridy = i;
            lblpanel.add( lbl, c );

            JButton btn = new JButton( "Insert" );
            btn.addActionListener( new ActionListener(){
                public void actionPerformed( ActionEvent e )
                {
                    insertLabel( lbltxt );
                }
            });
            c.gridx = 1;
            c.gridy = i;
            lblpanel.add( btn, c );
        }

        txtTemplate.setText( this.record.getTemplate() );

        JButton btnApplyAll = new JButton( "Apply Template to All Labels" );
        btnApplyAll.addActionListener( new ActionListener(){
           public void actionPerformed( ActionEvent e )
           {
               applyAll();
           }
        });
        getCustomButtonPanel().add( btnApplyAll );

        addCloseActionListener( "Apply", new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                apply();
                EditTemplateDialog.this.view.displayPage();
            }
        });
        setContent( content );

        refreshPreview();
    }


    public void insertLabel( String lbl )
    {
        //_txtTemplate.getDocument().insertString( _txtTemplate.getCaretPosition(), s, null );
        txtTemplate.insert( "{" + lbl + "}", txtTemplate.getCaretPosition() );
        txtTemplate.insert( lbl.equals( "city" )? ", ": " ", txtTemplate.getCaretPosition() );
        refreshPreview();
    }


    public boolean validateTemplate()
    {
        return true;
    }


    public void apply()
    {
        String template = txtTemplate.getText();
        record.setTemplate( template );
        view.displayPage();
    }


    public void applyAll()
    {
        if( !validateTemplate() )
        {
            // self.initial_focus.focus_set()  # put focus back
            return;
        }
        String template = txtTemplate.getText();
        for( Record record: model.getRecords() )
        {
            record.setTemplate( template );
        }
        view.displayPage();
    }

    private void refreshPreview(){
        tpPreview.setText( "" );
        if( record != null && record.isUsed() )
        {
            String template = txtTemplate.getText();
            tpPreview.setText(record.format(template));
        }
    }
}

