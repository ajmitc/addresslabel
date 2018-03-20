package addresslabel.view;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.template.Template;
import addresslabel.util.Logger;

import javax.swing.JPanel;

import java.awt.GridLayout;
import java.awt.Color;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.util.List;
import java.util.ArrayList;

public class SheetPanel extends JPanel
{
    private Logger _logger = Logger.getLogger( SheetPanel.class );
    private Model _model;
    private View _view;

    private List<LabelPanel> _labelPanels;
    private LabelPanel _highlighted;
    private JPanel _content;

    private Color _origBg;

    public SheetPanel( Model model, View view )
    {
        _model = model;
        _view = view;
        _highlighted = null;

        _labelPanels = new ArrayList<>();
        _content = new JPanel( new GridLayout() );
    }

    public void reset()
    {
        // TODO Remove old LabelPanels from grid
        _labelPanels.clear();

        for( int row = 0; row < _model.getTemplate().getRows(); ++row )
        {
            for( int col = 0; col < _model.getTemplate().getColumns(); ++col )
            {
                LabelPanel labelpanel = new LabelPanel();
                _content.add( labelpanel );
                _labelPanels.add( labelpanel );
            }
        }

        if( _labelPanels.size() > 0 )
            _origBg = _labelPanels.get( 0 ).getBackground();
        else
            _origBg = Color.WHITE;
    }


    /**
     * Display contact data.  data must be in format: [ Record, Record, ... ]
     */
    public void display( List<Record> records )
    {
        _logger.debug( "Displaying " + records.size() + " records" );
        for( LabelPanel lp: _labelPanels )
            lp.setRecord( null );
        for( int i = 0; i < Math.min( records.size(), _labelPanels.size() ); ++i )
        {
            LabelPanel lblpnl = _labelPanels.get( i );
            lblpnl.setRecord( records.get( i ) );
        }
    }


    public void saveLabels()
    {
        for( LabelPanel lp: _labelPanels )
            lp.save();
    }


    public void highlightLabelWithRecord( Record record )
    {
        if( _highlighted != null )
            _highlighted.setBackground( _origBg );

        for( LabelPanel lp: _labelPanels )
        {
            if( lp.getRecord() == record )
            {
                lp.setBackground( Color.RED );
                _highlighted = lp;
                break;
            }
        }
    }
}

