package addresslabel.view;

import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.BorderFactory;
import javax.swing.UIManager;

import javax.swing.text.Style;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.util.Logger;

public class LabelPanel extends JPanel implements ActionListener
{
    private Logger _logger;
    private Model _model;
    private View _view;

    private JTextPane _tpLabel;
    private Record _record;
    private JPopupMenu _popup;


    public LabelPanel( Model model, View view )
    {
        super();
        setLayout( new BorderLayout() );

        _logger = Logger.getLogger( "LabelPanel" );
        _model = model;
        _view  = view;

        _record = null;

        _tpLabel = new JTextPane();
        _tpLabel.setEditable( false );
        _tpLabel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ), BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.BLACK ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) ) );
        //StyledDocument doc = _tpLabel.getStyledDocument();

        _tpLabel.addFocusListener( new FocusListener(){
            public void focusLost( FocusEvent e )
            {
                save();
            }

            public void focusGained( FocusEvent e )
            {

            }
        });

        add( new JScrollPane( _tpLabel ), BorderLayout.CENTER );

        //Create the popup menu.
        _popup = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem( "Edit Record" );
        menuItem.addActionListener( this ); _popup.add( menuItem ); menuItem = new JMenuItem( "Edit Template" ); menuItem.addActionListener( this );
        _popup.add( menuItem );
        menuItem = new JMenuItem( "Edit Text" );
        menuItem.addActionListener( this );
        _popup.add( menuItem );
        menuItem = new JMenuItem( "Refresh" );
        menuItem.addActionListener( this );
        _popup.add( menuItem );
        _popup.addSeparator();
        menuItem = new JMenuItem( "Remove" );
        menuItem.addActionListener( this );
        _popup.add( menuItem );

        //Add listener to components that can bring up popup menus.
        MouseListener popupListener = new PopupListener();
        _tpLabel.addMouseListener( popupListener );
    }


    public void actionPerformed( ActionEvent e )
    {
        JMenuItem menuitem = (JMenuItem) e.getSource();
        if( menuitem.getLabel().equals( "Edit Record" ) )
        {
            if( _record != null )
            {
                EditRecordDialog d = new EditRecordDialog( _view.getFrame(), _record );
                d.setVisible( true );
                refresh( true );
            }
        }
        else if( menuitem.getLabel().equals( "Edit Template" ) )
        {
            if( _record != null )
            {
                EditTemplateDialog d = new EditTemplateDialog( _model, _view, _record );
                d.setVisible( true );
                refresh( true );
            }
        }
        else if( menuitem.getLabel().equals( "Edit Text" ) )
        {
            if( _record != null )
            {
                _tpLabel.setEditable( true );
            }
        }
        else if( menuitem.getLabel().equals( "Refresh" ) )
        {
            refresh();
        }
        else if( menuitem.getLabel().equals( "Remove" ) )
        {
            if( _record != null )
            {
                _model.getRecords().remove( _record );
                _view.displayPage();
            }
        }
    }


    public void setFontFamily( String font )
    {
        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        if( font.equalsIgnoreCase( "system" ) )
        {
            Font sysfont = UIManager.getFont("Label.font");
            font = sysfont.getFamily();
        }

        Style bodyStyle = ((HTMLDocument) _tpLabel.getDocument()).getStyleSheet().getRule( "body" );
        if( bodyStyle == null )
        {
            addBodyRule( "font-family: " + font );
        }

        bodyStyle.removeAttribute( "font-family" );
        bodyStyle.addAttribute( "font-family", font );
    }

    public void setFontSize( int size )
    {
        // add a CSS rule to force body tags to use the default label font
        // instead of the value in javax.swing.text.html.default.csss
        if( size < 0 )
        {
            Font sysfont = UIManager.getFont("Label.font");
            size = sysfont.getSize();
        }

        Style bodyStyle = ((HTMLDocument) _tpLabel.getDocument()).getStyleSheet().getRule( "body" );
        if( bodyStyle == null )
        {
            addBodyRule( "font-size: " + size + "pt;" );
        }

        bodyStyle.removeAttribute( "font-size" );
        bodyStyle.addAttribute( "font-size", size );
    }

    public void addBodyRule( String rule )
    {
        String bodyRule = "body { " + rule + " }";
        ((HTMLDocument) _tpLabel.getDocument()).getStyleSheet().addRule( bodyRule );
    }

    public void setLabelBackground( Color color ) {
        _tpLabel.setBackground( color );
    }


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
        _tpLabel.setText( "" );
        if( _record != null && _record.isUsed() )
        {
            if( clearDisplay )
                _record.setDisplay( null );
            _tpLabel.setText( _record.getDisplay() );
        }
    }


    public void save()
    {
        if( _record != null )
        {
            _record.setDisplay( _tpLabel.getText() );
            _tpLabel.setEditable( false );
        }
    }


    class PopupListener extends MouseAdapter {
        public void mousePressed( MouseEvent e )
        {
            maybeShowPopup( e );
        }

        public void mouseReleased( MouseEvent e )
        {
            maybeShowPopup( e );
        }

        private void maybeShowPopup( MouseEvent e )
        {
            //_logger.debug( "isPopupTrigger: " + e.isPopupTrigger() );
            if( e.isPopupTrigger() )
            {
                _popup.show( e.getComponent(), e.getX(), e.getY() );
            }
        }
    }
}

