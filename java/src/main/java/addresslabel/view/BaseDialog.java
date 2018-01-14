package addresslabel.view;

import addresslabel.util.Util;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComponent;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class BaseDialog extends JDialog
{
    private JButton _btnCancel;
    private JButton _btnClose;
    private boolean _cancelled;
    private JPanel  _customButtonPanel;

    public BaseDialog( JFrame parent, String title, boolean modal, int width, int height )
    {
        super( parent, title, modal );
        _init( width, height );
    }

    public BaseDialog( JDialog parent, String title, boolean modal, int width, int height )
    {
        super( parent, title, modal );
        _init( width, height );
    }

    private void _init( int width, int height )
    {
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

        _customButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );

        JPanel buttonpanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        buttonpanel.add( _customButtonPanel );
        buttonpanel.add( _btnClose );
        buttonpanel.add( _btnCancel );

        getContentPane().setLayout( new BorderLayout() );
        getContentPane().add( buttonpanel, BorderLayout.SOUTH );
    }

    public void setContent( JComponent panel )
    {
        getContentPane().add( panel, BorderLayout.CENTER );
    }

    public void hideClose()
    {
        _btnClose.setVisible( false );
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

    public void setCloseText( String title )
    {
        _btnClose.setText( title );
    }

    public void setCancelText( String title )
    {
        _btnCancel.setText( title );
    }

    public boolean isCancelled(){ return _cancelled; }
    public boolean isCanceled(){ return _cancelled; }
    public void setCanceled( boolean v ){ _cancelled = v; }
    public void setCancelled( boolean v ){ _cancelled = v; }

    public JPanel getCustomButtonPanel(){ return _customButtonPanel; }
}

