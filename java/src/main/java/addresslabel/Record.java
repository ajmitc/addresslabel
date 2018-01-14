package addresslabel;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import addresslabel.util.Logger;

public class Record
{
    public static final String TITLE            = "title";
    public static final String NAME             = "name";
    public static final String FIRST_NAME       = "first name";
    public static final String MIDDLE_NAME      = "middle name";
    public static final String LAST_NAME        = "last name";
    public static final String SUFFIX           = "suffix";
    public static final String ADDRESS          = "home address";
    public static final String ADDRESS_STREET_1 = "home street";
    public static final String ADDRESS_STREET_2 = "home street 2";
    public static final String ADDRESS_CITY     = "home city";
    public static final String ADDRESS_STATE    = "home state";
    public static final String ADDRESS_ZIP      = "home postal code";
    public static final String ADDRESS_COUNTRY  = "home country";
    public static final String ADDRESS_COUNTRY_NOT_USA = "home country not usa";

    public static final String[] LABELS = {
        TITLE,
        NAME,
        FIRST_NAME,
        MIDDLE_NAME,
        LAST_NAME,
        SUFFIX,
        ADDRESS,
        ADDRESS_STREET_1,
        ADDRESS_STREET_2,
        ADDRESS_CITY,
        ADDRESS_STATE,
        ADDRESS_ZIP,
        ADDRESS_COUNTRY,
        ADDRESS_COUNTRY_NOT_USA
    };

    // These labels do not have corresponding values, but add functionality to existing labels
    public static final String[] LABEL_IGNORE = {
        ADDRESS_COUNTRY_NOT_USA
    };

    public static final Map<String, List<String>> LABEL_MAPPING = new HashMap<>();
    static {
        LABEL_MAPPING.put( ADDRESS, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS ).add( "address" );

        LABEL_MAPPING.put( ADDRESS_STREET_1, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS_STREET_1 ).add( "street" );
        LABEL_MAPPING.get( ADDRESS_STREET_1 ).add( "street 1" );

        LABEL_MAPPING.put( ADDRESS_STREET_2, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS_STREET_2 ).add( "street 2" );

        LABEL_MAPPING.put( ADDRESS_CITY, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS_CITY ).add( "city" );

        LABEL_MAPPING.put( ADDRESS_STATE, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS_STATE ).add( "state" );

        LABEL_MAPPING.put( ADDRESS_ZIP, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS_ZIP ).add( "zip" );
        LABEL_MAPPING.get( ADDRESS_ZIP ).add( "zipcode" );

        LABEL_MAPPING.put( ADDRESS_COUNTRY, new ArrayList<String>() );
        LABEL_MAPPING.get( ADDRESS_COUNTRY ).add( "country" );
    }


    public static final String REGEXP_MATCH_LABEL_TAG = "\\{[a-zA-Z0-9 _]+\\}";


    private Logger _logger;
    private String _defLabelTemplate;
    private Pattern _regexpLabelTag;
    private String _template;
    private String _display;
    private Map<String, String> _data;
    private boolean _used;  // true if this Record should contain some content, false if it's an empty record

    public Record( String defLabelTemplate )
    {
        _logger = Logger.getLogger( "Record" );
        _defLabelTemplate = defLabelTemplate;

        _regexpLabelTag = Pattern.compile( REGEXP_MATCH_LABEL_TAG );

        _template = null; // If null, use default label template in app 
        _display  = null; // Displayed text override

        _data = new HashMap<String, String>();
        clearData();
        _used = false;
    }

    public Record( String defLabelTemplate, List<String> record )
    {
        this( defLabelTemplate, record, null );
    }

    public Record( String defLabelTemplate, List<String> record, List<String> header )
    {
        this( defLabelTemplate );
        _mapRecord( record, header );
        _used = true;
    }


    public void clearData()
    {
        _data.clear();

        // Seed data with un-ignored labels
        for( String lbl: LABELS )
        {
            boolean shouldIgnore = false;
            for( String ignore: LABEL_IGNORE )
            {
                if( ignore.equalsIgnoreCase( lbl ) )
                {
                    shouldIgnore = true;
                    break;
                }
            }
            if( !shouldIgnore )
                _data.put( lbl, "" );
        }
    }


    /**
     * Return True if 'text' is found within this Record, False otherwise
     */
    public boolean search( String text )
    {
        return _display.toLowerCase().indexOf( text.toLowerCase() ) >= 0;
    }


    private boolean _mapRecord( List<String> rawdata, List<String> header )
    {
        if( header != null )
        {
            return _mapRecordListWithHeaderList( rawdata, header );
        }
        _logger.warning( "Unsupported record format" );
        return false;
    }


    private boolean _mapRecordListWithHeaderList( List<String> rawdata, List<String> header )
    {
        for( int i = 0; i < header.size(); i++ )
        {
            String h = header.get( i );
            if( rawdata.size() <= i )
            {
                _logger.warning( "record length (" + rawdata.size() + ") on line " + (i + 1) + " has fewer fields than header (" + header.size() + ")" );
                continue;
            }
            String v = rawdata.get( i );
            String key = _mapHeader( h );
            if( key != null )
            {
                _data.put( key, v );
            }
            else
            {
                _data.put( h, v );
            }
        }
        return true;
    }


    private String _mapHeader( String header )
    {
        header = header.trim();
        for( String lbl: LABELS )
        {
            if( lbl.equalsIgnoreCase( header ) )
            {
                return lbl;
            }
            if( lbl.replace( " ", "_" ).equalsIgnoreCase( header ) )
            {
                return lbl;
            }
        }
        for( String key: LABEL_MAPPING.keySet() )
        {
            List<String> options = LABEL_MAPPING.get( key );
            for( String option: options )
            {
                if( option.equalsIgnoreCase( header ) )
                    return key;
            }
        }
        return null;
    }


    private String _format( String template )
    {
        for( String lbl: LABELS )
        {
            if( lbl.equals( ADDRESS_COUNTRY_NOT_USA ) )
            {
                if( !_data.get( ADDRESS_COUNTRY ).equals( "" ) && !_data.get( ADDRESS_COUNTRY ).equalsIgnoreCase( "usa" ) && !_data.get( ADDRESS_COUNTRY ).equalsIgnoreCase( "united states of america" ) )
                {
                    template = template.replaceAll( "\\{" + lbl + "\\}", _data.get( ADDRESS_COUNTRY ) );
                }
                else
                {
                    template = template.replaceAll( "\\{" + lbl + "\\}", "" );
                }
            }
            else
            {
                template = template.replaceAll( "\\{" + lbl + "\\}", _data.get( lbl ) );
            }
        }
        template = template.replaceAll( REGEXP_MATCH_LABEL_TAG, "" );
        while( template.indexOf( "  " ) >= 0 )
        {
            template = template.replaceAll( "  +", " " );
        }
        template = template.replaceAll( "\n\n", "\n" );
        template = template.replaceAll( "\n", "<br/>" );
        return template.trim();
    }


    public String getDisplay()
    {
        if( _display == null )
        {
            _display = _format( _template != null? _template: _defLabelTemplate );
        }
        return _display;
    }


    public void setDisplay( String s )
    {
        _display = s;
    }


    public String getTemplate()
    {
        return _template != null? _template: _defLabelTemplate;
    }


    public void setTemplate( String t )
    {
        _template = t;
        _display  = null;
    }

    public Map<String, String> getData(){ return _data; }

    public String get( String key )
    {
        if( !_data.containsKey( key ) )
            return null;
        return _data.get( key );
    }

    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for( String key: _data.keySet() )
        {
            sb.append( key );
            sb.append( "=" );
            sb.append( _data.get( key ) );
            sb.append( ", " );
        }
        return sb.toString();
    }



    public static boolean shouldIgnore( String lbl )
    {
        for( String l: LABEL_IGNORE )
        {
            if( l.equalsIgnoreCase( lbl ) )
                return true;
        }
        return false;
    }


    public boolean isUsed(){ return _used; }
    public void setUsed( boolean u ){ _used = u; }
}

