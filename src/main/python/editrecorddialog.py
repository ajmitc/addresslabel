from Tkinter import *
from basedialog import *
from scrolledframe import ScrolledFrame

class EditRecordDialog( BaseDialog ):
    def __init__( self, master, app, record ):
        self.app = app
        self.record = record
        self.vars   = []  # [ (keyvar, valvar), ]
        BaseDialog.__init__( self, master, "Edit Record", oktext='Apply' )


    def body( self, master ):
        lblHelpTxt = Label( master, text="Edit the record below." )
        lblHelpTxt.grid( row=0, column=0, sticky=N+E+W, padx=10, pady=10 )
        frmFields = ScrolledFrame( master )
        frmFields.grid( row=1, column=0, sticky=N+S+E+W, padx=10, pady=10 )
        keys = self.record.LABELS + sorted( [ key for key in self.record.data.keys() if key not in self.record.LABELS ] )
        for idx, key in enumerate(keys):
            if key in self.record.LABEL_IGNORE:
                continue
            keyvar = StringVar()
            keyvar.set( key )
            keyentry = Entry( frmFields.interior, textvariable=keyvar )
            keyentry.grid( row=idx, column=0, sticky=N+S+E+W )
            valvar = StringVar()
            valvar.set( self.record.data[ key ] )
            valentry = Entry( frmFields.interior, textvariable=valvar )
            valentry.grid( row=idx, column=1, sticky=N+S+E+W )
            self.vars.append( (keyvar, valvar) )
        return None


    def apply( self ):
        self.record.clear_data()
        for keyvar, valvar in self.vars:
            self.record.data[ keyvar.get() ] = valvar.get()


