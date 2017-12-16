###############################################################################
# Address Label Easy-Button
#
# Load in contact list (in CSV format) and easily create printable mailing
# labels.
#
# Version 1.0.0
# - Only supports Avery 5160 Labels
# 
###############################################################################
try:
    from Tkinter import *
    import tkFileDialog as filedialog
except:
    from tkinter import *
    import filedialog
import tkMessageBox
import traceback
import csv, re, math, uuid, os, subprocess, sys
from edittemplatedialog import EditTemplateDialog
from editrecorddialog import EditRecordDialog
from helpdialog import HelpDialog
from aboutdialog import AboutDialog
from logger import *
from printutil import Printer
from sheettemplate import *


class Record( object ):
    TITLE            = "title"
    NAME             = "name"
    FIRST_NAME       = "first name"
    MIDDLE_NAME      = "middle name"
    LAST_NAME        = "last name"
    SUFFIX           = "suffix"
    ADDRESS          = "home address"
    ADDRESS_STREET_1 = "home street"
    ADDRESS_STREET_2 = "home street 2"
    ADDRESS_CITY     = "home city"
    ADDRESS_STATE    = "home state"
    ADDRESS_ZIP      = "home postal code"
    ADDRESS_COUNTRY  = "home country"
    ADDRESS_COUNTRY_NOT_USA = "home country not usa"

    LABELS = [
        TITLE,
        NAME,
        FIRST_NAME,
        MIDDLE_NAME,
        LAST_NAME,
        SUFFIX,
        ADDRESS,
        ADDRESS_STREET_1,
        ADDRESS_STREET_2,
        ADDRESS_CITY,
        ADDRESS_STATE,
        ADDRESS_ZIP,
        ADDRESS_COUNTRY,
        ADDRESS_COUNTRY_NOT_USA,
    ]

    # These labels do not have corresponding values, but add functionality to existing labels
    LABEL_IGNORE = [
        ADDRESS_COUNTRY_NOT_USA,
    ]

    LABEL_MAPPING = {
        ADDRESS:          [ "address" ],
        ADDRESS_STREET_1: [ "street", "street 1" ],
        ADDRESS_STREET_2: [ "street 2" ],
        ADDRESS_CITY:     [ "city" ],
        ADDRESS_STATE:    [ "state" ],
        ADDRESS_ZIP:      [ "zip", "zipcode" ],
        ADDRESS_COUNTRY:  [ "country" ],
    }


    REGEXP_MATCH_LABEL_TAG = r"\{[a-zA-Z0-9 _]+\}"

    def __init__( self, app, record, header=None ):
        self.app = app
        self.log = getLogger( "Record" )
        self.regexp_label_tag = re.compile( self.REGEXP_MATCH_LABEL_TAG )
        self.clear_data()
        self.__map_record( record, header )
        self._template = None  # If None, use default label template in app 
        self._display  = None  # Displayed text


    def clear_data( self ):
        self.data = dict( [ (lbl, "") for lbl in self.LABELS if lbl not in self.LABEL_IGNORE ] )  # { FIRST_NAME: "Aaron", LAST_NAME: "Mitchell" }


    def search( self, text ):
        """ Return True if 'text' is found within this Record, False otherwise """
        return self.display.find( text ) >= 0


    def __map_record( self, rawdata, header=None ):
        if type(rawdata) is list and header is not None and type(header) is list:
            return self.__map_record_list_with_header_list( rawdata, header )
        self.log.warning( "Unsupported record format" )
        return False


    def __map_record_list_with_header_list( self, rawdata, header ):
        for i in xrange(len(header)):
            h = header[ i ]
            if len(rawdata) <= i:
                self.log.warning( "record length (%d) on line %d has fewer fields than header (%d)" % (len(rawdata), i + 1, len(header)) )
                continue
            v = rawdata[ i ]
            key = self.__map_header( h )
            if key is not None:
                self.data[ key ] = v
            else:
                self.data[ h   ] = v
        return True


    def __map_header( self, header ):
        header = header.strip().lower()
        for lbl in self.LABELS:
            if lbl.lower() == header:
                return lbl
            if lbl.lower().replace( " ", "_" ) == header:
                return lbl
        for key, options in self.LABEL_MAPPING.iteritems():
            for option in options:
                if option == header:
                    return key
        return None


    def __format( self, template ):
        for lbl in self.LABELS:
            if lbl == self.ADDRESS_COUNTRY_NOT_USA:
                if self.data[ self.ADDRESS_COUNTRY ].lower() not in [ "", "usa", "united states of america" ]:
                    template = template.replace( "{%s}" % lbl, self.data[ self.ADDRESS_COUNTRY ] )
                else:
                    template = template.replace( "{%s}" % lbl, "" )
            else:
                template = template.replace( "{%s}" % lbl, self.data[ lbl ] )
        template = self.regexp_label_tag.sub( "", template )
        while template.find( "  " ) >= 0:
            template = re.sub( r"  +", " ", template )
        template = re.sub( "\n\n", "\n", template )
        return template.strip()


    @property
    def display( self ):
        if self._display is None:
            self._display = self.__format( self._template if self._template is not None else self.app.def_label_template )
        return self._display


    @display.setter
    def display( self, d ):
        #self.log.error( "You cannot set the display property explicitly!" )
        self._display = d


    @property
    def template( self ):
        return self._template if self._template is not None else self.app.def_label_template


    @template.setter
    def template( self, t ):
        self._template = t
        self._display  = None


    def __str__( self ):
        return str(self.data)




class LabelFrame( object, Frame ):
    def __init__( self, master, app ):
        self.master = master
        self.app    = app
        self.log    = getLogger( "LabelFrame" )
        Frame.__init__( self, self.master, bd=3 )
        self.createWidgets()
        # create a popup menu
        self.popup = Menu( self, tearoff=0 )
        self.popup.add_command( label="Edit Record",   command=self.edit_record )
        self.popup.add_command( label="Edit Template", command=self.edit_label_template )
        self.popup.add_command( label="Edit Text",     command=self.edit_display )
        self.popup.add_command( label="Refresh",       command=self.refresh )
        self.popup.add_separator()
        self.popup.add_command( label="Remove",        command=self.remove_record )
        self._record = None


    def createWidgets( self ):
        self.txtlabel = Text( self, height=5, width=35, wrap=WORD )
        self.txtlabel.config( state=DISABLED )
        self.txtlabel.pack( fill=BOTH, expand=1, anchor=NW )
        self.txtlabel.bind( "<Button-3>", self.right_click )
        self.txtlabel.bind( "<FocusOut>", self.save )


    def right_click( self, event=None ):
        # display the popup menu
        if self._record is not None:
            #self.popup.post( event.x_root, event.y_root )
            self.popup.tk_popup( event.x_root, event.y_root )


    def edit_label_template( self, event=None ):
        if self._record is not None:
            EditTemplateDialog( self.app, self.app, Record.LABELS, self._record )
            self.refresh( True )


    def edit_record( self, event=None ):
        if self._record is not None:
            EditRecordDialog( self.app, self.app, self._record )
            self.refresh( True )


    def edit_display( self, event=None ):
        self.txtlabel.config( state=NORMAL )


    def remove_record( self, event=None ):
        if self._record is not None:
            self.app.records.remove( self._record )
            self.app.display_page( self.app.page )


    @property
    def record( self ):
        return self._record


    @record.setter
    def record( self, v ):
        self._record = v
        self.refresh()


    def refresh( self, clear_display=False ):
        self.txtlabel.config( state=NORMAL )
        self.txtlabel.delete( "1.0", END )
        if self._record is not None:
            if clear_display:
                self._record.display = None
            self.txtlabel.insert( INSERT, self._record.display )
        self.txtlabel.config( state=DISABLED )


    def save( self, event=None ):
        if self._record is not None:
            self._record.display = self.txtlabel.get( "1.0", END )
            self.txtlabel.config( state=DISABLED, relief=SUNKEN, bd=0 )



class SheetFrame( object, Frame ):
    def __init__( self, master, app, sheet_template ):
        self.master = master
        self.app    = app
        self.log    = getLogger( "SheetFrame" )
        Frame.__init__( self, self.master )
        self.sheet_template = sheet_template
        self.labelFrames = []
        self.createWidgets()
        self.highlighted = None


    def createWidgets( self ):
        for t in self.labelFrames:
            t.grid_remove()
        self.labelFrames = []
        for row in xrange(self.sheet_template.rows):
            for col in xrange(self.sheet_template.columns):
                txtlabel = LabelFrame( self, self.app )
                txtlabel.grid( row=row, column=col, sticky=N+S+E+W )
                self.labelFrames.append( txtlabel )
        if len(self.labelFrames) > 0:
            self.orig_bg = self.labelFrames[ 0 ].cget( "background" )
        else:
            self.orig_bg = "white"


    def display( self, records ):
        """ Display contact data.  data must be in format: [ Record, Record, ... ] """
        #self.log.debug( "Displaying %d records" % len(records) )
        for t in self.labelFrames:
            t.record = None
        for i in xrange( min( len(records), len(self.labelFrames) ) ):
            lblfrm = self.labelFrames[ i ]
            #self.log.debug( "   Setting record in LabelFrame" )
            lblfrm.record = records[ i ]


    def save_labels( self ):
        for t in self.labelFrames:
            t.save()


    def highlight_label_with_record( self, record ):
        if self.highlighted is not None:
            self.highlighted.config( bd=3, background=self.orig_bg )
        for txtlabel in self.labelFrames:
            if txtlabel.record == record:
                txtlabel.config( bd=3, background="red" )
                self.highlighted = txtlabel



class Application( Frame ):
    VERSION = "1.0.0"

    CONFIG_FILE = "addrlbl.conf"

    TEMPLATES = [
        Avery5160Template(),
    ]

    def __init__( self, master ):
        init_logging()
        self.log = getLogger( "App" )
        Frame.__init__( self, master, borderwidth=5 )
        self.master  = master
        self.templateidx = 0
        self.createWidgets()
        self.options = {
            #self.OPTION_OPEN_SECTION_MODE: StringVar(),
        }
        #self.options[ self.OPTION_OPEN_SECTION_MODE ].set( self.OPTION_OPEN_SECTION_MODE_NEXT )
        self.header = None
        self.records = []
        self.loaded_filepath = None
        self.page = 0  # Currently displayed page

        # default format template
        self.def_label_template = "{%s} {%s} {%s} {%s} {%s}\n" % (Record.TITLE, Record.FIRST_NAME, Record.MIDDLE_NAME, Record.LAST_NAME, Record.SUFFIX)
        self.def_label_template += "{%s}\n" % (Record.ADDRESS_STREET_1, )
        self.def_label_template += "{%s}\n" % (Record.ADDRESS_STREET_2, )
        self.def_label_template += "{%s}, {%s} {%s}\n" % (Record.ADDRESS_CITY, Record.ADDRESS_STATE, Record.ADDRESS_ZIP)
        self.def_label_template += "{%s}\n" % (Record.ADDRESS_COUNTRY_NOT_USA)

        self.search_results = []
        self.search_results_idx = 0

    def createWidgets( self ):
        menubar = Menu( self )
        filemenu = Menu( menubar, tearoff=0 )
        #filemenu.add_command( label="New", command=self.new_story )
        #filemenu.add_separator()
        filemenu.add_command( label="Open CSV", command=self.load )
        #filemenu.add_command( label="Save", command=self.save )
        #filemenu.add_command( label="Save As", command=self.saveas )
        filemenu.add_separator()
        filemenu.add_command( label="Save CSV",    command=self.save )
        filemenu.add_command( label="Save As CSV", command=self.saveas )
        filemenu.add_separator()
        filemenu.add_command( label="Export to PDF", command=self.export_to_pdf )
        filemenu.add_command( label="Print", command=self.print_labels )
        filemenu.add_separator()
        filemenu.add_command( label="Exit", command=self.exit )
        menubar.add_cascade( label="File", menu=filemenu )

        self.templvar = IntVar()
        self.templvar.set( 0 )
        templmenu = Menu( menubar, tearoff=0 )
        for idx, templ in enumerate(self.TEMPLATES):
            templmenu.add_radiobutton( label=templ.name, command=self.select_template, variable=self.templvar, value=idx )
        menubar.add_cascade( label="Template", menu=templmenu )

        helpmenu = Menu( menubar, tearoff=0 )
        helpmenu.add_command( label="Manual", command=self.display_help )
        helpmenu.add_command( label="About",  command=self.display_about )
        menubar.add_cascade( label="Help", menu=helpmenu )

        self.master.config( menu=menubar )

        frmToolbar = Frame( self, borderwidth=1, relief=RAISED )
        #btnNew     = Button( frmToolbar, text='New', command=self.new_story )
        #btnNew.pack( side=LEFT )
        btnLoad    = Button( frmToolbar, text='Open CSV', command=self.load )
        btnLoad.pack( side=LEFT )
        #btnSave    = Button( frmToolbar, text='Save', command=self.save_story )
        #btnSave.pack( side=LEFT )
        Label( frmToolbar, text="  " ).pack( side=LEFT )
        btnExportPdf = Button( frmToolbar, text="Export PDF", command=self.export_to_pdf )
        btnExportPdf.pack( side=LEFT )
        btnPrint = Button( frmToolbar, text='Print', command=self.print_labels )
        btnPrint.pack( side=LEFT )
        Label( frmToolbar, text="  " ).pack( side=LEFT )
        lblSearch = Label( frmToolbar, text="Search: " )
        lblSearch.pack( side=LEFT )
        self.searchvar = StringVar()
        self.searchvar.set( "" )
        self.entrySearch = Entry( frmToolbar, textvariable=self.searchvar, width=10 )
        self.entrySearch.pack( side=LEFT )
        self.entrySearch.bind( "<KeyRelease>", self.search_key_released )
        btnSearchNext = Button( frmToolbar, text="Next", command=self.find_search_next )
        btnSearchNext.pack( side=LEFT )

        btnNextPage = Button( frmToolbar, text='>', command=self.display_next_page )
        btnNextPage.pack( side=RIGHT )
        btnPrevPage = Button( frmToolbar, text='<', command=self.display_prev_page )
        btnPrevPage.pack( side=RIGHT )
        self.lblPageVar = StringVar()
        self.lblPageVar.set( "Page 0 of 0" )
        lblPage = Label( frmToolbar, textvariable=self.lblPageVar )
        lblPage.pack( side=RIGHT )

        self.sheetframe = SheetFrame( self, self, self.TEMPLATES[ self.templateidx ] )

        frmToolbar.pack( side=TOP, fill=X, expand=1 )
        self.sheetframe.pack( fill=BOTH, expand=1 )

        self.pack()


    def exit( self ):
        self.master.quit()
        self.master.destroy()


    def display_help( self ):
        HelpDialog( self )


    def display_about( self ):
        AboutDialog( self )


    def print_labels( self ):
        self.sheetframe.save_labels()
        if len(self.records) == 0:
            return
        printer = Printer( self )
        printer.print_labels()


    def export_to_pdf( self ):
        if len(self.records) == 0:
            return
        self.sheetframe.save_labels()
        fn = self.sheetframe.sheet_template.get_printable_doc( self.records, "contact-labels.pdf" )
        if fn is None:
            return
        if sys.platform.startswith( 'linux' ):
            subprocess.call( [ "xdg-open", fn ] )
        else:
            os.startfile( fn )


    def new( self ):
        pass


    def load( self ):
        filepath = filedialog.askopenfilename( parent=self, defaultextension="csv", initialdir=".", title="Open Contact List", filetypes=(("Comma-Separated-Values", "*.csv"), ("All Files", "*.*")) )
        if filepath:
            if not self.load_contacts( filepath ):
                self.log.error( "Contacts failed to load" )
            else:
                self.log.info( "Loaded contacts: %s" % filepath )
                self.loaded_filepath = filepath
                


    def load_contacts( self, filepath ):
        if filepath.endswith( ".csv" ):
            return self.load_contacts_csv( filepath )
        self.log.error( "Unsupported file format" )
        return False


    def load_contacts_csv( self, filepath ):
        """ Load a CSV Contact list.  The first row must be a header, the delimiter must be a comma ',' and record delimiter must be newline """
        self.log.info( "Loading %s" % filepath )
        with open( filepath, "rb" ) as csvfile:
            csvreader = csv.reader( csvfile, delimiter=',' ) # quotechar='|'
            for row in csvreader:
                #self.log.debug( ", ".join( row ) )
                if self.header is None:
                    self.header = row
                else:
                    self.records.append( Record( self, row, self.header ) )
        #csvreader.close()
        #self.log.debug( "Read in records:" )
        #self.log.debug( str(self.header) )
        #for record in self.records:
            #self.log.debug( str(record) )
        self.page = 0
        self.sheetframe.display( self.records )
        num_pages = int(math.ceil( float(len(self.records)) / float(self.TEMPLATES[ self.templateidx ].rows * self.TEMPLATES[ self.templateidx ].columns) ))
        self.lblPageVar.set( "Page %d of %d" % (self.page + 1, num_pages) )
        return True



    def save( self ):
        if self.loaded_filepath is not None:
            self.write_csv( self.loaded_filepath )


    def saveas( self ):
        filepath = filedialog.asksaveasfilename( parent=self, defaultextension="csv", initialdir=".", title="Save Contact List", filetypes=(("Comma-Separated-Values", "*.csv"), ("All Files", "*.*")) )
        if filepath:
            self.write_csv( filepath )
            self.loaded_filepath = filepath


    def write_csv( self, filepath ):
        self.log.debug( "Saving csv to %s" % filepath )
        with open( filepath, 'wb' ) as csvfile:
            csvwriter = csv.writer( csvfile, delimiter=',' )
            header = None
            for record in self.records:
                if header is None:
                    header = record.data.keys()
                    csvwriter.writerow( header )
                    #self.log.debug( str(header) )
                row = [ record.data[ h ] if h in record.data.keys() else "" for h in header ]
                csvwriter.writerow( row )
                #self.log.debug( str(row) )


    def display_prev_page( self ):
        if self.page == 0:
            return
        self.display_page( self.page - 1 )
        #self.page -= 1
        #records_per_page = self.TEMPLATES[ self.templateidx ].rows * self.TEMPLATES[ self.templateidx ].columns
        #self.sheetframe.display( self.records[ (self.page * records_per_page): ] )
        #num_pages = int(math.ceil( float(len(self.records)) / float(records_per_page) ))
        #self.lblPageVar.set( "Page %d of %d" % (self.page + 1, num_pages) )


    def display_next_page( self ):
        records_per_page = self.TEMPLATES[ self.templateidx ].rows * self.TEMPLATES[ self.templateidx ].columns
        if self.page < math.ceil( float(len(self.records)) / float(records_per_page) ) - 1:
            #self.page += 1
            self.display_page( self.page + 1 )
            #self.sheetframe.display( self.records[ (self.page * records_per_page): ] )
            #num_pages = int(math.ceil( float(len(self.records)) / float(records_per_page) ))
            #self.lblPageVar.set( "Page %d of %d" % (self.page + 1, num_pages) )


    def display_page( self, pagenum ):
        self.page = pagenum
        records_per_page = self.TEMPLATES[ self.templateidx ].rows * self.TEMPLATES[ self.templateidx ].columns
        self.sheetframe.display( self.records[ (self.page * records_per_page): ] )
        num_pages = int(math.ceil( float(len(self.records)) / float(records_per_page) ))
        self.lblPageVar.set( "Page %d of %d" % (self.page + 1, num_pages) )
    

    def select_template( self ):
        if self.templvar.get() == self.templateidx:
            return
        self.templateidx = self.templvar.get()
        templ = self.TEMPLATES[ self.templateidx ]
        self.sheetframe.sheet_template = templ
        self.sheetframe.createWidgets()
        if len(self.records) > 0:
            self.page = 0
            self.sheetframe.display( self.records )


    def search_key_released( self, event=None ):
        if len(self.records) == 0:
            return
        self.search_results = []
        self.search_results_idx = 0
        search = self.searchvar.get()
        #self.log.debug( "Searching for '%s'" % search )
        for idx, record in enumerate(self.records):
            if record.search( search ):
                self.search_results.append( (idx, record) )
                self.search_results_idx = -1
        if len(self.search_results) > 0:
            #self.log.debug( "Search Results:" )
            #for sr in self.search_results:
                #self.log.debug( "   %d" % sr[ 0 ] )
            self.find_search_next()


    def find_search_next( self ):
        if len(self.search_results) == 0:
            return
        self.search_results_idx = (self.search_results_idx + 1) % len(self.search_results)
        idx, record = self.search_results[ self.search_results_idx ]
        # Get page
        records_per_page = self.TEMPLATES[ self.templateidx ].rows * self.TEMPLATES[ self.templateidx ].columns
        page = int(idx / records_per_page)
        self.display_page( page )
        self.sheetframe.highlight_label_with_record( record )



if __name__ == "__main__":
    try:
        root = Tk()
        root.wm_title( "Address Label Easy Button" )

        app = Application( master=root )
        root.protocol( "WM_DELETE_WINDOW", app.exit )

        app.mainloop()
        try:
            root.destroy()
        except:
            pass
    except Exception, e:
        traceback.print_exc()

