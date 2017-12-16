from Tkinter import *
import os

class BaseDialog( Toplevel ):
    def __init__( self, parent, title=None, show_on_create=True, oktext="OK", canceltext="Cancel", ok_on_return=True ):
        Toplevel.__init__( self, parent )
        self.transient( parent )
        self.oktext = oktext
        self.canceltext = canceltext
        self.ok_on_return = ok_on_return

        if title:
            self.title( title )

        self.parent = parent
        self.result = None

        body = Frame( self )
        self.initial_focus = self.body( body )
        body.pack( padx=5, pady=5 )

        self.buttonbox()
        try:
            self.grab_set()
        except:
            pass

        if not self.initial_focus:
            self.initial_focus = self

        self.protocol( "WM_DELETE_WINDOW", self.cancel )
        self.geometry( "+%d+%d" % (parent.winfo_rootx() + 50, parent.winfo_rooty() + 50) )
        self.initial_focus.focus_set()
        if show_on_create:
            self.show()


    def show( self ):
        self.wait_window( self )


    def body( self, master ):
        # Create dialog body.  Return widget that should have initial focus.  This method should be overridden.
        pass


    def buttonbox( self ):
        # add standard button box.  Override if you don't want the standard buttons.
        box = Frame( self )
        self.btnok = Button( box, text=self.oktext, width=10, command=self.ok, default=ACTIVE )
        self.btnok.pack( side=LEFT, padx=5, pady=5 )
        self.btncancel = Button( box, text=self.canceltext, width=10, command=self.cancel )
        self.btncancel.pack( side=LEFT, padx=5, pady=5 )
        if self.ok_on_return:
            self.bind( "<Return>", self.ok )
        self.bind( "<Escape>", self.cancel )
        box.pack()


    def ok( self, event=None ):
        if not self.validate():
            self.initial_focus.focus_set()  # put focus back
            return
        self.withdraw()
        self.update_idletasks()
        self.apply()
        self.cancel()


    def cancel( self, event=None ):
        # Put focus back to parent window
        self.parent.focus_set()
        self.destroy()


    def validate( self ):
        # Override
        return 1


    def apply( self ):
        # Override
        pass


