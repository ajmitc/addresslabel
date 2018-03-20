package addresslabel.util;

public class Logger
{
    public static Logger getLogger( String prefix )
    {
        return new Logger( prefix );
    }

    public static Logger getLogger( Class clazz )
    {
        return getLogger( "" + clazz );
    }

    private String _prefix;

    private Logger( String prefix )
    {
        _prefix = prefix;
    }

    private void _write( String lvl, String text )
    {
        System.out.println( "[" + lvl + "] " + _prefix + " - " + text );
    }


    public void info( String text )
    {
        _write( "INFO", text );
    }


    public void warning( String text )
    {
        _write( "WARNING", text );
    }


    public void error( String text )
    {
        _write( "ERROR", text );
    }


    public void debug( String text )
    {
        _write( "DEBUG", text );
    }
}

