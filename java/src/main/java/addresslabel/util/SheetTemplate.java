package addresslabel.util;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import addresslabel.Record;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.util.*;

public class SheetTemplate
{
    // Margin indexes
    public static final int TOP    = 0;
    public static final int RIGHT  = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT   = 3;
    public static final double INCH = 72.0; // 72pts per inch

    protected Logger _logger;
    protected String _name;
    protected double[] _margins = { 0.0, 0.0, 0.0, 0.0 };
    protected int _rows;
    protected int _columns;
    protected double _paperWidth;
    protected double _paperHeight;
    protected double _labelWidth;
    protected double _labelHeight;
    protected double[] _labelMargins = { 0.125, 0.125, 0.125, 0.125 };
    protected double _verticalPitch;
    protected double _horizontalPitch;
    protected String _fontName;
    protected int _fontSize;
    protected boolean _drawLabelBorder;
    protected boolean _drawMargins;

    public SheetTemplate( String name )
    {
        _logger  = Logger.getLogger( String.format( "SheetTemplate(%s)", name ) );
        _name = name;
        _rows          = 0;
        _columns       = 0;
        _paperWidth    =  8.5;   // inches
        _paperHeight   = 11.0;   // inches
        _labelWidth    =  2.63;  // inches
        _labelHeight   =  1.0;   // inches

        // The vertical pitch is defined as the measurement from the top of the first label to the top of the label below it. 
        // The horizontal pitch is defined as the measurement from the left edge of the first label to the left edge of the label next to it.
        _verticalPitch   = 1.0;   // inches
        _horizontalPitch = 1.0;   // inches

        _fontName = "Helvetica";  // canvas.getAvailableFonts()
        _fontSize = 10;

        _drawLabelBorder = true;
        _drawMargins = false;
    }


    public PDDocument toPDF( List<Record> records )
    {
        try
        {
            PDDocument doc = new PDDocument();
            // a valid PDF document requires at least one page

            int recordidx = 0;
            boolean done = false;
            while( !done )  // for each Page
            {
                PDPage page = new PDPage( PDRectangle.LETTER );
                doc.addPage( page );

                float pageWidth  = page.getMediaBox().getWidth();
                float pageHeight = page.getMediaBox().getHeight();

                PDPageContentStream contents = new PDPageContentStream( doc, page );
                double topmargin  = _margins[ TOP    ] * INCH;
                double leftmargin = _margins[ LEFT   ] * INCH;
                double botmargin  = _margins[ BOTTOM ] * INCH;
                double rtmargin   = _margins[ RIGHT  ] * INCH;

                if( _drawMargins )
                {
                    contents.moveTo( (float) leftmargin, (float) botmargin );
                    contents.lineTo( (float) leftmargin, (float) (pageHeight - topmargin) );
                    contents.lineTo( (float) (pageWidth - rtmargin), (float) (pageHeight - topmargin) );
                    contents.lineTo( (float) (pageWidth - rtmargin), (float) botmargin );
                    contents.lineTo( (float) leftmargin, (float) botmargin );
                }

                for( int row = 0; row < _rows; ++row )
                {
                    for( int col = 0; col < _columns; ++col )
                    {
                        int y = (int) (pageHeight - topmargin - (row * _verticalPitch * INCH));
                        int x = (int) (leftmargin + (col * _horizontalPitch * INCH));
                        Record r = records.get( recordidx );
                        _drawLabel( doc, contents, x, y, r );

                        recordidx += 1;
                        if( recordidx >= records.size() )
                        {
                            done = true;
                            break;
                        }
                    }
                    if( done )
                        break;
                }
                contents.close();
            }

            //doc.save( filename );
            return doc;
        }
        catch( Exception e )
        {
            e.printStackTrace();
            return null;
        }
    }

    private void _drawLabel( PDDocument doc, PDPageContentStream c, int x, int y, Record record )
    {
        try
        {
            String[] lines = record.getDisplay().split( "\n" );
            // get max line length
            String maxline = "";
            for( String line: lines )
            {
                line = line.replace( "\n", "" ).replace( "\r", "" );
                if( line.length() > maxline.length() )
                    maxline = line;
            }

            PDFont font = null;
            if( _fontName.equalsIgnoreCase( "Helvetica" ) )
                font = PDType1Font.HELVETICA;
            else
                font = PDType1Font.HELVETICA;

            // center text in label
            double maxlinelen = font.getStringWidth( maxline ) * _fontSize / 1000.0;
            int cx = x + (int) (((_labelWidth  * INCH) - maxlinelen) / 2);
            // Include margin
            if( cx < x + (int) (_labelMargins[ LEFT ] * INCH) )
            {
                cx = x + (int) (_labelMargins[ LEFT ] * INCH);
            }

            List<String> labelLines = new ArrayList<String>();
            int maxwidth = (int) ((_labelWidth - _labelMargins[ LEFT ] - _labelMargins[ RIGHT ]) * INCH);
            if( maxlinelen > maxwidth )
            {
                //self.log.debug( "maxlinelen (%d) > maxwidth (%d)" % (maxlinelen, maxwidth) )
                double charwidth = font.getStringWidth( "a" ) * _fontSize / 1000.0;
                // One or more of the lines exceed the max width
                for( String line: lines )
                {
                    line = line.replace( "\n", "" ).replace( "\r", "" );
                    double w = font.getStringWidth( line ) * _fontSize / 1000.0;
                    if( w > maxwidth )
                    {
                        int maxchars = Math.min( (int) (maxwidth / charwidth), line.length() );
                        while( line.charAt( maxchars ) != ' ' && maxchars > 0 )
                        {
                            maxchars -= 1;
                        }
                        String line1 = line.substring( 0, maxchars + 1 );
                        String line2 = line.substring( maxchars, line.length() );
                        labelLines.add( line1 );
                        labelLines.add( line2 );
                    }
                    else
                    {
                        labelLines.add( line );
                    }
                }
            }
            else
            {
                for( String line: lines )
                {
                    line = line.replace( "\n", "" ).replace( "\r", "" );
                    labelLines.add( line );
                }
            }

            int cy = y - (int) (_labelMargins[ TOP ] * INCH) - _fontSize;

            // Get the string height in text space units.
            float stringHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() * _fontSize / 1000.f;

            c.beginText();
            c.setFont( font, _fontSize );
            c.newLineAtOffset( cx, cy );

            for( int i = 0; i < labelLines.size(); ++i )
            {
                String line = labelLines.get( i );
                c.showText( line );
                c.newLineAtOffset( 0, -stringHeight );
            }
            c.endText();
            if( _drawLabelBorder )
            {
                //RoundRect roundRect = new RoundRect( 5, 5 );
                //roundRect.add( doc, c, (float) x, (float) (y - (_labelHeight * INCH)), (float) (_labelWidth * INCH), (float) (_labelHeight * INCH) );
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }



    public String getName(){ return _name; }
    public double[] getMargins(){ return _margins; }
    public int getRows(){ return _rows; }
    public int getColumns(){ return _columns; }
    public double getPaperWidth(){ return _paperWidth; }
    public double getPaperHeight(){ return _paperHeight; }
    public double getLabelWidth(){ return _labelWidth; }
    public double getLabelHeight(){ return _labelHeight; }
    public double[] getLabelMargins(){ return _labelMargins; }
    public double getVerticalPitch(){ return _verticalPitch; }
    public double getHorizontalPitch(){ return _horizontalPitch; }
    public String getFontName(){ return _fontName; }
    public int getFontSize(){ return _fontSize; }
    public boolean shouldDrawLabelBorder(){ return _drawLabelBorder; }
    public boolean shouldDrawMargins(){ return _drawMargins; }
}

