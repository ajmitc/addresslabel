package addresslabel.template;

import java.util.List;
import java.util.ArrayList;

import java.io.BufferedWriter;

import addresslabel.Record;
import addresslabel.util.Logger;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.*;
import org.apache.pdfbox.pdmodel.font.*;

public class Template
{
    // Margin indexes
    public static final int TOP    = 0;
    public static final int RIGHT  = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT   = 3;
    public static final double INCH = 72.0; // 72pts per inch

    private Logger _logger;
    protected String name;
    protected double[] margins = { 0, 0, 0, 0 };  // inches
    protected int rows;
    protected int columns;
    protected double paperWidth;
    protected double paperHeight;
    protected double labelWidth;
    protected double labelHeight;
    protected double[] labelMargins = { 0.125, 0.125, 0.125, 0.125 };

    // The vertical pitch is defined as the measurement from the top of the first label to the top of the label below it. 
    // The horizontal pitch is defined as the measurement from the left edge of the first label to the left edge of the label next to it.
    protected double verticalPitch;
    protected double horizontalPitch;

    protected String fontName;
    protected int fontSize;

    protected boolean drawLabelBorder;
    protected boolean drawMargins;


    public Template( String name )
    {
        _logger = Logger.getLogger( "Template(" + name + ")" );
        this.name = name;
        rows = 0;
        columns = 0;
        paperWidth =  8.5;   // inches
        paperHeight = 11.0;   // inches
        labelWidth =  2.63;  // inches
        labelHeight =  1.0;   // inches

        verticalPitch = 1.0;   // inches
        horizontalPitch = 1.0;   // inches

        fontName = "Helvetica";
        fontSize = 10;

        drawLabelBorder = true;
        drawMargins = false;
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

                double pageWidth  = page.getMediaBox().getWidth();
                double pageHeight = page.getMediaBox().getHeight();

                //_logger.info( "pageWidth=" + pageWidth );
                //_logger.info( "pageHeight=" + pageHeight );
                //_logger.info( "pageWidthInch=" + (pageWidth / INCH) );
                //_logger.info( "pageHeightInch=" + (pageHeight / INCH) );

                PDPageContentStream contents = new PDPageContentStream( doc, page );
                double topmargin  = margins[ TOP    ] * INCH;
                double leftmargin = margins[ LEFT   ] * INCH;
                double botmargin  = margins[ BOTTOM ] * INCH;
                double rtmargin   = margins[ RIGHT  ] * INCH;

                //_logger.info( "topMargin=" + topmargin );
                //_logger.info( "leftMargin=" + leftmargin );

                if(drawMargins)
                {
                    contents.moveTo( (float) leftmargin, (float) botmargin );
                    contents.lineTo( (float) leftmargin, (float) (pageHeight - topmargin) );
                    contents.lineTo( (float) (pageWidth - rtmargin), (float) (pageHeight - topmargin) );
                    contents.lineTo( (float) (pageWidth - rtmargin), (float) botmargin );
                    contents.lineTo( (float) leftmargin, (float) botmargin );
                    contents.closeAndStroke();
                }

                for(int row = 0; row < rows; ++row )
                {
                    for(int col = 0; col < columns; ++col )
                    {
                        int y = (int) Math.round(pageHeight - topmargin - (row * verticalPitch * INCH));
                        int x = (int) Math.round(leftmargin + (col * horizontalPitch * INCH));
                        int yInch = (int) Math.round(pageHeight - topmargin - (row * verticalPitch));
                        int xInch = (int) Math.round(leftmargin + (col * horizontalPitch));
                        //_logger.info( "Calling drawLabel at x=" + xInch + ", y=" + yInch );
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
            //_logger.info( "Drawing Label '" + lines[ 0 ] + "' at x:" + x + ", y:" + y );
            // get max line length
            String maxline = "";
            for( String line: lines )
            {
                line = line.replace( "\n", "" ).replace( "\r", "" );
                if( line.length() > maxline.length() )
                    maxline = line;
            }

            PDFont font = null;
            if( fontName.equalsIgnoreCase( "Helvetica" ) )
                font = PDType1Font.HELVETICA;
            else
                font = PDType1Font.HELVETICA;

            // center text in label
            double maxlinelen = font.getStringWidth( maxline ) / 1000.0 * fontSize;

            List<String> labelLines = new ArrayList<>();
            int maxwidth = (int) ((labelWidth - labelMargins[ LEFT ] - labelMargins[ RIGHT ]) * INCH);
            if( maxlinelen > maxwidth )
            {
                // Reset maxline, so we can re-calculate maxlinelen
                maxline = "";
                //self.log.debug( "maxlinelen (%d) > maxwidth (%d)" % (maxlinelen, maxwidth) )
                double charwidth = font.getStringWidth( "a" ) * fontSize / 1000.0;
                // One or more of the lines exceed the max width
                for( String line: lines )
                {
                    line = line.replace( "\n", "" ).replace( "\r", "" );
                    double w = font.getStringWidth( line ) * fontSize / 1000.0;
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
                        if( line1.length() > maxline.length() ) {
                            maxline = line1;
                        }
                        if( line2.length() > maxline.length() ) {
                            maxline = line2;
                        }
                    }
                    else
                    {
                        labelLines.add( line );
                        if( line.length() > maxline.length() ) {
                            maxline = line;
                        }
                    }
                }

                maxlinelen = font.getStringWidth( maxline ) * fontSize / 1000.0;
            }
            else
            {
                for( String line: lines )
                {
                    line = line.replace( "\n", "" ).replace( "\r", "" );
                    labelLines.add( line );
                }
            }
            double maxlinelenInch = maxlinelen / INCH;

            int xInch = (int) Math.round(x / INCH);
            int yInch = (int) Math.round(y / INCH);

            //_logger.info( "   Max Line: '" + maxline + "'" );
            //_logger.info( "   Max Line Len: " + maxlinelenInch + " (" + maxlinelen + "px)" );

            //_logger.info( "Label Width: " + (labelWidth * INCH) );
            //_logger.info( "Label Height: " + (labelHeight * INCH) );
            //_logger.info( "Label Top Margin: " + (labelMargins[ TOP ] * INCH) );
            //_logger.info( "Label Left Margin: " + (labelMargins[ LEFT ] * INCH) );

            int cx = x + (int) Math.round(((labelWidth  * INCH) - maxlinelen) / 2);
            int cxInch = xInch + (int) Math.round((labelWidth - maxlinelenInch) / 2);
            //_logger.info( "   Text Start X: " + cxInch + " (" + cx + "px)" );

            // Include margin
            if( cx < x + (int) Math.round(labelMargins[ LEFT ] * INCH) )
            {
                cx = x + (int) Math.round(labelMargins[ LEFT ] * INCH);
                cxInch = xInch + (int) Math.round(labelMargins[ LEFT ]);
                //_logger.info( "      Text Start X Modified: " + cxInch + " (" + cx + "px)" );
            }

            int cy = y - (int) Math.round(labelMargins[ TOP ] * INCH) - fontSize;
            int cyInch = yInch - (int) Math.round(labelMargins[ TOP ]) - fontSize;
            //_logger.info( "   Text Start Y: " + cyInch + " (" + cy + "px)" );

            // Get the string height in text space units.
            float stringHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000.f * fontSize;
            //_logger.info( "   Text Height: " + stringHeight + "px" );

            c.beginText();
            c.setFont( font, fontSize);
            c.newLineAtOffset( cx, cy );

            for( int i = 0; i < labelLines.size(); ++i )
            {
                String line = labelLines.get( i );
                c.showText( line );
                c.newLineAtOffset( 0, -stringHeight );
            }
            c.endText();
            if(drawLabelBorder)
            {
                //RoundRect roundRect = new RoundRect( 5, 5 );
                //roundRect.add( doc, c, (float) x, (float) (y - (labelHeight * INCH)), (float) (labelWidth * INCH), (float) (labelHeight * INCH) );
                c.moveTo( (float) x, (float) y );
                c.lineTo( (float) x, (float) (y - (labelHeight * INCH)) );
                c.lineTo( (float) (x + (labelWidth * INCH)), (float) (y - (labelHeight * INCH)) );
                c.lineTo( (float) (x + (labelWidth * INCH)), (float) y );
                c.lineTo( (float) x, (float) y );
                c.closeAndStroke();
            }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }



    /**
     * Write the pdf to the input file descriptor (infd)
     */
    private boolean writePdf( BufferedWriter infd, List<Record> records )
    {
        if( records.size() == 0 )
            return false;
            /*
        c = pdfcanvas.Canvas( infd, pagesize=letter )
        pagewidth, pageheight = letter

        recordidx = 0
        done = False
        while not done:  # for each Page
            # Move origin up and to the left (ie. left and bottom margins)
            #c.translate( self.margins[ self.BOTTOM ] * inch, self.margins[ self.LEFT ] * inch )
            c.setFont( self.font_name, self.font_size )
            topmargin  = self.margins[ self.TOP    ] * inch
            leftmargin = self.margins[ self.LEFT   ] * inch
            botmargin  = self.margins[ self.BOTTOM ] * inch
            rtmargin   = self.margins[ self.RIGHT  ] * inch
            #rightmargin = self.margins[ self.RIGHT ]

            if self.draw_margins:
                c.line( leftmargin, botmargin, leftmargin, pageheight - topmargin )
                c.line( pagewidth - rtmargin, botmargin, pagewidth - rtmargin, pageheight - topmargin )
                c.line( leftmargin, botmargin, pagewidth - rtmargin, botmargin )
                c.line( leftmargin, pageheight - topmargin, pagewidth - rtmargin, pageheight - topmargin )

            for row in xrange( self.rows ):
                for col in xrange( self.columns ):
                    y = pageheight - topmargin - (row * self.vertical_pitch * inch)
                    x = leftmargin + (col * self.horizontal_pitch * inch)
                    r = records[ recordidx ]
                    self._draw_label( c, x, y, r )
                    recordidx += 1
                    if recordidx >= len(records):
                        done = True
                        break
                if done:
                    break
            c.showPage() # Save current page of canvas and open a new page
        c.save()  # Stores in file and closes canvas
        */
        return true;
    }



    /*
    def _draw_label( self, c, x, y, record ):
        # get max line length
        maxline = ""
        lines = record.display.split( "\n" )
        for line in lines:
            if len(line) > len(maxline):
                maxline = line
        # center text in label
        maxlinelen = c.stringWidth( maxline, self.font_name, self.font_size )
        cx = x + (((self.label_width  * inch) - maxlinelen) / 2)
        # Include margin
        if cx < x + (self.label_margins[ self.LEFT ] * inch):
            cx = x + (self.label_margins[ self.LEFT ] * inch)
        maxwidth = (self.label_width - self.label_margins[ self.LEFT ] - self.label_margins[ self.RIGHT ]) * inch
        if maxlinelen > maxwidth:
            #self.log.debug( "maxlinelen (%d) > maxwidth (%d)" % (maxlinelen, maxwidth) )
            charwidth = c.stringWidth( 'a', self.font_name, self.font_size )
            # One or more of the lines exceed the max width
            nlines = []
            for line in lines:
                w = c.stringWidth( line, self.font_name, self.font_size )
                if w > maxwidth:
                    #self.log.debug( "   Found line '%s' with length %d > maxwidth %d" % (line, w, maxwidth) )
                    maxchars = int(maxwidth / charwidth)
                    #self.log.debug( "      maxchars = %d" % (maxchars) )
                    while line[ maxchars ] != " " and maxchars > 0:
                        maxchars -= 1
                    line1 = line[ :(maxchars + 1) ]
                    line2 = line[ maxchars: ]
                    #self.log.debug( "      line1 = '%s'" % (line1) )
                    #self.log.debug( "      line2 = '%s'" % (line2) )
                    nlines.append( line1 )
                    nlines.append( line2 )
                else:
                    nlines.append( line )
            lines = nlines
        cy = y - (self.label_margins[ self.TOP ] * inch) - self.font_size #(((self.label_height * inch) - (len(lines) * self.font_size)) / 2)
        # Text must not start outside label boundaries
        #if cy > y - (self.label_margins[ self.TOP ] * inch):
            #cy = y - (self.label_margins[ self.TOP ] * inch)
        obj = c.beginText()
        obj.setTextOrigin( cx, cy )
        obj.setFont( self.font_name, self.font_size )
        for line in lines:
            obj.textLine( line )
        c.drawText( obj )
        if self.draw_label_border:
            c.roundRect( x, y - (self.label_height * inch), self.label_width * inch, self.label_height * inch, 5 )
            */

    public String getName(){ return name; }
    public double[] getMargins(){ return margins; }
    public int getRows(){ return rows; }
    public int getColumns(){ return columns; }
    public double getPaperWidth(){ return paperWidth; }
    public double getPaperHeight(){ return paperHeight; }
    public double getLabelWidth(){ return labelWidth; }
    public double getLabelHeight(){ return labelHeight; }
    public double[] getLabelMargins(){ return labelMargins; }

    public double getVerticalPitch(){ return verticalPitch; }
    public double getHorizontalPitch(){ return horizontalPitch; }

    public String getFontName(){ return fontName; }
    public int getFontSize(){ return fontSize; }

    public boolean shouldDrawLabelBorder(){ return drawLabelBorder; }
    public boolean shouldDrawMargins(){ return drawMargins; }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public void setDrawLabelBorder(boolean drawLabelBorder) {
        this.drawLabelBorder = drawLabelBorder;
    }

    public void setDrawMargins(boolean drawMargins) {
        this.drawMargins = drawMargins;
    }
}

