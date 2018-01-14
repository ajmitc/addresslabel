package addresslabel.view;

import javax.swing.JPanel;

import java.awt.Color;
import java.awt.GridLayout;

import java.util.List;
import java.util.ArrayList;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.util.Logger;
import addresslabel.util.SheetTemplate;

public class SheetPanel extends JPanel
{
    public static final Color HIGHLIGHT_COLOR = Color.RED;

    private Logger _logger;
    private Model _model;
    private View _view;
    private SheetTemplate _sheetTemplate;
    private List<LabelPanel> _labelPanels;
    private LabelPanel _highlighted;
    private Color _origBg;

    public SheetPanel( Model model, View view, SheetTemplate sheetTemplate )
    {
        super();
        setLayout( new GridLayout( sheetTemplate.getRows(), sheetTemplate.getColumns() ) );

        _logger = Logger.getLogger( "SheetFrame" );
        _model  = model;
        _view   = view;
        _sheetTemplate = sheetTemplate;
        _labelPanels = new ArrayList<LabelPanel>();
        _highlighted = null;

        for( int row = 0; row < _sheetTemplate.getRows(); ++row )
        {
            for( int col = 0; col < _sheetTemplate.getColumns(); ++col )
            {
                LabelPanel txtlabel = new LabelPanel( _model, _view );
                add( txtlabel );
                _labelPanels.add( txtlabel );
            }
        }

        _origBg = (_labelPanels.size() > 0)? _labelPanels.get( 0 ).getBackground(): Color.WHITE;
    }


    /**
     * Display contact data.
     */
    public void display( List<Record> records )
    {
        for( LabelPanel t: _labelPanels )
            t.setRecord( null );
        for( int i = 0; i < Math.min( records.size(), _labelPanels.size() ); ++i )
        {
            LabelPanel lblfrm = _labelPanels.get( i );
            lblfrm.setRecord( records.get( i ) );
        }
    }


    public void saveLabels()
    {
        for( LabelPanel t: _labelPanels )
            t.save();
    }


    public void highlightLabelWithRecord( Record record )
    {
        if( _highlighted != null )
            _highlighted.setBackground( _origBg );
        for( LabelPanel txtlabel: _labelPanels )
        {
            if( txtlabel.getRecord() == record )
            {
                txtlabel.setBackground( HIGHLIGHT_COLOR );
                _highlighted = txtlabel;
            }
        }
    }

    public SheetTemplate getSheetTemplate(){ return _sheetTemplate; }
    public void setSheetTemplate( SheetTemplate t ){ _sheetTemplate = t; }

    public List<LabelPanel> getLabelPanels(){ return _labelPanels; }
}

