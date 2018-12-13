import subprocess
import sys
from logger import *


class Printer( object ):
    def __init__( self, app ):
        self.app = app
        self.log = getLogger( "Printer" )


    def print_labels( self ):
        if sys.platform.lower().startswith( "linux" ):
            self.__print_linux_lpr()
        elif sys.platform.lower() == "windows":
            self.__print_windows()
        else:
            self.log.error( "Unsupported platform: %s" % sys.platform )



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



"""
Pros:
- Takes care of standard file types
- No need to mess around with printer lists

Cons:
- Gives you no control
- Only works for well-defined document-application pairings.
- Only prints to default printer

UPDATE: Kudos to Chris Curvey for pointing out that you can specify a printer by including it with a d: switch in the params section. Don't know if it works for every file type.

import tempfile
import win32api
import win32print

filename = tempfile.mktemp (".txt")
open (filename, "w").write ("This is a test")
win32api.ShellExecute (
    0,
    "print",
    filename,
    #
    # If this is None, the default printer will
    # be used anyway.
    #
    '/d:"%s"' % win32print.GetDefaultPrinter (),
    ".",
    0
)

UPDATE 2: Mat Baker & Michael "micolous" both point out that there's an underdocumented printto verb which takes the printer name as a parameter, enclosed in quotes if it contains spaces. I haven't got this to work but they both report success for at least some file types.

import tempfile
import win32api
import win32print

filename = tempfile.mktemp (".txt")
open (filename, "w").write ("This is a test")
win32api.ShellExecute (
    0,
    "printto",
    filename,
    '"%s"' % win32print.GetDefaultPrinter (),
    ".",
    0
    )



Raw printable data: use win32print directly

The win32print module offers (almost) all the printing primitives you'll need to take some data and throw it at a printer which has already been defined on your system. The data must be in a form which the printer will happily swallow, usually something like text or raw PCL.

Pros:
- Quick and easy
- You can decide which printer to use

Cons:
- Data must be printer-ready

import os, sys
import win32print
printer_name = win32print.GetDefaultPrinter ()
#
# raw_data could equally be raw PCL/PS read from
#  some print-to-file operation
#
if sys.version_info >= (3,):
    raw_data = bytes ("This is a test", "utf-8")
else:
    raw_data = "This is a test"

hPrinter = win32print.OpenPrinter (printer_name)
try:
    hJob = win32print.StartDocPrinter (hPrinter, 1, ("test of raw data", None, "RAW"))
    try:
        win32print.StartPagePrinter (hPrinter)
        win32print.WritePrinter (hPrinter, raw_data)
        win32print.EndPagePrinter (hPrinter)
    finally:
        win32print.EndDocPrinter (hPrinter)
finally:
    win32print.ClosePrinter (hPrinter)



Lots of text: generate a PDF

You could just send text directly to the printer, but you're at the mercy of whatever fonts and margins and what-have-you the printer has defined. Rather than start emitting raw PCL codes you can generate PDFs and let Acrobat look after printing. The Reportlab toolkit does this supremely well, and especially its Platypus document framework, which gives you the ability to generate pretty much arbitrarily complex documents. The example below hardly scratches the surface of the toolkit, but shows that you don't need two pages of setup code to generate a perfectly usable PDF. Once this is generated, you can use the ShellExecute technique outlined above to print.

from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib.units import inch

import cgi
import tempfile
import win32api

source_file_name = "c:/temp/temp.txt"
pdf_file_name = tempfile.mktemp (".pdf")

styles = getSampleStyleSheet ()
h1 = styles["h1"]
normal = styles["Normal"]

doc = SimpleDocTemplate (pdf_file_name)
#
# reportlab expects to see XML-compliant
#  data; need to escape ampersands &c.
#
text = cgi.escape (open (source_file_name).read ()).splitlines ()

#
# Take the first line of the document as a
#  header; the rest are treated as body text.
#
story = [Paragraph (text[0], h1)]
for line in text[1:]:
    story.append (Paragraph (line, normal))
    story.append (Spacer (1, 0.2 * inch))

doc.build (story)
win32api.ShellExecute (0, "print", pdf_file_name, None, ".", 0)

"""

