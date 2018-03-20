package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.ButtonGroup;

import java.awt.FileDialog;
import java.awt.BorderLayout;

import java.io.File;
import java.io.FilenameFilter;

import java.util.List;
import java.util.ArrayList;

import addresslabel.Model;
import addresslabel.action.*;
import addresslabel.template.Template;
import addresslabel.util.Logger;

public class View
{
    private Logger _logger;
    private Model  _model;
    private JFrame _frame;

    private JTextField _tfSearch;
    private JButton _btnSearchNext;
    private JButton _btnNextPage;
    private JButton _btnPrevPage;
    private JLabel  _lblPage;

    private SheetPanel _sheetpanel;

    private FileDialog _fileDialog;

    public View( Model model, JFrame frame )
    {
        _logger = Logger.getLogger( "Model" );
        _model  = model;
        _frame  = frame;

        _fileDialog = new FileDialog( _frame, "Choose a file", FileDialog.LOAD );
        _fileDialog.setDirectory( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" )? "C:\\": "~" );
        _fileDialog.setFile( "*.csv" );

        JMenu filemenu = new JMenu( "File" );
        filemenu.add( new NewAction( _model, this ) );
        filemenu.addSeparator();
        filemenu.add( new OpenCsvAction( _model, this ) );
        filemenu.add( new SaveAction( _model, this ) );
        filemenu.add( new SaveAsAction( _model, this ) );
        filemenu.addSeparator();
        filemenu.add( new SaveCsvAction( _model, this ) );
        filemenu.add( new SaveCsvAsAction( _model, this ) );
        filemenu.addSeparator();
        filemenu.add( new ExportToPdfAction( _model, this ) );
        filemenu.add( new PrintAction( _model, this ) );
        filemenu.addSeparator();
        filemenu.add( new ExitAction( _model, this ) );

        JMenu templmenu = new JMenu( "Template" );
        ButtonGroup group = new ButtonGroup();
        for( int i = 0; i < Model.TEMPLATES.length; ++i )
        {
            Template templ = Model.TEMPLATES[ i ];
            JRadioButtonMenuItem rbmi = new JRadioButtonMenuItem( new SelectTemplateAction( _model, this, templ ) );
            templmenu.add( rbmi );
            group.add( rbmi );
            if( i == 0 )
                rbmi.setSelected( true );
        }

        JMenu helpmenu = new JMenu( "Help" );
        helpmenu.add( new DisplayManualAction( _model, this ) );
        helpmenu.add( new DisplayAboutAction( _model, this ) );

        JMenuBar menubar = new JMenuBar();
        menubar.add( filemenu );
        menubar.add( templmenu );
        menubar.add( helpmenu );

        _frame.setJMenuBar( menubar );


        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable( false );
        toolbar.setRollover( true );
        toolbar.add( new NewAction( _model, this ) );
        toolbar.add( new OpenCsvAction( _model, this ) );
        toolbar.add( new SaveCsvAction( _model, this ) );
        toolbar.addSeparator();
        toolbar.add( new ExportToPdfAction( _model, this ) );
        toolbar.add( new PrintAction( _model, this ) );
        toolbar.addSeparator();
        toolbar.add( new JLabel( "Search: " ) );

        _tfSearch = new JTextField( 10 );
        _btnSearchNext = new JButton( "Next" );
        toolbar.add( _tfSearch );
        toolbar.add( _btnSearchNext );

        _btnNextPage = new JButton( ">" );
        _btnPrevPage = new JButton( "<" );
        _lblPage = new JLabel( "Page 0 of 0" );
        toolbar.add( _btnNextPage );
        toolbar.add( _btnPrevPage );
        toolbar.add( _lblPage );

        _sheetpanel = new SheetPanel( _model, this );

        _frame.getContentPane().setLayout( new BorderLayout() );
        _frame.getContentPane().add( toolbar, BorderLayout.PAGE_START );
        _frame.getContentPane().add( _sheetpanel, BorderLayout.CENTER );
    }


    public void displayPage()
    {
        int recordsPerPage = _model.getRecordsPerPage();
        _sheetpanel.display( _model.getRecords().subList( _model.getPage() * recordsPerPage, _model.getRecords().size() ) );
        _lblPage.setText( "Page " + _model.getPage() + 1 + " of " + _model.getNumPagesToFitRecords() );
    }

    public JFrame getFrame(){ return _frame; }
    public SheetPanel getSheetPanel(){ return _sheetpanel; }
    public JTextField getTfSearch(){ return _tfSearch; }
    public JButton getBtnSearchNext(){ return _btnSearchNext; }
    public JButton getBtnNextPage(){ return _btnNextPage; }
    public JButton getBtnPrevPage(){ return _btnPrevPage; }

    public FileDialog getFileDialog(){ return _fileDialog; }

    public FileDialog getLoadCsvFileDialog()
    {
        _fileDialog.setMode( FileDialog.LOAD );
        _fileDialog.setFilenameFilter( new FilenameFilter(){
            public boolean accept( File dir, String name )
            {
                return name.endsWith( ".csv" );
            }
        });
        return _fileDialog;
    }

    public FileDialog getSaveCsvFileDialog()
    {
        _fileDialog.setMode( FileDialog.SAVE );
        _fileDialog.setTitle( "Open Contact List" );
        _fileDialog.setFilenameFilter( new FilenameFilter(){
            public boolean accept( File dir, String name )
            {
                return name.endsWith( ".csv" );
            }
        });
        return _fileDialog;
    }
}

