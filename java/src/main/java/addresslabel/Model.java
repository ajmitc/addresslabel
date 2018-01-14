package addresslabel;

import addresslabel.util.Util;
import addresslabel.util.Logger;
import addresslabel.util.SearchResult;
import addresslabel.util.Avery5160Template;
import addresslabel.util.SheetTemplate;
import addresslabel.util.CSVUtils;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;

import com.opencsv.CSVReader;

public class Model
{
    public static final String VERSION = "1.0.0";

    public static final String CONFIG_FILE = "addrlbl.conf";

    public static final SheetTemplate[] TEMPLATES = {
        new Avery5160Template()
    };

    private Logger _logger;
    private int _templateidx;
    private List<String> _header;
    private List<Record> _records;
    private int _page;
    private String _loadedFilepath;
    private String _defLabelTemplate;
    private List<SearchResult> _searchResults;
    private int _searchResultsIdx;

    public Model()
    {
        _logger = Logger.getLogger( "Model" );
        _templateidx = 0;
        _header = null;
        _records = new ArrayList<>();
        _page = 0;  // Currently displayed page
        _loadedFilepath = null;

        // default format template
        _defLabelTemplate = "{" + Record.TITLE + "} {" + Record.FIRST_NAME + "} {" + Record.MIDDLE_NAME + "} {" + Record.LAST_NAME + "} {" + Record.SUFFIX + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_STREET_1 + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_STREET_2 + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_CITY + "}, {" + Record.ADDRESS_STATE + "} {" + Record.ADDRESS_ZIP + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_COUNTRY_NOT_USA + "}\n";

        _searchResults = new ArrayList<SearchResult>();
        _searchResultsIdx = 0;
    }


    public boolean loadContactsFromFile( String filename )
    {
        if( filename.endsWith( ".csv" ) )
            return loadContactsCsv( filename );
        _logger.error( "Unsupported file format" );
        return false;
    }

    /**
     * Load a CSV Contact list.  The first row must be a header, the delimiter must be a comma ',' and record delimiter must be newline
     */
    public boolean loadContactsCsv( String filepath )
    {
        _logger.info( "Loading " + filepath );
        try
        {
            CSVReader reader = new CSVReader( new FileReader( filepath ) );
            String[] entry;
            while( (entry = reader.readNext()) != null )
            {
                List<String> fields = Arrays.asList( entry );
                if( _header == null )
                    _header = fields;
                else
                {
                    _records.add( new Record( _defLabelTemplate, fields, _header ) );
                }
            }

            // Add Record objects to fill the page
            int recordsPerPage = getTemplate().getRows() * getTemplate().getColumns();
            int numPages = (int) (Math.ceil( (float) _records.size() / (float) recordsPerPage) );
            int totalRecords = recordsPerPage * numPages;
            int missingRecords = totalRecords - _records.size();
            addEmptyRecords( missingRecords );

            _page = 0;
            return true;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public void exit()
    {
        System.exit( 0 );
    }

    public void addEmptyRecords( int num )
    {
        for( int i = 0; i < num; ++i )
            _records.add( new Record( _defLabelTemplate ) );
    }

    public List<Record> getRecords(){ return _records; }
    public List<Record> getUsedRecords()
    {
        List<Record> used = new ArrayList<Record>();
        for( Record r: _records )
        {
            if( r.isUsed() )
                used.add( r );
        }
        return used; 
    }

    public int getPage(){ return _page; }
    public void setPage( int p ){ _page = p; }

    public List<SearchResult> getSearchResults(){ return _searchResults; }
    public int getSearchResultsIdx(){ return _searchResultsIdx; }
    public void setSearchResultsIdx( int i ){ _searchResultsIdx = i; }

    public SheetTemplate getTemplate()
    {
        return TEMPLATES[ _templateidx ];
    }

    public void setTemplate( int i )
    {
        _templateidx = i;
    }
}
