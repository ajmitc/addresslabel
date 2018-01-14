package addresslabel.util;

public class Logger
{
    private String _prefix;

    public Logger( String prefix )
    {
        _prefix = prefix;
    }

    public static Logger getLogger( String prefix )
    {
        return new Logger( prefix );
    }

    public void write( String lvl, String text )
    {
        System.out.println( String.format( "[%s] %s - %s", lvl, _prefix, text ) );
    }


    public void info( String text )
    {
        write( "INFO", text );
    }


    public void warning( String text )
    {
        write( "WARNING", text );
    }


    public void error( String text )
    {
        write( "ERROR", text );
    }


    public void debug( String text )
    {
        write( "DEBUG", text );
    }
}




