from Tkinter import *
from basedialog import *

class EditTemplateDialog( BaseDialog ):
    def __init__( self, master, app, templ_labels, record ):
        self.app = app
        self.templ_labels = templ_labels
        self.record = record
        self.insert_btns = {}  # { button: label }
        BaseDialog.__init__( self, master, "Edit Template" )


    def body( self, master ):
        lblHelpTxt = Label( master, text="""Edit the template below.  
Click Apply to change only the selected label.  Click Apply to All to change all label templates.""" )
        lblHelpTxt.grid( row=0, column=0, sticky=N+E+W, padx=10, pady=10 )
        self.txtTemplate = Text( master, height=5 )
        self.txtTemplate.grid( row=1, column=0, sticky=N+S+E+W, padx=10, pady=10 )
        frmHelp = Frame( master )
        frmHelp.grid( row=2, column=0, sticky=N+S+E+W, padx=10, pady=10 )
        for idx, lbltxt in enumerate(self.templ_labels):
            lbl = Label( frmHelp, text=lbltxt )
            lbl.grid( row=idx, column=0, sticky=N+S+E+W )
            btn = Button( frmHelp, text="Insert" )
            btn.bind( "<Button-1>", self.insert_label )
            btn.grid( row=idx, column=1 )
            self.insert_btns[ btn ] = lbltxt
        self.txtTemplate.insert( INSERT, self.record.template )
        return None


    def insert_label( self, event ):
        for btn, lbltxt in self.insert_btns.iteritems():
            if btn == event.widget:
                self.txtTemplate.insert( INSERT, "{%s}" % lbltxt )
                self.txtTemplate.insert( INSERT, ", " if lbltxt == "city" else " " )
                break


    def validate( self ):
        return True


    def apply( self ):
        template = self.txtTemplate.get( '1.0', END )
        self.record.template = template
        for lblframe in self.app.sheetframe.labelFrames:
            lblframe.refresh()


    def apply_all( self ):
        template = self.txtTemplate.get( '1.0', END )
        for lblframe in self.app.sheetframe.labelFrames:
            lblframe.record.template = template
            lblframe.refresh()



    def buttonbox( self ):
        box = Frame( self )
        self.btnokall = Button( box, text="Apply to All", width=10, command=self.okall, default=ACTIVE )
        self.btnokall.pack( side=LEFT, padx=5, pady=5 )
        self.btnok = Button( box, text="Apply", width=10, command=self.ok, default=ACTIVE )
        self.btnok.pack( side=LEFT, padx=5, pady=5 )
        self.btncancel = Button( box, text="Cancel", width=10, command=self.cancel )
        self.btncancel.pack( side=LEFT, padx=5, pady=5 )
        self.bind( "<Escape>", self.cancel )
        box.pack()


    def okall( self ):
        if not self.validate():
            self.initial_focus.focus_set()  # put focus back
            return
        self.withdraw()
        self.update_idletasks()
        self.apply_all()
        self.cancel()


