package addresslabel.view;

import addresslabel.Model;
import addresslabel.Record;
import addresslabel.util.SearchResult;
import addresslabel.util.SheetTemplate;
import addresslabel.util.Logger;
import addresslabel.action.*;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.ButtonGroup;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.Dimension;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FilenameFilter;

public class View
{
    private Logger _logger;
    private Model _model;
    private JFrame _frame;

    private JLabel _lblPage;

    private SheetPanel _sheetpanel;
    private JTextField _tfSearch;

    private FileDialog _fileDialog;

    public View( Model model, JFrame frame )
    {
        _logger = Logger.getLogger( "View" );
        _model = model;
        _frame = frame;

        _frame.getContentPane().setLayout( new BorderLayout() );

        createMenuBar();
        createToolBar();

        _sheetpanel = new SheetPanel( _model, this, _model.getTemplate() );

        _frame.getContentPane().add( _sheetpanel, BorderLayout.CENTER );

        _fileDialog = new FileDialog( _frame );
        _fileDialog.setFilenameFilter( new FilenameFilter(){
            public boolean accept( File dir, String name )
            {
                return name.endsWith( ".csv" );
            }
        });
    }

  
    private void createMenuBar()
    {
        JMenuBar menubar = new JMenuBar();
        JMenu filemenu = new JMenu( "File" );
        menubar.add( filemenu );

        JMenuItem menuitem = new JMenuItem( new OpenCsvAction( _model, this ) );
        filemenu.add( menuitem );
        filemenu.addSeparator();

        menuitem = new JMenuItem( new SaveCsvAction( _model, this, false ) );
        filemenu.add( menuitem );
        menuitem = new JMenuItem( new SaveCsvAction( _model, this, true ) );
        filemenu.add( menuitem );
        filemenu.addSeparator();

        menuitem = new JMenuItem( new ExportToPdfAction( _model, this ) );
        filemenu.add( menuitem );
        menuitem = new JMenuItem( new PrintLabelsAction( _model, this ) );
        filemenu.add( menuitem );
        filemenu.addSeparator();

        menuitem = new JMenuItem( "Exit" );
        menuitem.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                _model.exit();
            }
        });
        filemenu.add( menuitem );



        JMenu templmenu = new JMenu( "Template" );
        templmenu.add( new AddPageAction( _model, this ) );
        templmenu.addSeparator();
        templmenu.add( new SortByLastNameAction( _model, this ) );
        templmenu.add( new SortByCountryAction( _model, this ) );
        templmenu.addSeparator();
        ButtonGroup templgroup = new ButtonGroup();
        for( int idx = 0; idx < Model.TEMPLATES.length; ++idx )
        {
            SheetTemplate templ = Model.TEMPLATES[ idx ];
            JRadioButtonMenuItem rbmenuitem = new JRadioButtonMenuItem( new SelectTemplateAction( this, templ.getName(), idx ) );
            rbmenuitem.setSelected( idx == 0 );
            templmenu.add( rbmenuitem );
            templgroup.add( rbmenuitem );
        }
        menubar.add( templmenu );


        JMenu helpmenu = new JMenu( "Help" );
        helpmenu.add( new DisplayHelpAction( this ) );
        helpmenu.add( new DisplayAboutAction( this ) );
        menubar.add( helpmenu );

        _frame.setJMenuBar( menubar );
    }


    private void createToolBar()
    {
        JToolBar frmToolbar = new JToolBar();
        frmToolbar.add( new OpenCsvAction( _model, this ) );

        frmToolbar.addSeparator();

        frmToolbar.add( new ExportToPdfAction( _model, this ) );

        frmToolbar.add( new PrintLabelsAction( _model, this ) );

        frmToolbar.addSeparator();

        frmToolbar.add( new JLabel( "Search: " ) );
        _tfSearch = new JTextField( "" );
        _tfSearch.addKeyListener( new KeyAdapter(){
            public void keyReleased( KeyEvent e )
            {
                searchKeyReleased();
            }
        });
        frmToolbar.add( _tfSearch );
        JButton btnSearchNext = new JButton( "Next" );
        btnSearchNext.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e )
            {
                findSearchNext();
            }
        });
        frmToolbar.add( btnSearchNext );

        frmToolbar.addSeparator();

        _lblPage = new JLabel( "Page 0 of 0" );
        frmToolbar.add( _lblPage );

        frmToolbar.addSeparator( new Dimension( 5, 1 ) );

        frmToolbar.add( new DisplayPageAction( _model, this, DisplayPageAction.PREV ) );
        frmToolbar.add( new DisplayPageAction( _model, this, DisplayPageAction.NEXT ) );

        _frame.getContentPane().add( frmToolbar, BorderLayout.NORTH );
    }


    public void selectTemplate( int idx )
    {
        _model.setTemplate( idx );
        SheetTemplate templ = _model.getTemplate();
        _sheetpanel.setSheetTemplate( templ );
        if( _model.getRecords().size() > 0 )
        {
            _model.setPage( 0 );
            _sheetpanel.display( _model.getRecords() );
        }
    }


    public void searchKeyReleased()
    {
        if( _model.getRecords().size() == 0 )
            return;
        _model.getSearchResults().clear();
        _model.setSearchResultsIdx( 0 );
        String search = _tfSearch.getText();
        _logger.debug( String.format( "Searching for '%s'", search ) );
        for( int i = 0; i < _model.getRecords().size(); ++i )
        {
            Record record = _model.getRecords().get( i );
            if( record.search( search ) )
            {
                _model.getSearchResults().add( new SearchResult( i, record ) );
                _model.setSearchResultsIdx( -1 );
            }
        }
        if( _model.getSearchResults().size() > 0 )
        {
            findSearchNext();
        }
    }


    public void findSearchNext()
    {
        if( _model.getSearchResults().size() == 0 )
            return;
        _model.setSearchResultsIdx( (_model.getSearchResultsIdx() + 1) % _model.getSearchResults().size() );
        SearchResult result = _model.getSearchResults().get( _model.getSearchResultsIdx() );
        // Get page
        int recordsPerPage = getRecordsPerPage();
        int page = result.index / recordsPerPage;
        displayPage( page );
        _sheetpanel.highlightLabelWithRecord( result.record );
    }

    public void displayPage()
    {
        displayPage( _model.getPage() );
    }

    public void displayPage( int pagenum )
    {
        _model.setPage( pagenum );
        int recordsPerPage = getRecordsPerPage();
        if( _model.getPage() * recordsPerPage > _model.getRecords().size() )
        {
            _sheetpanel.display( new ArrayList<Record>() );
        }
        else
            _sheetpanel.display( _model.getRecords().subList( _model.getPage() * recordsPerPage, _model.getRecords().size() ) );
        int numPages = (int) (Math.ceil( (float) _model.getRecords().size() / (float) recordsPerPage) );
        _lblPage.setText( String.format( "Page %d of %d", _model.getPage() + 1, numPages ) );
    }

    public void refresh()
    {
        displayPage();
    }

    public int getRecordsPerPage()
    {
        return _model.getTemplate().getRows() * _model.getTemplate().getColumns();
    }

    public JFrame getFrame(){ return _frame; }
    public FileDialog getFileDialog(){ return _fileDialog; }
    public SheetPanel getSheetPanel(){ return _sheetpanel; }
}

