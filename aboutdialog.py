from Tkinter import *
from basedialog import BaseDialog


class AboutDialog( BaseDialog ):
    COPYRIGHT_SYMBOL = u"\u00A9"

    CONTENT = [
        ("Version {version}", ("Arial", 10)),
        ("Developed by Aaron Mitchell", ("Arial", 10)),
        (u"Copyright \u00A9 2017 Aaron Mitchell", ("Arial", 12)),
    ]

    def __init__( self, app ):
        self.app = app
        BaseDialog.__init__( self, app )


    def body( self, master ):
        r = 0
        Label( master, text="Address Label Easy Button", font=("Arial", 16), padx=5, pady=5 ).grid( row=r )
        r += 1
        for entry in self.CONTENT:
            text = entry[ 0 ].replace( "{version}", self.app.VERSION )
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
        self.bind( "<Return>", self.cancel )
        self.bind( "<Escape>", self.cancel )
        box.pack()

