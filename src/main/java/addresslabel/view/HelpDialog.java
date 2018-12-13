package addresslabel.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.BorderLayout;

public class HelpDialog extends BaseDialog
{
    public HelpDialog( JFrame parent )
    {
        super( parent, "Help", true, 400, 400 );
        JPanel content = new JPanel( new BorderLayout() );

        JLabel title = new JLabel( "Address Label Easy Button" );
        content.add( title, BorderLayout.NORTH );

        String c = "<html>How to use this program:<br/><br/>" +
        "1) Open a Contact List CSV File (ie. exported from Google Contacts using Outlook or other format)<br/>" +
        "2) Update any records by right-clicking on the record and selecting 'Edit Record'<br/>" +
        "3) Update any label templates by right-clicking on the record and selecting 'Edit Template'<br/>" +
        "4) Manually edit label display by directly changing text in Record box (this will not change the underlying CSV record)<br/>" +
        "5) Click 'Print' to send the document to the default printer.  If you need to select a different printer or printer options, export to PDF and print from the default PDF viewer application.<br/></html>";

        JLabel lblc = new JLabel( c );
        content.add( lblc, BorderLayout.CENTER );
        setContent( content );

        hideClose();
        setCancelText( "Close" );
    }
}

