package addresslabel.util;

public class Printer
{
    private Logger _logger;

    public Printer()
    {
        _logger = Logger.getLogger( "Printer" );
    }


    public void printLabels()
    {
        /*
        if sys.platform.lower().startswith( "linux" ):
            self.__print_linux_lpr()
        elif sys.platform.lower() == "windows":
            self.__print_windows()
        else:
            self.log.error( "Unsupported platform: %s" % sys.platform )
        */
    }



    /*
    def __print_linux_lpr( self ):
        #fn = self.app.sheetframe.sheet_template.get_printable_doc( self.app.records )
        #self.log.debug( "PDF: %s" % fn )
        lpr = subprocess.Popen( "/usr/bin/lpr", stdin=subprocess.PIPE )
        self.app.sheetframe.sheet_template.write_pdf( lpr.stdin, self.app.records )
        lpr.stdin.close()
        #lpr = subprocess.Popen( "/usr/bin/lpr", stdin=subprocess.PIPE )
        #fd = open( fn, 'rb' )
        #lpr.stdin.write( fd.read() )
        #fd.close()


    def __print_linux_ipp( self ):
        # CUPS' API
        from pkipplib import pkipplib
        # Create a CUPS client instance 
        # cups = pkipplib.CUPS(url="http://server:631, \
        #                      username="john", \
        #                      password="5.%!oyu")
        cups = pkipplib.CUPS()
        # High level API : retrieve info about job 3 :
        answer = cups.getJobAttributes(3)
        print answer.job["document-format"]
        # That's all folks !
        # Lower level API :
        request = cups.newRequest(pkipplib.IPP_GET_PRINTER_ATTRIBUTES)
        request.operation["printer-uri"] = ("uri", )
        for attribute in ("printer-uri-supported", "printer-type", "member-uris"):
            # IMPORTANT : here, despite the unusual syntax, we append to              
            # the list of requested attributes :
            request.operation["requested-attributes"] = ("nameWithoutLanguage", attribute)
        # Sends this request to the CUPS server    
        answer = cups.doRequest(request)    
        # Print the answer as a string of text
        print answer



    def __print_windows( self ):
        # Print PDF with Ghostscript
        # gsprint -printer \\server\printer "test.pdf"
        # in python:
        #    win32api.ShellExecute( 0, 'open', 'gsprint.exe', '-printer "\\\\' + self.server + '\\' + self.printer_name + '" ' + file, '.', 0 )
        import tempfile
        import win32api
        import win32print
        #filename = tempfile.mktemp( ".txt" )
        filename = self.app.sheetframe.sheet_template.get_printable_doc( self.app.records, tempfile.mktemp( ".txt" ) )
        #open( filename, "w" ).write(  )
        win32api.ShellExecute( 0, "print", filename,
            # If this is None, the default printer will
            # be used anyway.
            '/d:"%s"' % win32print.GetDefaultPrinter(),
            ".",
            0
        )
        */
}
