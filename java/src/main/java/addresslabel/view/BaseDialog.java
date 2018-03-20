package addresslabel.view;

import addresslabel.util.Util;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BaseDialog extends JDialog
{
    private JPanel _buttonpanel;
    private JButton _btnCancel;
    private JButton _btnClose;
    private boolean _cancelled;

    public BaseDialog( JFrame parent, String title, boolean modal, int width, int height )
    {
        super( parent, title, modal );
        setSize( width, height );
        Util.center( this );
        _cancelled = true;

        _btnCancel = new JButton( "Cancel" );
        _btnCancel.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                setVisible( false );
            }
        });
        _btnClose = new JButton( "Close" );
        _btnClose.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                _cancelled = false;
                setVisible( false );
            }
        });

        _buttonpanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        _buttonpanel.add( _btnClose );
        _buttonpanel.add( _btnCancel );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( _buttonpanel, BorderLayout.SOUTH );
    }

    public void setContent( JPanel panel )
    {
        getContentPane().add( panel, BorderLayout.CENTER );
    }

    public void setContent( JScrollPane panel )
    {
        getContentPane().add( panel, BorderLayout.CENTER );
    }

    public JPanel getButtonPanel(){ return _buttonpanel; }

    public void hideClose( boolean v )
    {
        _btnClose.setVisible( v );
    }

    public void hideClose()
    {
        _btnClose.setVisible( false );
    }

    public void hideCancel( boolean v )
    {
        _btnCancel.setVisible( !v );
    }

    public void hideCancel()
    {
        _btnCancel.setVisible( false );
    }

    public void addCancelActionListener( ActionListener listener )
    {
        _btnCancel.addActionListener( listener );
    }

    public void addCancelActionListener( String title, ActionListener listener )
    {
        _btnCancel.setText( title );
        _btnCancel.addActionListener( listener );
    }

    public void addCloseActionListener( ActionListener listener )
    {
        _btnClose.addActionListener( listener );
    }

    public void addCloseActionListener( String title, ActionListener listener )
    {
        _btnClose.setText( title );
        _btnClose.addActionListener( listener );
    }

    public JButton getCloseButton(){ return _btnClose; }
    public JButton getCancelButton(){ return _btnCancel; }
}

