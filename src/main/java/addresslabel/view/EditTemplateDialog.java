package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.ArrayList;

import addresslabel.Model;
import addresslabel.Record;

public class EditTemplateDialog extends BaseDialog
{
    //private Map<JButton, String> _insertBtns;
    private Model _model;
    private View _view;
    private Record _record;
    private JTextArea _txtTemplate;

    /**
     * @param model Model
     * @param view View
     * @param record Record
     */
    public EditTemplateDialog( Model model, View view, Record record )
    {
        super( view.getFrame(), "Edit Template", true, 600, 800 );
        _model = model;
        _view = view;
        _record = record;

        //_insertBtns = new HashMap<>();  // { button: label }

        JPanel content = new JPanel( new BorderLayout() );

        JLabel lblHelpTxt = new JLabel( "<html><center>Edit the template below.<br/>Click Apply to change only the selected label.  Click Apply to All to change all label templates.</html>" );
        content.add( lblHelpTxt, BorderLayout.NORTH );

        JPanel templPanel = new JPanel( new BorderLayout() );
        content.add( templPanel, BorderLayout.CENTER );

        _txtTemplate = new JTextArea();
        templPanel.add( new JScrollPane( _txtTemplate ), BorderLayout.NORTH );


        JPanel lblpanel = new JPanel( new GridLayout( Record.LABELS.length, 2 ) );
        templPanel.add( new JScrollPane( lblpanel ), BorderLayout.CENTER );

        for( int i = 0; i < Record.LABELS.length; ++i )
        {
            final String lbltxt = Record.LABELS[ i ];

            JLabel lbl = new JLabel( lbltxt );
            lblpanel.add( lbl );

            JButton btn = new JButton( "Insert" );
            btn.addActionListener( new ActionListener(){
                public void actionPerformed( ActionEvent e )
                {
                    insertLabel( lbltxt );
                }
            });
            lblpanel.add( btn );
        }

        _txtTemplate.setText( _record.getTemplate() );

        JButton btnApplyAll = new JButton( "Apply to All" );
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
                _view.displayPage();
            }
        });
    }


    public void insertLabel( String lbl )
    {
        //_txtTemplate.getDocument().insertString( _txtTemplate.getCaretPosition(), s, null );
        _txtTemplate.insert( "{" + lbl + "}", _txtTemplate.getCaretPosition() );
        _txtTemplate.insert( lbl.equals( "city" )? ", ": " ", _txtTemplate.getCaretPosition() );
    }


    public boolean validateTemplate()
    {
        return true;
    }


    public void apply()
    {
        String template = _txtTemplate.getText();
        _record.setTemplate( template );
        _view.displayPage();
    }


    public void applyAll()
    {
        if( !validateTemplate() )
        {
            // self.initial_focus.focus_set()  # put focus back
            return;
        }
        String template = _txtTemplate.getText();
        for( Record record: _model.getRecords() )
        {
            record.setTemplate( template );
        }
        _view.displayPage();
    }
}
