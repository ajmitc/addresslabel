package addresslabel.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Color;
import java.util.List;

public class Util
{
    public static boolean DEBUG = true;

    public static void center( Component c )
    {
        int sw = Toolkit.getDefaultToolkit().getScreenSize().width;
        int sh = Toolkit.getDefaultToolkit().getScreenSize().height;
        c.setLocation( (sw - c.getSize().width) / 2, (sh - c.getSize().height) / 2 );
    }

    public static int getScreenWidth(){ return Toolkit.getDefaultToolkit().getScreenSize().width; }
    public static int getScreenHeight(){ return Toolkit.getDefaultToolkit().getScreenSize().height; }

    public static Dimension getScreenSize(){ return Toolkit.getDefaultToolkit().getScreenSize(); }

    public static String join( String[] strings, String delimiter )
    {
        StringBuffer buf = new StringBuffer();
        for( int i = 0; i < strings.length; ++i )
        {
            buf.append( strings[ i ] );
            if( i < strings.length - 1 )
                buf.append( delimiter );
        }
        return buf.toString();
    }

    public static String join( List<String> strings, String delimiter )
    {
        StringBuffer buf = new StringBuffer();
        for( int i = 0; i < strings.size(); ++i )
        {
            buf.append( strings.get( i ) );
            if( i < strings.size() - 1 )
                buf.append( delimiter );
        }
        return buf.toString();
    }

    public static String toPrecision( double val, int decimals )
    {
        double mult = Math.pow( 10, decimals );
        int ival = (int) (val * mult);
        return "" + (double) (ival / mult);
    }

    public static double getDistance( int x1, int y1, int x2, int y2 )
    {
        return Math.sqrt( ((double) (x1 - x2) * (double) (x1 - x2)) + ((double) (y1 - y2) * (double) (y1 - y2)) );
    }

    public static Color getColor( String name )
    {
        name = name.toLowerCase();
        if( name.startsWith( "#" ) && name.length() >= 7 )
        {
            int r = Integer.decode( name.substring( 1, 3 ) );
            int g = Integer.decode( name.substring( 3, 5 ) );
            int b = Integer.decode( name.substring( 5, 7 ) );
            return new Color( r, g, b );
        }
        if( name.equals( "black" ) )
            return Color.BLACK;
        if( name.equals( "blue" ) )
            return Color.BLUE;
        if( name.equals( "green" ) )
            return Color.GREEN;
        if( name.equals( "red" ) )
            return Color.RED;
        if( name.equals( "cyan" ) )
            return Color.CYAN;
        if( name.equals( "dark_gray" ) )
            return Color.DARK_GRAY;
        if( name.equals( "gray" ) )
            return Color.GRAY;
        if( name.equals( "light_gray" ) )
            return Color.LIGHT_GRAY;
        if( name.equals( "magenta" ) || name.equals( "purple" ) )
            return Color.MAGENTA;
        if( name.equals( "orange" ) )
            return Color.ORANGE;
        if( name.equals( "pink" ) )
            return Color.PINK;
        if( name.equals( "white" ) )
            return Color.WHITE;
        if( name.equals( "yellow" ) )
            return Color.YELLOW;
        return null;
    }

    private Util(){}
}

