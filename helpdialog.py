from Tkinter import *
from basedialog import BaseDialog


class HelpDialog( BaseDialog ):
    CONTENT = [
        ("How to use this program:", ("Arial", 12)),
        ("""
        1) Open a Contact List CSV File (ie. exported from Google Contacts using Outlook or other format)
        2) Update any records by right-clicking on the record and selecting 'Edit Record'
        3) Update any label templates by right-clicking on the record and selecting 'Edit Template'
        4) Manually edit label display by directly changing text in Record box (this will not change the underlying CSV record)
        5) Click 'Print' to send the document to the default printer.  If you need to select a different printer or printer options, export to PDF and print from the default PDF viewer application.
        """, ("Arial", 10)),
    ]

    def __init__( self, app ):
        self.app = app
        BaseDialog.__init__( self, app )


    def body( self, master ):
        r = 0
        Label( master, text="Address Label Easy Button", font=("Arial", 16), padx=5, pady=5 ).grid( row=r )
        r += 1
        for entry in self.CONTENT:
            text = entry[ 0 ]
            font = ("Arial", 10)
            if len(entry) >= 2:
                font = entry[ 1 ]
            Label( master, text=text, font=font, wraplength=1000, justify=LEFT, anchor=NW ).grid( row=r, sticky=NW )
            r += 1
        return None


    def buttonbox( self ):
        box = Frame( self )
        w = Button( box, text="Close", width=10, command=self.cancel )
        w.pack( side=LEFT, padx=5, pady=5 )
        self.bind( "<Escape>", self.cancel )
        box.pack()

