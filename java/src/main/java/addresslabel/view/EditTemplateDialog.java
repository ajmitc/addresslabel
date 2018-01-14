package addresslabel.view;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JButton;

import java.awt.BorderLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.Map;
import java.util.HashMap;

import addresslabel.Model;
import addresslabel.Record;

public class EditTemplateDialog extends BaseDialog
{
    private Map<JButton, String> _insertBtns;
    private JTextArea _txtTemplate;
    private Model _model;
    private View _view;
    private Record _record;

    public EditTemplateDialog( Record record, String[] templLabels, Model model, View view )
    {
        super( view.getFrame(), "Edit Template", true, 400, 400 );
        _record = record;
        _model  = model;
        _view   = view;
        _insertBtns = new HashMap<>();  // { button: label }

        JPanel content = new JPanel( new BorderLayout() );

        JLabel lblHelpTxt = new JLabel( "<html><center>Edit the template below.<br/>Click Apply to change only the selected label.  Click Apply to All to change all label templates." );
        content.add( lblHelpTxt, BorderLayout.NORTH );

        JPanel templPanel = new JPanel( new BorderLayout() );
        content.add( templPanel, BorderLayout.CENTER );

        _txtTemplate = new JTextArea();
        templPanel.add( new JScrollPane( _txtTemplate ), BorderLayout.NORTH );

        JPanel lblpanel = new JPanel();
        templPanel.add( new JScrollPane( lblpanel ), BorderLayout.CENTER );

        for( String lbltxt: templLabels )
        {
            JLabel lbl = new JLabel( lbltxt );
            JButton btn = new JButton( "Insert" );
            btn.addActionListener( new ActionListener(){
                public void actionPerformed( ActionEvent e )
                {
                    insertLabel( _insertBtns.get( (JButton) e.getSource() ) );
                }
            });
            lblpanel.add( btn );
            _insertBtns.put( btn, lbltxt );
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
            }
        });
    }


    public void insertLabel( String s )
    {
        try
        {
            _txtTemplate.getDocument().insertString( _txtTemplate.getCaretPosition(), s, null );
            if( s == "{city}" )
                _txtTemplate.getDocument().insertString( _txtTemplate.getCaretPosition(), ", ", null );
            else
                _txtTemplate.getDocument().insertString( _txtTemplate.getCaretPosition(), " ", null );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }


    public void apply()
    {
        String template = _txtTemplate.getText();
        _record.setTemplate( template );
        _view.refresh();
    }


    public void applyAll()
    {
        String template = _txtTemplate.getText();
        _view.refresh();
        for( Record record: _model.getRecords() )
        {
            record.setTemplate( template );
        }
        _view.refresh();
    }
}
