

class Logger( object ):
    def __init__( self, prefix ):
        self.prefix = prefix

    def write( self, lvl, text ):
        print "[%s] %s - %s" % (lvl, self.prefix, text)


    def info( self, text ):
        self.write( "INFO", text )


    def warning( self, text ):
        self.write( "WARNING", text )


    def error( self, text ):
        self.write( "ERROR", text )


    def debug( self, text ):
        self.write( "DEBUG", text )



def init_logging():
    pass


def getLogger( prefix ):
    return Logger( prefix )

