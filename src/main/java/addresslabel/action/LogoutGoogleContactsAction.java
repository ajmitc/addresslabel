package addresslabel.action;

import addresslabel.Model;
import addresslabel.view.View;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogoutGoogleContactsAction extends AbstractAction
{
    private static final Logger logger = Logger.getLogger(LogoutGoogleContactsAction.class.getName());

    private Model model;
    private View view;

    public LogoutGoogleContactsAction(Model model, View view )
    {
        super( "Logout from Google" );
        putValue( Action.SHORT_DESCRIPTION, "Logout of Google Contacts" );
        this.model = model;
        this.view = view;
    }

    public void actionPerformed( ActionEvent e )
    {
        try {
            model.getGoogleApi().logout();
            logger.info("Logged out of Google");
            view.setStatus("Logged out of Google Contacts");
            view.setGoogleUser("Not logged in");
        }
        catch (Exception ex){
            logger.log(Level.SEVERE, "Unable to log out of google", ex);
        }
    }
}

