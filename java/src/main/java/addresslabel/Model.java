package addresslabel;

import java.util.List;
import java.util.ArrayList;

import addresslabel.template.*;
import addresslabel.util.Logger;
import addresslabel.util.SearchResult;

public class Model
{
    public static final String VERSION = "1.0.0";

    public static final String CONFIG_FILE = "addrlbl.conf";

    public static final Template[] TEMPLATES = {
        new Avery5160Template()
    };

    private Logger _logger;
    private Template _template; // selected template
    private List<List<String>> _header;
    private List<Record> _records;
    private String _loadedFilepath;
    private int _page;
    private String _defLabelTemplate;
    private List<SearchResult> _searchResults;
    private int _searchResultsIdx;

    public Model()
    {
        _logger = Logger.getLogger( Model.class );
        _template = TEMPLATES[ 0 ];
        _header = null;
        _records = new ArrayList<>();
        _loadedFilepath = null;
        _page = 0;  // Currently displayed page

        // default format template
        _defLabelTemplate  = String.format( "{%s} {%s} {%s} {%s} {%s}\n", Record.TITLE, Record.FIRST_NAME, Record.MIDDLE_NAME, Record.LAST_NAME, Record.SUFFIX );
        _defLabelTemplate += String.format( "{%s}\n", Record.ADDRESS_STREET_1 );
        _defLabelTemplate += String.format( "{%s}\n", Record.ADDRESS_STREET_2 );
        _defLabelTemplate += String.format( "{%s}, {%s} {%s}\n", Record.ADDRESS_CITY, Record.ADDRESS_STATE, Record.ADDRESS_ZIP );
        _defLabelTemplate += String.format( "{%s}\n", Record.ADDRESS_COUNTRY_NOT_USA );

        _searchResults = new ArrayList<>();
        _searchResultsIdx = 0;
    }

    public boolean loadContacts( String filepath )
    {
        if( filepath.endsWith( ".csv" ) )
            return loadContactsCsv( filepath );
        _logger.error( "Unsupported file format" );
        return false;
    }


    /**
     * Load a CSV Contact list.  The first row must be a header, the delimiter must be a comma ',' and record delimiter must be newline """
     */
    public boolean loadContactsCsv( String filepath )
    {
        _logger.info( "Loading " + filepath );
        /*
        with open( filepath, "rb" ) as csvfile:
            csvreader = csv.reader( csvfile, delimiter=',' ) // quotechar='|'
            for row in csvreader:
                //self.log.debug( ", ".join( row ) )
                if self.header is None:
                    self.header = row
                else:
                    self.records.append( Record( self, row, self.header ) )
                                                                                                                                                                                                    //csvreader.close()
                                                                                                                                                                                                    //self.log.debug( "Read in records:" )
                                                                                                                                                                                                    //self.log.debug( str(self.header) )
                                                                                                                                                                                                    //for record in self.records:
                                                                                                                                                                                                    //self.log.debug( str(record) )
                        self.page = 0
                        self.sheetframe.display( self.records )
                        num_pages = int(math.ceil( float(len(self.records)) / float(self.TEMPLATES[ self.templateidx ].rows * self.TEMPLATES[ self.templateidx ].columns) ))
                        self.lblPageVar.set( "Page %d of %d" % (self.page + 1, num_pages) )
                        */
        _loadedFilepath = filepath;
        return true;
    }


    public boolean writeCsv()
    {
        return false;
    }

    public List<Record> getRecords(){ return _records; }

    public int getPage(){ return _page; }
    public void setPage( int p )
    {
        _page = p; 
        if( _page < 0 ) _page = 0;
        if( _page >= getNumPagesToFitRecords() ) _page = getNumPagesToFitRecords() - 1;
    }
    public void adjPage( int v )
    {
        setPage( _page + v );
    }

    public int getRecordsPerPage()
    {
        return getTemplate().getRows() * getTemplate().getColumns();
    }

    public int getNumPagesToFitRecords()
    {
        return (int) Math.ceil( (float) _records.size() / (float) getRecordsPerPage() );
    }

    public String getDefaultTemplate(){ return _defLabelTemplate; }

    public List<SearchResult> getSearchResults(){ return _searchResults; }

    public int getSearchResultsIndex(){ return _searchResultsIdx; }
    public void setSearchResultsIndex( int i ){ _searchResultsIdx = i; }

    public void setTemplate( Template t )
    {
        _template = t;
    }

    public Template getTemplate()
    {
        return _template;
    }

    public String getLoadedFilepath(){ return _loadedFilepath; }
    public void setLoadedFilepath( String fp ){ _loadedFilepath = fp; }
}

