package addresslabel.template;

import java.util.UUID;
import java.util.List;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

import addresslabel.Record;
import addresslabel.util.Logger;

//from reportlab.pdfgen import canvas as pdfcanvas
//from reportlab.lib.pagesizes import letter
//from reportlab.lib.units import inch


public class Template
{
    // Margin indexes
    public static final int TOP    = 0;
    public static final int RIGHT  = 1;
    public static final int BOTTOM = 2;
    public static final int LEFT   = 3;

    private Logger _logger;
    protected String _name;
    protected double[]_margins = { 0, 0, 0, 0 };  // inches
    protected int _rows;
    protected int _columns;
    protected double _paperWidth;
    protected double _paperHeight;
    protected double _labelWidth;
    protected double _labelHeight;
    protected double[] _labelMargins = { 0.125, 0.125, 0.125, 0.125 };

    // The vertical pitch is defined as the measurement from the top of the first label to the top of the label below it. 
    // The horizontal pitch is defined as the measurement from the left edge of the first label to the left edge of the label next to it.
    protected double _verticalPitch;
    protected double _horizontalPitch;

    protected String _fontName;
    protected int _fontSize;

    protected boolean _drawLabelBorder;
    protected boolean _drawMargins;


    public Template( String name )
    {
        _logger = Logger.getLogger( "Template(" + name + ")" );
        _name = name;
        _rows         = 0;
        _columns      = 0;
        _paperWidth   =  8.5;   // inches
        _paperHeight  = 11.0;   // inches
        _labelWidth   =  2.63;  // inches
        _labelHeight  =  1.0;   // inches

        _verticalPitch   = 1.0;   // inches
        _horizontalPitch = 1.0;   // inches

        _fontName = "Helvetica";
        _fontSize = 10;

        _drawLabelBorder = true;
        _drawMargins = false;
    }


    public String getPrintableDoc( List<Record> records, String filename ) throws IOException
    {
        if( records.size() == 0 )
            return null;
        String fn = filename != null? filename: _getTempFilename();
        BufferedWriter bw = new BufferedWriter( new FileWriter( fn ) );
        writePdf( bw, records );
        bw.close();
        return fn;
    }


    private String _getTempFilename()
    {
        if( System.getProperty( "os.name" ).toLowerCase().startsWith( "win" ) )
            return "C:\\temp\\" + UUID.randomUUID() + ".pdf";
        return "/tmp/" + UUID.randomUUID() + ".pdf";
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

