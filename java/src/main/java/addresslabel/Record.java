package addresslabel;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.stream.*;

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
        ADDRESS_COUNTRY_NOT_USA,
    };

    // These labels do not have corresponding values, but add functionality to existing labels
    public static final List<String> LABEL_IGNORE = new ArrayList<>();

    // Mapping of an imported header to a standard label
    public static final Map<String, String[]> LABEL_MAPPING = new HashMap<>();

    // Labels values that match USA
    public static final List<String> USA_VALUES = new ArrayList<>();

    static {
        // Ignored labels
        LABEL_IGNORE.add( ADDRESS_COUNTRY_NOT_USA );

        // Label mapping
        LABEL_MAPPING.put( ADDRESS,          new String[]{ "address" } );
        LABEL_MAPPING.put( ADDRESS_STREET_1, new String[]{ "street", "street 1" } );
        LABEL_MAPPING.put( ADDRESS_STREET_2, new String[]{ "street 2" } );
        LABEL_MAPPING.put( ADDRESS_CITY,     new String[]{ "city" } );
        LABEL_MAPPING.put( ADDRESS_STATE,    new String[]{ "state" } );
        LABEL_MAPPING.put( ADDRESS_ZIP,      new String[]{ "zip", "zipcode" } );
        LABEL_MAPPING.put( ADDRESS_COUNTRY,  new String[]{ "country" } );

        USA_VALUES.add( "" );
        USA_VALUES.add( "usa" );
        USA_VALUES.add( "united states of america" );
    }


    public static final String REGEXP_MATCH_LABEL_TAG = "\\{[a-zA-Z0-9 _]+\\}";

    private Logger _logger = Logger.getLogger( Record.class );
    private Pattern _regexpLabelTag;
    private Map<String, String> _data;
    // If null, use default label template in app 
    private String _template;
    private String _defTemplate;
    // Displayed text
    private String _display;

    /**
     * @param Map of header to value
     */
    public Record( Map<String, String> record, String defTemplate )
    {
        _regexpLabelTag = Pattern.compile( REGEXP_MATCH_LABEL_TAG );
        _data = new HashMap<String, String>();
        clearData();
        _mapRecord( record );
        _template = null;
        _defTemplate = defTemplate;
        _display  = null;
    }


    public void clearData()
    {
        _data.clear();

        // We still want the standard labels
        for( String lbl: LABELS )
        {
            if( !LABEL_IGNORE.contains( lbl ) ) // { FIRST_NAME: "Aaron", LAST_NAME: "Mitchell" }
            {
                _data.put( lbl, "" );
            }
        }
    }


    /**
     * Return True if 'text' is found within this Record, False otherwise
     */
    public boolean search( String text )
    {
        return _display.toLowerCase().indexOf( text.toLowerCase() ) >= 0;
    }


    private void _mapRecord( Map<String, String> record )
    {
        for( String h: record.keySet() )
        {
            _data.put( _mapHeader( h ), record.get( h ) );
        }
    }


    private String _mapHeader( String header )
    {
        header = header.trim();
        for( String lbl: LABELS )
        {
            if( lbl.equalsIgnoreCase( header ) || lbl.replace( " ", "_" ).equalsIgnoreCase( header ) )
                return lbl;
        }
        for( String key: LABEL_MAPPING.keySet() )
        {
            String[] options = LABEL_MAPPING.get( key );
            for( String option: options )
            {
                if( option.equalsIgnoreCase( header ) )
                    return key;
            }
        }
        return header;
    }


    private String _format( String template )
    {
        for( String lbl: LABELS )
        {
            if( lbl.equals( ADDRESS_COUNTRY_NOT_USA ) )
            {
                if( !USA_VALUES.contains( _data.get( ADDRESS_COUNTRY ).toLowerCase() ) )
                    template = template.replace( "{" + lbl + "}", _data.get( ADDRESS_COUNTRY ) );
                else
                    template = template.replace( "{" + lbl + "}", "" );
            }
            else
                template = template.replace( "{" + lbl + "}", _data.get( lbl ) );
        }
        // Clear out any tags that are not replaced already
        Matcher matcher = _regexpLabelTag.matcher( template );
        template = matcher.replaceAll( "" );
        while( template.indexOf( "  " ) >= 0 )
            template = template.replaceAll( "  +", " " );
        template = template.replaceAll( "\n\n", "\n" );
        return template.trim();
    }


    public String getDisplay()
    {
        if( _display == null )
            _display = _format( _template != null? _template: _defTemplate );
        return _display;
    }


    public void setDisplay( String d )
    {
        _display = d;
    }


    public String getTemplate()
    {
        return _template != null? _template: _defTemplate;
    }


    public void setTemplate( String t )
    {
        _template = t;
        _display  = null;
    }

    public Map<String, String> getData(){ return _data; }

    public String getString()
    {
        return _data.entrySet().stream().map( e -> e.getKey() + "=" + e.getValue() ).collect( Collectors.joining( ", " ) );
    }
}

