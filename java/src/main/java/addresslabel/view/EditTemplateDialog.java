package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextArea;

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

        JPanel content = new JPanel( new BorderLayout() );

        JLabel lblHelpTxt = new JLabel( "<html>Edit the template below.<br/>Click Apply to change only the selected label.  Click Apply to All to change all label templates.</html>" );
        content.add( lblHelpTxt, BorderLayout.NORTH );

        _txtTemplate = new JTextArea( 5, 5 );
        content.add( _txtTemplate, BorderLayout.CENTER );

        JPanel pnlHelp = new JPanel( new GridLayout( Record.LABELS.length, 2 ) );

        for( int i = 0; i < Record.LABELS.length; ++i )
        {
            final String lbltxt = Record.LABELS[ i ];

            JLabel lbl = new JLabel( lbltxt );
            pnlHelp.add( lbl );

            JButton btn = new JButton( "Insert" );
            btn.addActionListener( new ActionListener(){
                public void actionPerformed( ActionEvent e )
                {
                    insertLabel( lbltxt );
                }
            });
            pnlHelp.add( btn );

        }
        _txtTemplate.append( _record.getTemplate() );

        JButton btnApplyAll = new JButton( "Apply to All" );
        btnApplyAll.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                applyAll();
            }
        });
        getButtonPanel().add( btnApplyAll );

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

