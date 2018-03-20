package addresslabel;

import java.io.BufferedReader;
import java.io.FileReader;

import com.opencsv.CSVReader;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import addresslabel.template.*;
import addresslabel.util.Logger;
import addresslabel.util.SearchResult;
import addresslabel.util.Util;
import addresslabel.util.CSVUtils;


public class Model
{
    public static final String VERSION = "1.0.0";

    public static final String CONFIG_FILE = "addrlbl.conf";

    public static final Template[] TEMPLATES = {
        new Avery5160Template()
    };

    private Logger _logger;

    private Template _template; // selected template
    private List<Record> _records;
    private String _loadedFilepath;
    private int _page;
    private String _defLabelTemplate;
    private List<SearchResult> _searchResults;
    private int _searchResultsIdx;
    private List<CountryLabelTemplate> _countryLabelTemplates;

    public Model()
    {
        _logger = Logger.getLogger( Model.class );
        _template = TEMPLATES[ 0 ];
        _records = new ArrayList<>();
        _loadedFilepath = null;
        _page = 0;  // Currently displayed page

        // default format template
        _defLabelTemplate = "{" + Record.TITLE + "} {" + Record.FIRST_NAME + "} {" + Record.MIDDLE_NAME + "} {" + Record.LAST_NAME + "} {" + Record.SUFFIX + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_STREET_1 + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_STREET_2 + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_CITY + "}, {" + Record.ADDRESS_STATE + "} {" + Record.ADDRESS_ZIP + "}\n";
        _defLabelTemplate += "{" + Record.ADDRESS_COUNTRY_NOT_USA + "}\n";

        _searchResults = new ArrayList<>();
        _searchResultsIdx = 0;

        _countryLabelTemplates = new ArrayList<CountryLabelTemplate>();
        _countryLabelTemplates.add( new GermanyLabelTemplate() );
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
            List<String> header = null;
            String[] entry;
            while( (entry = reader.readNext()) != null )
            {
                List<String> fields = Arrays.asList( entry );
                if( header == null )
                    header = fields;
                else
                {
                    Map<String, String> data = Util.zipToMap( header, fields );
                    Record record = new Record( data, _defLabelTemplate );
                    _records.add( record );

                    // attempt to set a different country label template, if appropriate
                    for( CountryLabelTemplate clt: _countryLabelTemplates )
                    {
                        if( clt.matches( record.get( Record.ADDRESS_COUNTRY ) ) )
                        {
                            record.setDefaultLabelTemplate( clt.template );
                            break;
                        }
                    }
                }
            }

            // Add Record objects to fill the page
            int recordsPerPage = getTemplate().getRows() * getTemplate().getColumns();
            int numPages = (int) (Math.ceil( (float) _records.size() / (float) recordsPerPage) );
            int totalRecords = recordsPerPage * numPages;
            int missingRecords = totalRecords - _records.size();
            addEmptyRecords( missingRecords );

            _page = 0;
            _loadedFilepath = filepath;
            return true;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeCsv()
    {
        // Write to _loadedFilepath
        return false;
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

    public void sortRecords( final String recordKey )
    {
        Collections.sort( _records, new Comparator<Record>(){
            public int compare( Record r1, Record r2 )
            {
                if( !r1.isUsed() )
                {
                    if( !r2.isUsed() )
                        return 0;
                    return 1;
                }
                else if( !r2.isUsed() )
                {
                    return -1;
                }
                return r1.get( recordKey ).compareTo( r2.get( recordKey ) );
            }

            public boolean equals( Object obj )
            {
                return false;
            }
        });
    }

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


    public static class CountryLabelTemplate
    {
        public List<String> matchCountry = new ArrayList<String>();
        public String template;

        public CountryLabelTemplate()
        {

        }

        public boolean matches( String country )
        {
            for( String c: matchCountry )
            {
                if( c.equalsIgnoreCase( country ) )
                    return true;
            }
            return false;
        }
    }


    public static class GermanyLabelTemplate extends CountryLabelTemplate
    {
        public GermanyLabelTemplate()
        {
            super();
            matchCountry.add( "Germany" );
            matchCountry.add( "Deutschland" );
            template = "{" + Record.TITLE + "} {" + Record.FIRST_NAME + "} {" + Record.MIDDLE_NAME + "} {" + Record.LAST_NAME + "} {" + Record.SUFFIX + "}\n";
            template += "{" + Record.ADDRESS_STREET_1 + "}\n";
            template += "{" + Record.ADDRESS_STREET_2 + "}\n";
            template += "{" + Record.ADDRESS_ZIP + "}, {" + Record.ADDRESS_CITY + "}\n";
            template += "{" + Record.ADDRESS_COUNTRY + "}\n";
        }
    }


}

