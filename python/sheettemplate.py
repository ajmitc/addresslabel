import traceback, sys
import uuid
from logger import *
from reportlab.pdfgen import canvas as pdfcanvas
from reportlab.lib.pagesizes import letter
from reportlab.lib.units import inch


class SheetTemplate( object ):
    # Margin indexes
    TOP    = 0
    RIGHT  = 1
    BOTTOM = 2
    LEFT   = 3

    def __init__( self, name ):
        self.log  = getLogger( "SheetTemplate(%s)" % name )
        self.name = name
        self.margins       = [ 0, 0, 0, 0 ]  # inches
        self.rows          = 0
        self.columns       = 0
        self.paper_width   =  8.5   # inches
        self.paper_height  = 11.0   # inches
        self.label_width   =  2.63  # inches
        self.label_height  =  1.0   # inches
        self.label_margins = [ 0.125, 0.125, 0.125, 0.125 ]

        # The vertical pitch is defined as the measurement from the top of the first label to the top of the label below it. 
        # The horizontal pitch is defined as the measurement from the left edge of the first label to the left edge of the label next to it.
        self.vertical_pitch   = 1.0   # inches
        self.horizontal_pitch = 1.0   # inches

        self.font_name = "Helvetica"  # canvas.getAvailableFonts()
        self.font_size = 10 

        self.draw_label_border = True
        self.draw_margins = False


    def get_printable_doc( self, records, filename=None ):
        if len(records) == 0:
            return None
        fn = filename if filename is not None else self.__get_temp_filename()
        fd = open( fn, "wb" )
        self.write_pdf( fd, records )
        fd.close()
        return fn


    def __get_temp_filename( self ):
        if sys.platform == "windows":
            return "C:\\temp\\%s.pdf" % uuid.uuid4()
        return "/tmp/%s.pdf" % uuid.uuid4()


    def write_pdf( self, infd, records ):
        """ Write the pdf to the input file descriptor (infd) """
        if len(records) == 0:
            return False
        # Canvas constructor:
        # def __init__( self, filename, pagesize=(595.27,841.89), bottomup = 1, pageCompression=0, encoding=rl_config.defaultEncoding, verbosity=0, encrypt=None )
        # filename is string or open file descriptor
        # pagesize is 2-tuple of points (1/72 of an inch) - default is A4
        # bottomup dictates coordinate system.  This is deprecated.  PDF and Postscript have coordinate (0,0) at lower-left corner!
        # pageCompression dictates if the pdf should be compressed (smaller size, but takes longer to generate).  Only affects text and vector graphics (images are always compressed).
        # encoding is mostly deprecated.  Leave as default.
        # verbosity gives log info
        # encrypt is a password (if not None) to encrypt pdf
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
        return True



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



class Avery5160Template( SheetTemplate ):
    def __init__( self ):
        """ Set the top margin at .5 inches, side margin to .19 inches, vertical pitch to 1 inch, horizontal pitch to 2.75 inches, 
            paper size to 8.5 by 11 inches, label height at 1 inch and label width at 2.63 inches. The number across is three and the number down is 10."""
        SheetTemplate.__init__( self, "Avery 5160" )
        self.rows = 10
        self.columns = 3
        self.paper_width  =  8.5    # inches
        self.paper_height = 11.0    # inches
        self.label_width  =  2.625  # inches
        self.label_height =  1.0    # inches
        self.margins[ SheetTemplate.TOP    ] = 0.5  
        self.margins[ SheetTemplate.RIGHT  ] = 0.19  # 0.21975
        self.margins[ SheetTemplate.LEFT   ] = 0.19  # 0.21975
        self.margins[ SheetTemplate.BOTTOM ] = 0.5
        self.vertical_pitch   = 1.0    # inches
        self.horizontal_pitch = 2.75   # inches


