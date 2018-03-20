package addresslabel.util;

import javax.swing.SwingUtilities;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;

import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.stream.*;

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

    public static double toDecimalValue( String text )
    {
        text = text.trim();
        if( text.equals( "" ) )
            return 0.0;
        if( text.contains( "/" ) )
        {
            String[] parts = text.split( " ", 2 );
            String wholenum = "";
            String fraction = "";
            if( parts.length == 1 )
            {
                fraction = parts[ 0 ];
            }
            else if( parts.length == 2 )
            {
                wholenum = parts[ 0 ];
                fraction = parts[ 1 ];
            }
            String[] fparts = fraction.split( "/" );
            double numerator   = Double.parseDouble( fparts[ 0 ] );
            double denominator = Double.parseDouble( fparts[ 1 ] );
            double value = numerator / denominator;
            if( wholenum != "" )
                value += Double.parseDouble( wholenum );
            return value;
        }
        return Double.parseDouble( text );
    }


    public static String trim( String s, String chars )
    {
        int startIdx = 0;
        int endIdx   = s.length();
        int i = 0;
        while( i < s.length() )
        {
            char a = s.charAt( i );
            if( chars.indexOf( a ) >= 0 )
                startIdx = i + 1;
            else
                break;
            i += 1;
        }

        i = endIdx - 1;
        while( i > 0 && i > startIdx )
        {
            char a = s.charAt( i );
            if( chars.indexOf( a ) >= 0 )
                endIdx = i;
            else
                break;
            i -= 1;
        }

        if( startIdx >= endIdx ) return "";

        return s.substring( startIdx, endIdx );
    }


    public static String toFraction( double value )
    {
        try
        {
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "Converting " + value + " to fraction" );
            int wholenum = (int) value;
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "Whole Num: " + wholenum );
            double fraction = value - (double) wholenum;
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "Fraction: " + fraction );
            fraction = roundto( fraction, 3 );
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "Rounded to " + fraction );
            if( fraction == 0.0 )
                return "" + wholenum;
            String sfraction = "" + fraction;
            int ptidx = sfraction.indexOf( "." );
            if( ptidx >= 0 )
            {
                sfraction = sfraction.substring( ptidx + 1 );
            }
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "sFraction: " + sfraction );
            int numdenom = sfraction.length();
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "numdenom: " + numdenom );
            int numerator = Integer.decode( trim( sfraction, "0" ) );
            int denominator = (int) Math.pow( 10, numdenom );
            if( 1.0 - ((double) numerator / (double) denominator) < 0.001 )
            {
                wholenum += 1;
                denominator = 0;
            }
            //if( ("" + value).equals( "5.1" ) ) System.out.println( numerator + "/" + denominator );
            String sf = reduceFraction16( numerator, denominator );
            if( sf.equals( "1/1" ) )
            {
                wholenum += 1;
                sf = "";
            }
            if( wholenum != 0 )
                sf = wholenum + " " + sf;
            //if( ("" + value).equals( "5.1" ) ) System.out.println( "Reduced to: " + sf );
            return sf;
        }
        catch( Exception e )
        {
            System.err.println( "" + e );
            //if( ("" + value).equals( "5.1" ) ) System.err.println( "" + e );
            e.printStackTrace();
            return "" + value;
        }
    }


    /**
     * Reduce the given numerator/denominator
     */
    public static String reduceFraction( int numerator, int denominator )
    {
        if( numerator == 0 || denominator == 0 )
            return "";
        int gcd = GCD( numerator, denominator );
        return (numerator / gcd) + "/" + (denominator / gcd);
    }

    /**
     * Reduce the given numerator/denominator such that the max denominator is <= 16 and a factor of 4.
     */
    public static String reduceFraction16( int numerator, int denominator )
    {
        int gcd = GCD( numerator, denominator );
        numerator /= gcd;
        denominator /= gcd;
        if( denominator > 16 || denominator % 4 != 0 )
        {
            numerator = (int) Math.round( ((16.0 * (double) numerator) / (double) denominator) );
            denominator = 16;
        }
        return reduceFraction( numerator, denominator );
    }

    public static int GCD( int a, int b ) 
    {
        if( b == 0 ) return a;
        return GCD( b, a % b );
    }


    public static double roundto( double value, int precision )
    {
        double multiplier = Math.pow( 10.0, precision );
        return (double) ((int) (value * multiplier)) / multiplier;
    }


    public static int getStringWidth( Graphics g, String s )
    {
        if( s == null || g.getFontMetrics() == null )
            return 0;
        return SwingUtilities.computeStringWidth( g.getFontMetrics(), s );
    }

    public static int getStringHeight( Graphics2D g, String s )
    {
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector gv = g.getFont().createGlyphVector( frc, s );
        return gv.getPixelBounds( null, 0.f, 0.f ).height;
    }

    public static void drawCenteredString( Graphics g, String s, int x, int y )
    {
        int width = getStringWidth( g, s );
        g.drawString( s, x - (width / 2), y );
    }

    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        Iterator<K> keyIter = keys.iterator();
        Iterator<V> valIter = values.iterator();
        return IntStream.range(0, keys.size()).boxed()
            .collect(Collectors.toMap(_i -> keyIter.next(), _i -> valIter.next()));
    }

    private Util(){}
}

