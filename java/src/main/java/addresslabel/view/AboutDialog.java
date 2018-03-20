package addresslabel.view;

import addresslabel.Model;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.BoxLayout;

public class AboutDialog extends BaseDialog
{
    public AboutDialog( JFrame frame )
    {
        super( frame, "About", true, 400, 400 );

        String title       = "<html><h1>Address Label Easy Button</h1></html>";
        String version     = "<html><h3>Version " + Model.VERSION + "</h3></html>";
        String description = "<html>This tool is used to facilitate printing address labels.</html>";
        String author      = "<html><br/>Written by Aaron Mitchell</html>";
        String copyright   = "<html><br/>Copyright \u00A9 2017</html>";

        JLabel lblTitle = new JLabel( title );
        lblTitle.setHorizontalAlignment( JLabel.CENTER );

        JLabel lblVersion = new JLabel( version );
        lblVersion.setHorizontalAlignment( JLabel.CENTER );

        JLabel lblDesc = new JLabel( description );
        lblDesc.setHorizontalAlignment( JLabel.CENTER );

        JLabel lblAuthor = new JLabel( author );
        lblAuthor.setHorizontalAlignment( JLabel.CENTER );

        JLabel lblCopyright = new JLabel( copyright );
        lblCopyright.setHorizontalAlignment( JLabel.CENTER );

        JPanel content = new JPanel( new BoxLayout( this, BoxLayout.PAGE_AXIS ) );
        content.add( lblTitle );
        content.add( lblVersion );
        content.add( lblDesc );
        content.add( lblAuthor );
        content.add( lblCopyright );
        setContent( content );

        hideCancel();
    }
}

