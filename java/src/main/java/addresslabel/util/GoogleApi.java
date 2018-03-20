package addresslabel.util;

/*
import httplib2

from apiclient.discovery import build
from oauth2client.file import Storage
from oauth2client.client import OAuth2WebServerFlow
from oauth2client.tools import run_flow

#import atom.data
#import gdata.data
#import gdata.contacts.client
#import gdata.contacts.data
*/

public class GoogleApi
{
    // CLIENT_ID = "125032623953-o2etmsbq4pk5g0rki7bf446bq72cnv7t.apps.googleusercontent.com"
    public static final String CLIENT_ID = "895944253718-8l4b45m4flu7fts1iu5gv09jc2if4b54.apps.googleusercontent.com";
    // API_KEY   = "AIzaSyBscpOqaRc-kUeLYKHng3yoBrr3Uwuwsxc"
    public static final String CLIENT_SECRET = "QXKikrXpc5IUxwtJNtQq9FY-";

    public GoogleApi()
    {
        /*
        GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
        Plus plus = new Plus.builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
            .setApplicationName("Google-PlusSample/1.0")
                .build();
                */
    }


    /*
    def get_people_service( self ):
        # Set up a Flow object to be used if we need to authenticate. This
        # sample uses OAuth 2.0, and we set up the OAuth2WebServerFlow with
        # the information it needs to authenticate. Note that it is called
        # the Web Server Flow, but it can also handle the flow for
        # installed applications.
        #
        # Go to the Google API Console, open your application's
        # credentials page, and copy the client ID and client secret.
        # Then paste them into the following code.
        FLOW = OAuth2WebServerFlow(
            client_id=self.CLIENT_ID,
            client_secret=self.CLIENT_SECRET,
            scope='https://www.googleapis.com/auth/contacts.readonly',
            user_agent='addresslabel/%s' % self.app.VERSION if self.app is not None else "1.0.0" )

        # If the Credentials don't exist or are invalid, run through the
        # installed application flow. The Storage object will ensure that,
        # if successful, the good Credentials will get written back to a
        # file.
        storage = Storage( 'info.dat' )
        credentials = storage.get()
        if credentials is None or credentials.invalid == True:
            credentials = run_flow( FLOW, storage )

        # Create an httplib2.Http object to handle our HTTP requests and
        # authorize it with our good Credentials.
        http = httplib2.Http()
        http = credentials.authorize( http )

        # Build a service object for interacting with the API. To get an API key for
        # your application, visit the Google API Console
        # and look at your application's credentials page.
        people_service = build( serviceName='people', version='v1', http=http )
        return people_service


    def get_contact_list( self, email, passwd ):
        people_service = self.get_people_service()
        connections = people_service.people().connections().list( 'people/me' )
        return connections



if __name__ == "__main__":
    api = GoogleApi( None )
    api.get_contact_list( "ajmitc@gmail.com", "B0@rdG@m35" )
    */
}

